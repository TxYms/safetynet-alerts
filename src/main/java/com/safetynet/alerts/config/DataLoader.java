package com.safetynet.alerts.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final PersonRepository personRepository;
    private final FirestationRepository firestationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    // Constructeur qui injecte les dépendances 
    public DataLoader(PersonRepository personRepository, FirestationRepository firestationRepository,
                      MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.firestationRepository = firestationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // Charge les données initiales depuis le fichier JSON
    @EventListener(ContextRefreshedEvent.class)
    public void loadData() {
        logger.info("🚀 Chargement des données depuis data.json...");
        
        // instanciation de ObjetMapper pour manipuler JSON avec Jackson 
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Ouverture puis lecture du fichier JSON
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Charger les personnes
            List<Person> persons = objectMapper.readValue(rootNode.get("persons").toString(),
                    new TypeReference<List<Person>>() {});
            personRepository.saveAll(persons);
            logger.info("✅ {} personnes chargées", persons.size());

            // Charger les casernes de pompiers
            List<Firestation> firestations = objectMapper.readValue(rootNode.get("firestations").toString(),
                    new TypeReference<List<Firestation>>() {});
            firestationRepository.saveAll(firestations);
            logger.info("✅ {} casernes de pompiers chargées", firestations.size());

            // Charger les dossiers médicaux
            List<MedicalRecord> medicalRecords = objectMapper.readValue(rootNode.get("medicalrecords").toString(),
                    new TypeReference<List<MedicalRecord>>() {});
            medicalRecordRepository.saveAll(medicalRecords);
            logger.info("✅ {} dossiers médicaux chargés", medicalRecords.size());

            logger.info("🎉 Toutes les données ont été chargées avec succès !");
            
            // gestion des erreurs
        } catch (Exception e) {
            logger.error("❌ Erreur lors du chargement des données : {}", e.getMessage(), e);
        }
    }
}