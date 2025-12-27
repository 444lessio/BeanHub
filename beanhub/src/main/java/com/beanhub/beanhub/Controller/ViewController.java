package com.beanhub.beanhub.Controller;

import com.beanhub.beanhub.model.Distributor; // Assicurati di importare il Model
import com.beanhub.beanhub.repository.DistributorRepository; // Assicurati di importare il Repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Importante per passare dati all'HTML
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private DistributorRepository distributorRepository; // Ci serve per leggere il DB

    /*@GetMapping("/")
        public String getHome() {
            return "Customer/login";
    }*/

    /*@GetMapping("/customer/signup")
        public String getCustomerSignup() {
            return "Customer/signup";
        }
    */
   

    /*@GetMapping("/customer/main")
    public String getClientMain() {
        return "Customer/main";
    }*/



    @GetMapping("/distributor")
    public String getDistributorScreen(Model model, @RequestParam(required = false) String id) {
        
        String finalId = "UNKNOWN"; // Valore di default se il DB è vuoto

        // 1. Se l'utente specifica un ID nell'URL (es. ?id=D-55), usiamo quello
        if (id != null && !id.isEmpty()) {
            finalId = id;
        } 
        else {
            // 2. ALTRIMENTI: Prendiamo il PRIMO distributore che troviamo nel database
            List<Distributor> allDistributors = distributorRepository.findAll();
            if (!allDistributors.isEmpty()) {
                finalId = allDistributors.get(0).getId(); // Prende l'ID del primo (quello che hai caricato)
            }
        }

        model.addAttribute("machineId", finalId);
        
        // Passiamo anche l'ID per visualizzarlo a video (opzionale)
        model.addAttribute("machineIdDisplay", finalId);

        return "Screen_distributor/index";
    }



    @GetMapping("/attendant/login")
    public String getAttendantLogin() {
        return "Maintenance_attendant/login";
    }


    @GetMapping("/attendant/main")
    public String getAttendantMain() {
        return "Maintenance_attendant/main";
    }


    /*@GetMapping("/System_manager/login")
    public String getSystem_managerLogin() {
        return "System_manager/login";
    }
*/
    /*@GetMapping("/System_manager/dashboard")
    public String getSystem_managerDashboard() {
        return "System_manager/dashboard";
    }

    @GetMapping("/System_manager/machines")
    public String getSystem_managerMachines() {
        return "System_manager/machines";
    }

    @GetMapping("/System_manager/attendants")
    public String getSystem_managerAttendants() {
        return "System_manager/attendants";
    }
    */
}
