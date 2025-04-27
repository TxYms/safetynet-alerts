package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);
    private final MedicalRecordRepository medicalRecordRepository;
    
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
    	this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        logger.info("Fetching all medical records");
        try {
            List<MedicalRecord> records = medicalRecordRepository.findAll();
            logger.debug("Number of medical records fetched: {}", records.size());
            return records;
        } catch (Exception e) {
            logger.error("Error fetching medical records: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<MedicalRecord> getMedicalRecordById(Long id) {
        logger.info("Fetching medical record with id: {}", id);
        try {
            Optional<MedicalRecord> result = medicalRecordRepository.findById(id);
            logger.debug("Medical record found: {}", result.isPresent());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching medical record with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
        logger.info("Saving medical record: {}", medicalRecord);
        try {
            logger.debug("Validating medical record before save");
            MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
            logger.debug("Medical record saved with id: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error saving medical record: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteMedicalRecord(Long id) {
        logger.info("Deleting medical record with id: {}", id);
        try {
            medicalRecordRepository.deleteById(id);
            logger.debug("Medical record with id {} deleted", id);
        } catch (Exception e) {
            logger.error("Error deleting medical record with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}