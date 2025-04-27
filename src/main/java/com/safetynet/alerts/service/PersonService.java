package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    
    public PersonService(PersonRepository personServiceRepository) {
    	this.personRepository = personServiceRepository;
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
}