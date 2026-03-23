package com.anushka.disaster_backend;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class DisasterController{

    //for test only
    @GetMapping("/hello")
    public String sayHello() {
        return "Backend is working ";
    }

    //  Report Disaster API
    @PostMapping("/report")
    public String submitReport(@RequestBody DisasterReport report) {

        System.out.println("===== New Disaster Report =====");
        System.out.println("Disaster Type: " + report.getDisasterType());
        System.out.println("Location: " + report.getLocation());
        System.out.println("Description: " + report.getDescription());

        return "Report submitted successfully";
    }
}


