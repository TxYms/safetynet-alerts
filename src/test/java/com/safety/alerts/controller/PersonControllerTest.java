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
import java.util.List;
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
}