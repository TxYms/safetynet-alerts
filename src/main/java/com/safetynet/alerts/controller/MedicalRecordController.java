package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicalRecords")
public class MedicalRecordController {
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private final MedicalRecordService medicalRecordService;
        
        public MedicalRecordController(MedicalRecordService medicalRecordService) {
            this.medicalRecordService = medicalRecordService;
        }
    

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        logger.info("GET /medicalRecords");
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        logger.info("GET /medicalRecords/{}", id);
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecordById(id);
        return medicalRecord.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        logger.info("POST /medicalRecords - Data: {}", medicalRecord);
        return ResponseEntity.ok(medicalRecordService.saveMedicalRecord(medicalRecord));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        logger.info("DELETE /medicalRecords/{}", id);
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}