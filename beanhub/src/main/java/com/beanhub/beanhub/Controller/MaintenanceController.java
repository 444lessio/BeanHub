package com.beanhub.beanhub.Controller; // O com.beanhub.beanhub.Controller se usi la maiuscola

import com.beanhub.beanhub.model.Distributor;
import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.DistributorRepository;
import com.beanhub.beanhub.repository.UserRepository; // Usiamo il repository diretto per sicurezza

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/maintenance") 
public class MaintenanceController {

    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository; 

    // Costruttore: Iniettiamo UserRepository invece di Service per coerenza con gli altri file
    public MaintenanceController(DistributorRepository distributorRepository, UserRepository userRepository) {
        this.distributorRepository = distributorRepository;
        this.userRepository = userRepository;
    }

    // --- LOGIN PAGE ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "Maintenance_attendant/login"; 
    }

    // --- LOGIN PROCESS ---
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               // La password la riceviamo ma la ignoriamo, o la rendiamo opzionale
                               @RequestParam(value = "password", required = false) String password,
                               HttpSession session,
                               Model model) {

        // 1. Cerchiamo l'utente nel DB
        User user = userRepository.findById(username).orElse(null);

        // 2. LOGICA: Se l'utente esiste (La password viene IGNORATA come richiesto)
        if (user != null) {

            // 3. CONTROLLO RUOLO (Cruciale: deve essere un tecnico!)
            String role = (user.getRole() != null) ? user.getRole().toLowerCase() : "";

            if (role.equals("technician") || role.equals("maintenance") || role.equals("tech")) {
                // Accesso consentito
                session.setAttribute("loggedTech", user);
                return "redirect:/maintenance/main";
            } else {
                // Utente esiste ma è un Cliente, non un Tecnico
                model.addAttribute("error", "Accesso negato: Non hai i permessi di manutentore.");
                return "Maintenance_attendant/login";
            }

        } else {
            // Utente non trovato
            model.addAttribute("error", "Username non trovato.");
            return "Maintenance_attendant/login";
        }
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Meglio invalidate() per pulire tutto
        return "redirect:/maintenance/login";
    }

    // --- DASHBOARD ---
    @GetMapping("/main")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedTech");
        
        if (user == null) {
            return "redirect:/maintenance/login";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("distributors", distributorRepository.findAll());

        return "Maintenance_attendant/main";
    }

    // --- REFILL ---
    @PostMapping("/refill")
    public String refillMachine(@RequestParam("id") String id, HttpSession session) {
        if (session.getAttribute("loggedTech") == null) return "redirect:/maintenance/login";

        Distributor d = distributorRepository.findById(id).orElse(null);
        if (d != null) {
            d.setCoffeeLevel(100);
            d.setMilkLevel(100);
            d.setSugarLevel(100);
            d.setCupsLevel(100);
            
            // Se non è rotto ("maintenance"), torna attivo dopo il refill
            if(!d.getStatus().equals("maintenance")) {
                d.setStatus("active");
            }
            
            distributorRepository.save(d);
        }
        return "redirect:/maintenance/main"; 
    }

    // --- REPAIR ---
    @PostMapping("/repair")
    public String repairMachine(@RequestParam("id") String id, HttpSession session) {
        if (session.getAttribute("loggedTech") == null) return "redirect:/maintenance/login";

        Distributor d = distributorRepository.findById(id).orElse(null);
        if (d != null) {
            d.setStatus("active"); // Riparato -> Attivo
            distributorRepository.save(d);
        }
        return "redirect:/maintenance/main"; 
    }
}