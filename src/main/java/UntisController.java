// src/main/java/UntisController.java (REST Controller)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/untis")
@CrossOrigin(origins = "*") // Für n8n Zugriff
public class UntisController {

    @Autowired
    private UntisService untisService;

    @PostMapping("/timetable")
    public ResponseEntity<Map<String, Object>> getTimetable(@RequestBody UntisRequest request) {

        // Validierung
        if (request.getUsername() == null || request.getPassword() == null ||
                request.getServer() == null || request.getSchoolName() == null) {

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Fehlende Parameter: username, password, server, schoolName sind erforderlich");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = untisService.getTimetableData(
                request.getUsername(),
                request.getPassword(),
                request.getServer(),
                request.getSchoolName(),
                request.getDays()
        );

        if ((Boolean) result.getOrDefault("success", false)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Untis WebService");
        status.put("version", "1.0");
        return ResponseEntity.ok(status);
    }

    // Test-Endpoint (basierend auf Ihrer ursprünglichen UntisTest main Methode)
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection(@RequestBody UntisRequest request) {
        Map<String, Object> testResult = new HashMap<>();

        try {
            // Nur Login testen (wie Ihr ursprünglicher Code)
            var result = untisService.getTimetableData(
                    request.getUsername(),
                    request.getPassword(),
                    request.getServer(),
                    request.getSchoolName(),
                    1
            );

            if ((Boolean) result.get("success")) {
                testResult.put("loginTest", "SUCCESS");
                testResult.put("schoolname", result.get("schoolname"));
                testResult.put("personId", result.get("personId"));
                testResult.put("totalClasses", result.get("totalClasses"));
            } else {
                testResult.put("loginTest", "FAILED");
                testResult.put("error", result.get("error"));
            }

        } catch (Exception e) {
            testResult.put("loginTest", "FAILED");
            testResult.put("error", e.getMessage());
        }

        return ResponseEntity.ok(testResult);
    }
}