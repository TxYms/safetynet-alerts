package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FirestationRepository firestationRepository;
    
    public PersonService(PersonRepository personRepository, MedicalRecordRepository medicalRecordRepository, FirestationRepository firestationRepository) {
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.firestationRepository = firestationRepository;
    }

    public List<Person> getAllPersons() {
        logger.info("Fetching all persons");
        try {
            List<Person> persons = personRepository.findAll();
            logger.debug("Number of persons fetched: {}", persons.size());
            return persons;
        } catch (Exception e) {
            logger.error("Error fetching persons: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Person> getPersonById(Long id) {
        logger.info("Fetching person with id: {}", id);
        try {
            Optional<Person> result = personRepository.findById(id);
            logger.debug("Person found: {}", result.isPresent());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching person with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public Person savePerson(Person person) {
        logger.info("Saving person: {}", person);
        try {
            logger.debug("Validating person before save");
            Person saved = personRepository.save(person);
            logger.debug("Person saved successfully with id: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error saving person: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deletePerson(Long id) {
        logger.info("Deleting person with id: {}", id);
        try {
            personRepository.deleteById(id);
            logger.debug("Person with id {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting person with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Map<String, Object>> getChildrenByAddress(String address) {
        List<Person> personsAtAddress = personRepository.findAll().stream()
                .filter(p -> address.equals(p.getAddress()))
                .collect(Collectors.toList());

        List<Map<String, Object>> childrenInfo = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate today = LocalDate.now();

        for (Person person : personsAtAddress) {
            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(person.getId());

            if (recordOpt.isPresent()) {
                MedicalRecord record = recordOpt.get();
                int age = 0;
                try {
                    LocalDate birthDate = LocalDate.parse(record.getBirthdate(), formatter);
                    age = Period.between(birthDate, today).getYears();
                } catch (Exception ignored) {}

                if (age <= 18) {
                    Map<String, Object> childData = new HashMap<>();
                    childData.put("firstName", person.getFirstName());
                    childData.put("lastName", person.getLastName());
                    childData.put("age", age);
                    childrenInfo.add(childData);
                }
            }
        }

        return childrenInfo;
    }
    
    public List<Map<String, Object>> getPhoneNumbersByStation(int stationNumber) {
        List<String> addresses = firestationRepository.findAll().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        return personRepository.findAll().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(p -> {
                    Map<String, Object> phoneData = new HashMap<>();
                    phoneData.put("phone", p.getPhone());
                    return phoneData;
                })
                .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getPersonsAndStationByAddress(String address) {
        List<Map<String, Object>> personsInfo = new ArrayList<>();

        Optional<Firestation> firestationOpt = firestationRepository.findAll().stream()
                .filter(f -> address.equals(f.getAddress()))
                .findFirst();

        if (firestationOpt.isEmpty()) {
            return personsInfo; // retourne une liste vide si aucune caserne
        }

        int stationNumber = firestationOpt.get().getStation();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate today = LocalDate.now();

        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> address.equals(p.getAddress()))
                .collect(Collectors.toList());

        for (Person p : persons) {
            Map<String, Object> personData = new HashMap<>();

            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(p.getId());
            int age = 0;
            List<String> medications = new ArrayList<>();
            List<String> allergies = new ArrayList<>();

            if (recordOpt.isPresent()) {
                MedicalRecord record = recordOpt.get();
                try {
                    LocalDate birthDate = LocalDate.parse(record.getBirthdate(), formatter);
                    age = Period.between(birthDate, today).getYears();
                } catch (Exception ignored) {}

                medications = record.getMedications();
                allergies = record.getAllergies();
            }

            personData.put("lastName", p.getLastName());
            personData.put("phone", p.getPhone());
            personData.put("age", age);
            personData.put("medications", medications);
            personData.put("allergies", allergies);
            personData.put("stationNumber", stationNumber);

            personsInfo.add(personData);
        }

        return personsInfo;
    }
    
    public Map<String, List<Map<String, Object>>> getHouseholdsByStations(List<Integer> stationNumbers) {
        Map<String, List<Map<String, Object>>> households = new HashMap<>();

        // Trouver toutes les adresses couvertes par les stations demandées
        List<String> addresses = firestationRepository.findAll().stream()
                .filter(f -> stationNumbers.contains(f.getStation()))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate today = LocalDate.now();

        // Pour chaque adresse
        for (String address : addresses) {
            List<Map<String, Object>> personsAtAddress = new ArrayList<>();

            List<Person> persons = personRepository.findAll().stream()
                    .filter(p -> address.equals(p.getAddress()))
                    .collect(Collectors.toList());

            for (Person person : persons) {
                Map<String, Object> personData = new HashMap<>();

                Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(person.getId());
                int age = 0;
                List<String> medications = new ArrayList<>();
                List<String> allergies = new ArrayList<>();

                if (recordOpt.isPresent()) {
                    MedicalRecord record = recordOpt.get();
                    try {
                        LocalDate birthDate = LocalDate.parse(record.getBirthdate(), formatter);
                        age = Period.between(birthDate, today).getYears();
                    } catch (Exception ignored) {}

                    medications = record.getMedications();
                    allergies = record.getAllergies();
                }

                personData.put("lastName", person.getLastName());
                personData.put("phone", person.getPhone());
                personData.put("age", age);
                personData.put("medications", medications);
                personData.put("allergies", allergies);

                personsAtAddress.add(personData);
            }

            households.put(address, personsAtAddress);
        }

        return households;
    }
    
    public List<Map<String, Object>> getPersonInfoByLastName(String lastName) {
        List<Map<String, Object>> personsInfo = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate today = LocalDate.now();

        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> lastName.equalsIgnoreCase(p.getLastName()))
                .collect(Collectors.toList());

        for (Person p : persons) {
            Map<String, Object> personData = new HashMap<>();

            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(p.getId());
            int age = 0;
            List<String> medications = new ArrayList<>();
            List<String> allergies = new ArrayList<>();

            if (recordOpt.isPresent()) {
                MedicalRecord record = recordOpt.get();
                try {
                    LocalDate birthDate = LocalDate.parse(record.getBirthdate(), formatter);
                    age = Period.between(birthDate, today).getYears();
                } catch (Exception ignored) {}

                medications = record.getMedications();
                allergies = record.getAllergies();
            }

            personData.put("lastName", p.getLastName());
            personData.put("address", p.getAddress());
            personData.put("email", p.getEmail());
            personData.put("age", age);
            personData.put("medications", medications);
            personData.put("allergies", allergies);

            personsInfo.add(personData);
        }

        return personsInfo;
    }
    
    public List<Map<String, Object>> getCommunityEmailsByCity(String city) {
        List<Map<String, Object>> emails = new ArrayList<>();

        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> city.equalsIgnoreCase(p.getCity()))
                .collect(Collectors.toList());

        for (Person person : persons) {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("email", person.getEmail());
            emails.add(emailData);
        }

        return emails;
    }
}