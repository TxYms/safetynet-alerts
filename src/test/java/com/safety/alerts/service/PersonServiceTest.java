package com.safety.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.service.PersonService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setFirstName("Jane");
        person.setLastName("Doe");
        person.setAddress("123 Main St");

        // 🔥 Réinitialisation des mocks avant chaque test
        Mockito.reset(personRepository);
    }

    @Test
    void testGetAllPersons() {
        List<Person> persons = Arrays.asList(person);
        when(personRepository.findAll()).thenReturn(persons);

        List<Person> result = personService.getAllPersons();

        assertNotNull(result);
        assertFalse(result.isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, result.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetPersonById_Found() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<Person> result = personService.getPersonById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPersonById_NotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Person> result = personService.getPersonById(1L);

        assertFalse(result.isPresent());
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void testSavePerson() {
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person result = personService.savePerson(person);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testDeletePerson() {
        doNothing().when(personRepository).deleteById(1L);

        personService.deletePerson(1L);

        verify(personRepository, times(1)).deleteById(1L);
    }
}