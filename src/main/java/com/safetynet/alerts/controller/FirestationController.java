package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/firestations")
public class FirestationController {
    private static final Logger logger = LoggerFactory.getLogger(FirestationController.class);
    private final FirestationService firestationService;

    public FirestationController(FirestationService firestationService) {
        this.firestationService = firestationService;
    }

    // retourne une liste de toute les firestations 
    @GetMapping
    public ResponseEntity<List<Firestation>> getAllFirestations() {
        logger.info("GET /firestations");
        return ResponseEntity.ok(firestationService.getAllFirestations());
    }

    // recupere une firestation par son ID 
    @GetMapping("/{id}")
    public ResponseEntity<Firestation> getFirestationById(@PathVariable Long id) {
        logger.info("GET /firestations/{}", id);
        Optional<Firestation> firestation = firestationService.getFirestationById(id);
        return firestation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // recoit une nouvelle firestation et la sauvegarde
    @PostMapping
    public ResponseEntity<Firestation> createFirestation(@RequestBody Firestation firestation) {
        logger.info("POST /firestations - Data: {}", firestation);
        return ResponseEntity.ok(firestationService.saveFirestation(firestation));
    }

    // supprime une firstation a partir de son ID 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFirestation(@PathVariable Long id) {
        logger.info("DELETE /firestations/{}", id);
        firestationService.deleteFirestation(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/firestation")
    public ResponseEntity<List<Map<String, Object>>> getPersonsCoveredByStation(@RequestParam int stationNumber) {
        logger.info("GET /firestation?stationNumber={}", stationNumber);
        List<Map<String, Object>> response = firestationService.getPersonsCoveredByStation(stationNumber);
        return ResponseEntity.ok(response);
    }
}
