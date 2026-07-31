package com.anushka.disaster_backend;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@Validated
public class DisasterController {
    private static final Set<String> REPORT_STATUSES = Set.of("PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED");
    private final DisasterRepository reports;
    private final HelpRequestRepository helpRequests;
    private final VolunteerRepository volunteers;
    private final FileStorageService files;

    public DisasterController(DisasterRepository reports, HelpRequestRepository helpRequests,
                              VolunteerRepository volunteers, FileStorageService files) {
        this.reports = reports;
        this.helpRequests = helpRequests;
        this.volunteers = volunteers;
        this.files = files;
    }

    @GetMapping("/hello")
    public String sayHello() { return "Backend is working"; }

    @PostMapping("/report")
    public ResponseEntity<DisasterReport> submitReport(
            @RequestParam @NotBlank String disasterType, @RequestParam @NotBlank String location,
            @RequestParam @NotBlank String description,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        DisasterReport report = new DisasterReport();
        report.setDisasterType(disasterType.trim());
        report.setLocation(location.trim());
        report.setDescription(description.trim());
        report.setStatus("PENDING");
        report.setVolunteer("");
        report.setImageUrl(image == null ? null : files.store(image));
        report.setCreatedAt(Instant.now());
        report.setUpdatedAt(Instant.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(reports.save(report));
    }

    @GetMapping("/reports")
    public List<DisasterReport> getAllReports() { return reports.findAll(); }

    @GetMapping("/report/{id}")
    public ResponseEntity<DisasterReport> getReport(@PathVariable String id) {
        return reports.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reports/assigned/{name}")
    public List<DisasterReport> getAssignedReports(@PathVariable String name) { return reports.findByVolunteer(name); }

    @GetMapping("/report/search/location")
    public List<DisasterReport> searchByLocation(@RequestParam @NotBlank String location) { return reports.findByLocationContainingIgnoreCase(location.trim()); }

    @GetMapping("/report/search/type")
    public List<DisasterReport> searchByType(@RequestParam @NotBlank String disasterType) { return reports.findByDisasterTypeContainingIgnoreCase(disasterType.trim()); }

    @PutMapping("/report/{id}/status/{status}")
    public ResponseEntity<DisasterReport> updateStatus(@PathVariable String id, @PathVariable String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!REPORT_STATUSES.contains(normalized)) throw new IllegalArgumentException("Unsupported report status");
        return reports.findById(id).map(report -> {
            report.setStatus(normalized); report.setUpdatedAt(Instant.now()); return ResponseEntity.ok(reports.save(report));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/report/{id}/volunteer/{username}")
    public ResponseEntity<DisasterReport> assignVolunteer(@PathVariable String id, @PathVariable String username) {
        Volunteer volunteer = volunteers.findByUsernameIgnoreCase(username).orElse(null);
        if (volunteer == null || !"VOLUNTEER".equalsIgnoreCase(volunteer.getRole())) throw new IllegalArgumentException("Volunteer does not exist");
        return reports.findById(id).map(report -> {
            report.setVolunteer(volunteer.getUsername()); report.setStatus("ASSIGNED"); report.setUpdatedAt(Instant.now());
            return ResponseEntity.ok(reports.save(report));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/report/{id}/delete")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        if (!reports.existsById(id)) return ResponseEntity.notFound().build();
        reports.deleteById(id); return ResponseEntity.noContent().build();
    }

    @PostMapping("/help")
    public ResponseEntity<HelpRequest> submitHelpRequest(@RequestBody HelpRequest helpRequest) {
        if (blank(helpRequest.getHelpType()) || blank(helpRequest.getLocation()) || blank(helpRequest.getDescription())) {
            throw new IllegalArgumentException("helpType, location, and description are required");
        }
        helpRequest.setStatus("OPEN"); helpRequest.setCreatedAt(Instant.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(helpRequests.save(helpRequest));
    }

    @GetMapping("/help")
    public List<HelpRequest> getAllHelpRequests() { return helpRequests.findAll(); }

    @GetMapping("/help/{id}")
    public ResponseEntity<HelpRequest> getHelpRequest(@PathVariable String id) {
        return helpRequests.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/help/{id}/delete")
    public ResponseEntity<Void> deleteHelpRequest(@PathVariable String id) {
        if (!helpRequests.existsById(id)) return ResponseEntity.notFound().build();
        helpRequests.deleteById(id); return ResponseEntity.noContent().build();
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
}
