package com.beanhub.beanhub.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
        public String getHome() {
            return "Customer/login";
    }

    @GetMapping("/customer/signup")
        public String getCustomerSignup() {
            return "Customer/signup";
        }

    @GetMapping("/customer/main")
    public String getClientMain() {
        return "Customer/main";
    }



    @GetMapping("/distributor") 
    public String getDistributorScreen() {
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


    @GetMapping("/System_manager/login")
    public String getSystem_managerLogin() {
        return "System_manager/login";
    }

    @GetMapping("/System_manager/dashboard")
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
    
}
