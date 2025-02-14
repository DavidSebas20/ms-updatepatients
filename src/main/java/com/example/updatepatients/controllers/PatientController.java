package com.example.updatepatients.controllers;

import com.example.updatepatients.entity.Patient;
import com.example.updatepatients.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/update-patient/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient updatedPatient,
            @RequestParam String password,
            @RequestParam(required = false) String newPassword
    ) {
        return patientService.updatePatient(id, updatedPatient, password, Optional.ofNullable(newPassword))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Healthy");
    }
}