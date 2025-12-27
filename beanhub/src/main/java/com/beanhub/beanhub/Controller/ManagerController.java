package com.beanhub.beanhub.Controller; // Nota: package in minuscolo per convenzione

import com.beanhub.beanhub.model.Distributor;
import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.DistributorRepository;
import com.beanhub.beanhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/System_manager")
public class ManagerController {

    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // URL del servizio Jakarta EE
    private final String MONITORING_API_URL = "http://localhost:8081/monitoring-service/api/distributors";

    public ManagerController(DistributorRepository distributorRepository,
                             UserRepository userRepository,
                             RestTemplate restTemplate) {
        this.distributorRepository = distributorRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    // ==========================================
    // --- NAVIGAZIONE BASE & LOGOUT ---
    // ==========================================

    @GetMapping("/login")
    public String showManagerLoginPage() {
        return "System_manager/login";
    }

    @GetMapping("/dashboard")
    public String showManagerDashboard(Model model, Principal principal) {
        model.addAttribute("username", principal != null ? principal.getName() : "Manager");
        return "System_manager/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/System_manager/login";
    }

    // ==========================================
    // --- GESTIONE DISTRIBUTORI (MACHINES) ---
    // ==========================================

    @GetMapping("/repair-db")
    public String repairDatabase() {
        if (distributorRepository.count() == 0) {
            // Coordinate di esempio (Milano)
            Distributor d1 = new Distributor("D-01", "Main Entrance", "ACTIVE", 45.4642, 9.1900, 100, 100, 100, 100);
            Distributor d2 = new Distributor("D-02", "Relax Room", "MAINTENANCE", 45.4650, 9.1910, 50, 50, 50, 50);

            distributorRepository.save(d1);
            distributorRepository.save(d2);

            try {
                syncWithJakarta(d1);
                syncWithJakarta(d2);
            } catch (Exception e) {
                System.out.println("Jakarta non raggiungibile durante il repair (normale se spento).");
            }
        }
        return "redirect:/System_manager/machines";
    }

    @GetMapping("/machines")
    public String showMachinesPage(Model model, Principal principal) {
        model.addAttribute("username", principal != null ? principal.getName() : "Manager");
        model.addAttribute("machines", distributorRepository.findAll());
        model.addAttribute("newMachine", new Distributor());
        return "System_manager/machines";
    }

    @PostMapping("/machines/add")
    public String addMachine(@ModelAttribute("newMachine") Distributor distributor) {
        // Valori di default
        distributor.setCoffeeLevel(100);
        distributor.setMilkLevel(100);
        distributor.setSugarLevel(100);
        distributor.setCupsLevel(100);
        //distributor.setStatus("ACTIVE"); // MAIUSCOLO per coerenza

        // 1. Salvataggio Locale
        distributorRepository.save(distributor);

        // 2. Sincronizzazione Jakarta EE
        try {
            syncWithJakarta(distributor);
            System.out.println("✅ Macchina aggiunta e sincronizzata!");
        } catch (Exception e) {
            System.err.println("❌ Errore sincronizzazione add: " + e.getMessage());
        }

        return "redirect:/System_manager/machines";
    }

    @GetMapping("/machines/edit/{id}")
    public String showEditMachineForm(@PathVariable("id") String id, Model model) {
        Distributor distributor = distributorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid machine Id:" + id));
        model.addAttribute("machine", distributor);
        return "System_manager/edit_machine";
    }

    @PostMapping("/machines/update")
    public String updateMachine(@ModelAttribute("machine") Distributor distributor) {
        // 1. Salvataggio locale
        distributorRepository.save(distributor);

        // 2. Sincronizzazione Jakarta (SCOMMENTATO!)
        try {
            syncWithJakarta(distributor);
            System.out.println("✅ UPDATE inviato a Jakarta: " + distributor.getStatus());
        } catch (Exception e) {
            System.err.println("❌ Errore sincronizzazione Jakarta durante Edit: " + e.getMessage());
        }

        return "redirect:/System_manager/machines";
    }

    @GetMapping("/machines/delete/{id}")
    public String deleteMachine(@PathVariable("id") String id) {
        distributorRepository.deleteById(id);

        try {
            restTemplate.delete(MONITORING_API_URL + "/" + id);
            System.out.println("✅ Eliminazione sincronizzata su Jakarta EE.");
        } catch (Exception e) {
            System.err.println("❌ Errore cancellazione remota: " + e.getMessage());
        }

        return "redirect:/System_manager/machines";
    }

    // --- METODO HELPER PER LA SINCRONIZZAZIONE ---
    private void syncWithJakarta(Distributor d) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", d.getId());
        data.put("location", d.getLocation());
        data.put("status", d.getStatus().toUpperCase());
        
        // ORA USIAMO I DATI REALI DELL'OGGETTO, NON HARDCODED
        data.put("latitude", d.getLatitude());  
        data.put("longitude", d.getLongitude());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        
        // Nota: Jakarta si aspetta un POST anche per gli aggiornamenti (se usa em.merge)
        restTemplate.postForEntity(MONITORING_API_URL, request, String.class);
    }

    // ==========================================
    // --- GESTIONE TECNICI (ATTENDANTS) ---
    // ==========================================

    @GetMapping("/attendants")
    public String showAttendantsPage(Model model, Principal principal) {
        model.addAttribute("username", principal != null ? principal.getName() : "Manager");
        
        // Filtriamo per ROLE_MAINTENANCE
        List<User> attendants = userRepository.findAll().stream()
                .filter(u -> "ROLE_MAINTENANCE".equalsIgnoreCase(u.getRole())) 
                .collect(Collectors.toList());

        model.addAttribute("attendants", attendants);
        model.addAttribute("newAttendant", new User());
        return "System_manager/attendants";
    }

    @PostMapping("/attendants/add")
    public String addAttendant(@ModelAttribute("newAttendant") User user) {
        // Assegniamo il ruolo corretto con prefisso
        user.setRole("ROLE_MAINTENANCE"); 
        user.setCredit(BigDecimal.ZERO);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("password");
        }
        userRepository.save(user);
        return "redirect:/System_manager/attendants";
    }

    @GetMapping("/attendants/delete/{username}")
    public String deleteAttendant(@PathVariable("username") String username) {
        userRepository.deleteById(username);
        return "redirect:/System_manager/attendants";
    }

    // ==========================================
    // --- XML ATTENDANTS ---
    // ==========================================

    @GetMapping(value = "/api/attendants/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getAttendantsXml() {
        // Filtriamo per ROLE_MAINTENANCE anche qui
        List<User> attendants = userRepository.findAll().stream()
                .filter(u -> "ROLE_MAINTENANCE".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<attendants>\n");
        for (User u : attendants) {
            xml.append("  <attendant>\n");
            xml.append("    <username>").append(u.getUsername()).append("</username>\n");
            xml.append("    <role>").append(u.getRole()).append("</role>\n");
            xml.append("  </attendant>\n");
        }
        xml.append("</attendants>");
        return xml.toString();
    }
}