package com.safety.alerts.controller;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setFirstName("Jane");
        person.setLastName("Doe");
        person.setAddress("123 Main St");

        // 🔥 Réinitialisation des mocks avant chaque test
        Mockito.reset(personService);
    }

    @Test
    void testGetAllPersons() {
        List<Person> persons = Arrays.asList(person);
        when(personService.getAllPersons()).thenReturn(persons);

        ResponseEntity<List<Person>> response = personController.getAllPersons();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, response.getBody().size());
        verify(personService, times(1)).getAllPersons();
    }

    @Test
    void testGetPersonById_Found() {
        when(personService.getPersonById(1L)).thenReturn(Optional.of(person));

        ResponseEntity<Person> response = personController.getPersonById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(personService, times(1)).getPersonById(1L);
    }

    @Test
    void testGetPersonById_NotFound() {
        when(personService.getPersonById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Person> response = personController.getPersonById(1L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(personService, times(1)).getPersonById(1L);
    }

    @Test
    void testCreatePerson() {
        when(personService.savePerson(any(Person.class))).thenReturn(person);

        ResponseEntity<Person> response = personController.createPerson(person);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        verify(personService, times(1)).savePerson(any(Person.class));
    }

    @Test
    void testDeletePerson() {
        doNothing().when(personService).deletePerson(1L);

        ResponseEntity<Void> response = personController.deletePerson(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(personService, times(1)).deletePerson(1L);
    }
    
    @Test
    void testGetChildrenByAddressController() {
        Map<String, Object> child = new HashMap<>();
        child.put("firstName", "John");
        child.put("lastName", "Doe");
        child.put("age", 10);

        List<Map<String, Object>> children = List.of(child);

        when(personService.getChildrenByAddress("123 Main St")).thenReturn(children);

        ResponseEntity<List<Map<String, Object>>> response = personController.getChildrenByAddress("123 Main St");

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).get("firstName"));
        assertEquals("Doe", response.getBody().get(0).get("lastName"));
        verify(personService, times(1)).getChildrenByAddress("123 Main St");
    }
    
    @Test
    void testGetPhoneNumbersByStationController() {
        Map<String, Object> phoneData = new HashMap<>();
        phoneData.put("phone", "123-456-7890");

        List<Map<String, Object>> phones = List.of(phoneData);

        when(personService.getPhoneNumbersByStation(1)).thenReturn(phones);

        ResponseEntity<List<Map<String, Object>>> response = personController.getPhoneNumbersByStation(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("123-456-7890", response.getBody().get(0).get("phone"));
        verify(personService, times(1)).getPhoneNumbersByStation(1);
    }
    
    @Test
    void testGetPersonsAndStationByAddressController() {
        Map<String, Object> personData = new HashMap<>();
        personData.put("lastName", "Doe");
        personData.put("phone", "123-456-7890");
        personData.put("age", 34);
        personData.put("medications", List.of("aznol:350mg"));
        personData.put("allergies", List.of("nillacilan"));
        personData.put("stationNumber", 1);

        List<Map<String, Object>> residents = List.of(personData);

        when(personService.getPersonsAndStationByAddress("123 Main St")).thenReturn(residents);

        ResponseEntity<List<Map<String, Object>>> response = personController.getPersonsAndStationByAddress("123 Main St");

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Doe", response.getBody().get(0).get("lastName"));
        verify(personService, times(1)).getPersonsAndStationByAddress("123 Main St");
    }
    
    @Test
    void testGetHouseholdsByStationsController() {
        Map<String, Object> resident = new HashMap<>();
        resident.put("lastName", "Doe");
        resident.put("phone", "123-456-7890");
        resident.put("age", 34);
        resident.put("medications", List.of("aznol:350mg"));
        resident.put("allergies", List.of("nillacilan"));

        Map<String, List<Map<String, Object>>> households = new HashMap<>();
        households.put("123 Main St", List.of(resident));

        when(personService.getHouseholdsByStations(List.of(1))).thenReturn(households);

        ResponseEntity<Map<String, List<Map<String, Object>>>> response = personController.getHouseholdsByStations(List.of(1));

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("123 Main St"));
        assertEquals("Doe", response.getBody().get("123 Main St").get(0).get("lastName"));
        verify(personService, times(1)).getHouseholdsByStations(List.of(1));
    }
    
    @Test
    void testGetPersonInfoByLastNameController() {
        Map<String, Object> personInfo = new HashMap<>();
        personInfo.put("lastName", "Doe");
        personInfo.put("address", "123 Main St");
        personInfo.put("email", "john.doe@example.com");
        personInfo.put("age", 34);
        personInfo.put("medications", List.of("aznol:350mg"));
        personInfo.put("allergies", List.of("nillacilan"));

        List<Map<String, Object>> personsInfo = List.of(personInfo);

        when(personService.getPersonInfoByLastName("Doe")).thenReturn(personsInfo);

        ResponseEntity<List<Map<String, Object>>> response = personController.getPersonInfoByLastName("Doe");

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Doe", response.getBody().get(0).get("lastName"));
        verify(personService, times(1)).getPersonInfoByLastName("Doe");
    }
    
    @Test
    void testGetCommunityEmailsByCityController() {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("email", "john.doe@example.com");

        List<Map<String, Object>> emails = List.of(emailData);

        when(personService.getCommunityEmailsByCity("Springfield")).thenReturn(emails);

        ResponseEntity<List<Map<String, Object>>> response = personController.getCommunityEmailsByCity("Springfield");

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("john.doe@example.com", response.getBody().get(0).get("email"));
        verify(personService, times(1)).getCommunityEmailsByCity("Springfield");
    }
}