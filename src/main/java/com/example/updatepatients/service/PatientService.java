package com.example.updatepatients.service;

import com.example.updatepatients.entity.*;
import com.example.updatepatients.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Value("${hash.service.url}")
    private String PASSWORD_HASH_URL;

    @Value("${verify.service.url}")
    private String PASSWORD_VERIFY_URL;

    public Optional<Patient> updatePatient(Long id, Patient updatedPatient, String password, Optional<String> newPassword) {
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();


            boolean passwordMatches = verifyPassword(password, patient.getPasswordHash());
            if (!passwordMatches) {
                return Optional.empty();
            }


            if (updatedPatient.getFirstName() != null) patient.setFirstName(updatedPatient.getFirstName());
            if (updatedPatient.getLastName() != null) patient.setLastName(updatedPatient.getLastName());
            if (updatedPatient.getAddress() != null) patient.setAddress(updatedPatient.getAddress());
            if (updatedPatient.getPhone() != null) patient.setPhone(updatedPatient.getPhone());
            if (updatedPatient.getEmail() != null) patient.setEmail(updatedPatient.getEmail());


            newPassword.ifPresent(pass -> patient.setPasswordHash(hashPassword(pass)));

            return Optional.of(patientRepository.save(patient));
        }
        return Optional.empty();
    }

    private boolean verifyPassword(String password, String hash) {
        RestTemplate restTemplate = new RestTemplate();

        VerifyRequest verifyRequest = new VerifyRequest(password, hash);

        try {
            HttpEntity<VerifyRequest> request = new HttpEntity<>(verifyRequest);
            ResponseEntity<VerifyResponse> response = restTemplate.postForEntity(PASSWORD_VERIFY_URL, request, VerifyResponse.class);
            return response.getBody() != null && response.getBody().isValid();
        } catch (Exception e) {
            e.printStackTrace();
            return password.equals(hash);
        }
    }

    private String hashPassword(String password) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<HashResponse> response = restTemplate.postForEntity(PASSWORD_HASH_URL, new HashRequest(password), HashResponse.class);
            return response.getBody() != null ? response.getBody().getHash() : password;
        } catch (Exception e) {
            return password;
        }
    }

}