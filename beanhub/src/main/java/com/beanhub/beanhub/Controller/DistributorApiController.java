package com.beanhub.beanhub.Controller;

import com.beanhub.beanhub.dto.PurchaseRequest;
import com.beanhub.beanhub.model.Distributor;
import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.DistributorRepository;
import com.beanhub.beanhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/distributor")
public class DistributorApiController {

    @Autowired
    private UserRepository userService; 

    @Autowired
    private DistributorRepository distributorRepository;

    // ==========================================
    //  PUNTO 6: LISTA XML DI TUTTI I DISTRIBUTORI
    // ==========================================
    // URL: http://localhost:8080/api/distributor/all
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_XML_VALUE)
    public List<Distributor> getAllDistributorsXml() {
        return distributorRepository.findAll();
    }

    // --- ENDPOINT PER LO SCHERMO: CHIEDE "CHI C'È?" ---
    @GetMapping("/{distributorId}/status")
    public ResponseEntity<?> getDistributorStatus(@PathVariable String distributorId) {
        Distributor distributor = distributorRepository.findById(distributorId).orElse(null);

        if (distributor == null) return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        String connectedUsername = distributor.getConnectedUser();

        if (connectedUsername != null) {
            // C'è un utente connesso!
            User user = userService.findById(connectedUsername).orElse(null);
            if (user != null) {
                response.put("connected", true);
                response.put("username", user.getUsername());
                response.put("credit", user.getCredit());
            }
        } else {
            // Nessuno connesso
            response.put("connected", false);
        }

        return ResponseEntity.ok(response);
    }

    // --- ENDPOINT PER ACQUISTO DALLO SCHERMO ---
    @PostMapping("/{distributorId}/buy")
    public ResponseEntity<?> buyDrinkViaId(@PathVariable String distributorId, @RequestBody PurchaseRequest request) {
        
        Distributor distributor = distributorRepository.findById(distributorId).orElse(null);
        if (distributor == null) return ResponseEntity.badRequest().body("Distributore non trovato");

        // Chi è connesso?
        String username = distributor.getConnectedUser();
        if (username == null) return ResponseEntity.status(401).body("Nessun utente connesso al distributore!");

        User user = userService.findById(username).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Utente non valido");

        // Controllo Credito
        if (user.getCredit().compareTo(request.getPrice()) < 0) {
            return ResponseEntity.badRequest().body("Credito insufficiente!");
        }

        // Scala Credito
        user.setCredit(user.getCredit().subtract(request.getPrice()));
        userService.save(user);

        // Aggiorna Risposta
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("newCredit", user.getCredit());
        response.put("message", "Erogazione: " + request.getDrinkName());

        return ResponseEntity.ok(response);
    }


    // --- INCOLLA QUESTO DENTRO DistributorApiController.java ---

    // Endpoint per il segnale "Heartbeat" (sono vivo!)
    @PostMapping("/{distributorId}/heartbeat")
    public ResponseEntity<?> sendHeartbeat(@PathVariable String distributorId) {
        
        // Cerca il distributore
        Distributor distributor = distributorRepository.findById(distributorId).orElse(null);
        
        if (distributor == null) {
            return ResponseEntity.notFound().build();
        }

        // Aggiorna l'orario all'istante attuale
        distributor.setLastHeartbeat(java.time.LocalDateTime.now());
        distributorRepository.save(distributor);

        return ResponseEntity.ok("Heartbeat ricevuto");
    }
}