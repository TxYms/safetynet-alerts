package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private final PersonService personService;
    
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        logger.info("GET /persons");
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        logger.info("GET /persons/{}", id);
        Optional<Person> person = personService.getPersonById(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        logger.info("POST /persons - Data: {}", person);
        return ResponseEntity.ok(personService.savePerson(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        logger.info("DELETE /persons/{}", id);
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/childAlert")
    public ResponseEntity<List<Map<String, Object>>> getChildrenByAddress(@RequestParam String address) {
        logger.info("GET /childAlert?address={}", address);
        List<Map<String, Object>> children = personService.getChildrenByAddress(address);

        if (children.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonList(Map.of("message", "Aucun enfant trouvé à cette adresse.")));
        }

        return ResponseEntity.ok(children);
    }
    
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<Map<String, Object>>> getPhoneNumbersByStation(@RequestParam int firestation) {
        logger.info("GET /phoneAlert?firestation={}", firestation);
        List<Map<String, Object>> phones = personService.getPhoneNumbersByStation(firestation);

        if (phones.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonList(Map.of("message", "Aucun numéro trouvé pour cette station.")));
        }

        return ResponseEntity.ok(phones);
    }
    
    @GetMapping("/fire")
    public ResponseEntity<List<Map<String, Object>>> getPersonsAndStationByAddress(@RequestParam String address) {
        logger.info("GET /fire?address={}", address);
        List<Map<String, Object>> response = personService.getPersonsAndStationByAddress(address);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getHouseholdsByStations(@RequestParam List<Integer> stations) {
        logger.info("GET /flood/stations?stations={}", stations);
        Map<String, List<Map<String, Object>>> response = personService.getHouseholdsByStations(stations);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/personInfo")
    public ResponseEntity<List<Map<String, Object>>> getPersonInfoByLastName(@RequestParam String lastName) {
        logger.info("GET /personInfo?lastName={}", lastName);
        List<Map<String, Object>> response = personService.getPersonInfoByLastName(lastName);

        if (response.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonList(Map.of("message", "Aucune personne trouvée avec ce nom.")));
        }

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/communityEmail")
    public ResponseEntity<List<Map<String, Object>>> getCommunityEmailsByCity(@RequestParam String city) {
        logger.info("GET /communityEmail?city={}", city);
        List<Map<String, Object>> emails = personService.getCommunityEmailsByCity(city);

        if (emails.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonList(Map.of("message", "Aucun email trouvé pour cette ville.")));
        }

        return ResponseEntity.ok(emails);
    }
}