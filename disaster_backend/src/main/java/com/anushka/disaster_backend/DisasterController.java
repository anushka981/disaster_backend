package com.anushka.disaster_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class DisasterController {

    @Autowired
    private DisasterRepository repo;

    @Autowired
    private HelpRequestRepository helpRepo;

    @GetMapping("/hello")
    public String sayHello() {
        return "Backend is working ";
    }

    @PostMapping("/report")
    public String submitReport(@RequestBody DisasterReport report) {
        report.setStatus("Pending");
        report.setVolunteer("");
        repo.save(report);

        System.out.println("===== New Disaster Report =====");
        System.out.println("Disaster Type: " + report.getDisasterType());
        System.out.println("Location: " + report.getLocation());
        System.out.println("Description: " + report.getDescription());
        System.out.println("Status: " + report.getStatus());
        System.out.println("Volunteer: " + report.getVolunteer());

        return "Report submitted successfully";
    }

    @GetMapping("/reports")
    public List<DisasterReport> getAllReports() {
        return repo.findAll();
    }

    @PostMapping("/help")
    public String submitHelpRequest(@RequestBody HelpRequest helpRequest) {
        helpRepo.save(helpRequest);

        System.out.println("===== New Help Request =====");
        System.out.println("Help Type: " + helpRequest.getHelpType());
        System.out.println("Location: " + helpRequest.getLocation());
        System.out.println("Description: " + helpRequest.getDescription());

        return "Help request submitted successfully";
    }

    @GetMapping("/help")
    public List<HelpRequest> getAllHelpRequests() {
        return helpRepo.findAll();
    }

    @PutMapping("/report/{id}/status/{status}")
    public String updateStatus(@PathVariable String id, @PathVariable String status) {
        DisasterReport report = repo.findById(id).orElse(null);

        if (report != null) {
            report.setStatus(status);
            repo.save(report);
            return "Status updated successfully";
        } else {
            return "Report not found";
        }
    }

    @PutMapping("/report/{id}/volunteer/{name}")
    public String assignVolunteer(@PathVariable String id, @PathVariable String name) {
        DisasterReport report = repo.findById(id).orElse(null);

        if (report != null) {
            report.setVolunteer(name);
            repo.save(report);
            return "Volunteer assigned successfully";
        } else {
            return "Report not found";
        }
    }
}