package com.beanhub.beanhub.Controller;

import com.beanhub.beanhub.model.Distributor;
import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.DistributorRepository;
import com.beanhub.beanhub.repository.DrinkRepository;
import com.beanhub.beanhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/customer")
public class ClientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    // --- LOGIN ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "Customer/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               HttpSession session,
                               Model model) {
        User user = userRepository.findById(username).orElse(null);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/customer/main";
        } else {
            model.addAttribute("error", "Utente non trovato! Riprova.");
            return "Customer/login";
        }
    }

    // --- HOME PAGE ---
    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        // 1. Recupero utente dalla sessione
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/customer/login";
        }

        // 2. Ricarico l'utente dal DB per avere il credito sempre aggiornato
        User dbUser = userRepository.findById(sessionUser.getUsername()).orElse(null);

        if (dbUser != null) {
            session.setAttribute("user", dbUser);

            model.addAttribute("username", dbUser.getUsername());

            // Gestione Credito (null safety)
            BigDecimal currentCredit = dbUser.getCredit() != null ? dbUser.getCredit() : BigDecimal.ZERO;
            model.addAttribute("credit", currentCredit);

            // NOTA: Ho rimosso "drinks" perché nello Scenario B il cliente NON vede i drink sul telefono,
            // ma solo sullo schermo del distributore.
            
            model.addAttribute("user", dbUser);
        }

        // Gestione Distributore connesso
        String connectedDistributor = (String) session.getAttribute("connectedDistributor");
        if (connectedDistributor != null) {
            model.addAttribute("connectedMsg", "Connesso al distributore: " + connectedDistributor);
            model.addAttribute("isConnected", true);
        } else {
            model.addAttribute("isConnected", false);
        }

        return "Customer/main";
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Se faccio logout e sono connesso, è buona norma disconnettersi anche dal distributore
        disconnectFromDistributor(session); 
        
        session.invalidate();
        return "redirect:/customer/login";
    }

    // --- TOP UP (Ricarica Credito) ---
    @PostMapping("/topup")
    public String topUpCredit(@RequestParam("amount") BigDecimal amount,
                              HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        
        if (sessionUser != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            User dbUser = userRepository.findById(sessionUser.getUsername()).orElse(null);
            
            if (dbUser != null) {
                BigDecimal oldCredit = dbUser.getCredit() != null ? dbUser.getCredit() : BigDecimal.ZERO;
                BigDecimal newCredit = oldCredit.add(amount);
                
                dbUser.setCredit(newCredit);
                userRepository.save(dbUser);
                
                session.setAttribute("user", dbUser);
            }
        }
        
        return "redirect:/customer/main";
    }

    // --- CONNESSIONE DISTRIBUTORE (AGGIORNATO) ---
    @PostMapping("/connect")
    public String connectToDistributor(@RequestParam("distributorId") String distributorId, 
                                       HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/customer/login";

        // 1. Cerco il distributore nel DB
        Distributor distributor = distributorRepository.findById(distributorId).orElse(null);
        
        if (distributor != null) {
            // 2. Registro l'utente CONNESSO nel database del distributore
            // Questo è fondamentale affinché lo schermo fisico sappia chi c'è davanti!
            distributor.setConnectedUser(user.getUsername());
            distributorRepository.save(distributor);

            // 3. Salvo in sessione per l'interfaccia cliente
            session.setAttribute("connectedDistributor", distributorId);
        } else {
            // Opzionale: gestire errore distributore non trovato
            return "redirect:/customer/main?error=distributor_not_found";
        }
        
        return "redirect:/customer/main";
    }

    // --- DISCONNESSIONE DISTRIBUTORE (AGGIORNATO) ---
    @PostMapping("/disconnect")
    public String disconnectFromDistributor(HttpSession session) {
        String distId = (String) session.getAttribute("connectedDistributor");
        
        if (distId != null) {
            // Cerco il distributore e rimuovo l'utente connesso
            Distributor distributor = distributorRepository.findById(distId).orElse(null);
            if (distributor != null) {
                distributor.setConnectedUser(null); // Libero il distributore
                distributorRepository.save(distributor);
            }
        }

        session.removeAttribute("connectedDistributor");
        return "redirect:/customer/main";
    }

    // --- REGISTRAZIONE (SIGNUP) ---
    
    // Mostra la pagina di registrazione (se non è già gestita dal ViewController)
    @GetMapping("/signup")
    public String showSignupPage() {
        return "Customer/signup";
    }

    // Gestisce i dati inviati dal form di registrazione
    @PostMapping("/signup")
    public String processSignup(@RequestParam("username") String username, 
                                // @RequestParam("password") String password, // Scommenta se il form ha la password
                                Model model) {
        
        // 1. VERIFICA ESISTENZA: Controlla se l'utente esiste già nel DB
        if (userRepository.existsById(username)) {
            model.addAttribute("error", "Questo username è già in uso. Scegline un altro.");
            return "Customer/signup"; // Ricarica la pagina con l'errore
        }

        // 2. CREAZIONE NUOVO UTENTE
        User newUser = new User();
        newUser.setUsername(username);
        // newUser.setPassword(password); // Scommenta se gestisci la password
        newUser.setCredit(java.math.BigDecimal.ZERO); // Il credito parte da 0

        // 3. SALVATAGGIO SU DB
        userRepository.save(newUser);

        // 4. Redirect al login per entrare
        return "redirect:/customer/login";
    }
}