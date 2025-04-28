package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FirestationService {
    private static final Logger logger = LoggerFactory.getLogger(FirestationService.class);
    private final FirestationRepository firestationRepository;
    
    public FirestationService(FirestationRepository firestationRepository) {
        this.firestationRepository = firestationRepository;
    }

 // méthode pour récupérer toutes les casernes
    public List<Firestation> getAllFirestations() {
        logger.info("Fetching all firestations");
        try {
            List<Firestation> firestations = firestationRepository.findAll();
            logger.debug("Number of firestations fetched: {}", firestations.size());
            return firestations;
        } catch (Exception e) {
            logger.error("Error fetching all firestations: {}", e.getMessage(), e);
            throw e;
        }
    }

    // recherche une firestation par son id
    public Optional<Firestation> getFirestationById(Long id) {
        logger.info("Fetching firestation with id: {}", id);
        try {
            Optional<Firestation> result = firestationRepository.findById(id);
            logger.debug("Firestation found: {}", result.isPresent());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching firestation with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // enregistre ou met à jour une firestation
    public Firestation saveFirestation(Firestation firestation) {
        logger.info("Saving firestation: {}", firestation);
        try {
            logger.debug("Validating firestation before save");
            // ici tu pourrais ajouter une logique métier de validation
            Firestation saved = firestationRepository.save(firestation);
            logger.debug("Firestation saved successfully with id: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error saving firestation: {}", e.getMessage(), e);
            throw e;
        }
    }

    // supprime une firestation selon son id
    public void deleteFirestation(Long id) {
        logger.info("Deleting firestation with id: {}", id);
        try {
            firestationRepository.deleteById(id);
            logger.debug("Firestation with id {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting firestation with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public List<Map<String, Object>> getPersonsCoveredByStation(int stationNumber) {
        List<Firestation> firestations = firestationRepository.findAll().stream()
                .filter(f -> f.getStation() == stationNumber)
                .collect(Collectors.toList());

        List<Map<String, Object>> personsInfo = new ArrayList<>();

        for (Firestation firestation : firestations) {
            Map<String, Object> personData = new HashMap<>();
            personData.put("address", firestation.getAddress());
            personData.put("station", firestation.getStation());
            personsInfo.add(personData);
        }

        return personsInfo;
    }
}