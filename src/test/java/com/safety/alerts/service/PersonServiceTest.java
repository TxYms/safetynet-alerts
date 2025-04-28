package com.safety.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    
    @Mock
    private FirestationRepository firestationRepository;

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
    
    @Test
    void testDeletePerson_Exception() {
        doThrow(new RuntimeException("Deletion failed")).when(personRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> personService.deletePerson(1L));

        verify(personRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testGetAllPersons_EmptyList() {
        when(personRepository.findAll()).thenReturn(Arrays.asList());

        List<Person> result = personService.getAllPersons();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personRepository, times(1)).findAll();
    }
    
    
    @Test
    void testSaveNewPerson() {
        Person newPerson = new Person();
        newPerson.setId(2L);
        newPerson.setFirstName("John");
        newPerson.setLastName("Smith");
        newPerson.setAddress("456 Elm St");

        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        Person result = personService.savePerson(newPerson);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("John", result.getFirstName());
        verify(personRepository, times(1)).save(newPerson);
    }
    
    @Test
    void testGetPersonById_NullId() {
        when(personRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            personService.getPersonById(null);
        });

        verify(personRepository, times(1)).findById(null);
    }
    
    @Test
    void testGetChildrenByAddress() {
        Person child = new Person();
        child.setId(1L);
        child.setFirstName("John");
        child.setLastName("Doe");
        child.setAddress("123 Main St");

        MedicalRecord record = new MedicalRecord();
        record.setBirthdate("01/01/2015"); // 10 ans

        when(personRepository.findAll()).thenReturn(List.of(child));
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        List<Map<String, Object>> result = personService.getChildrenByAddress("123 Main St");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals("Doe", result.get(0).get("lastName"));
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetPhoneNumbersByStation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("123 Main St");
        firestation.setStation(1);

        Person person = new Person();
        person.setPhone("123-456-7890");
        person.setAddress("123 Main St");

        when(firestationRepository.findAll()).thenReturn(List.of(firestation));
        when(personRepository.findAll()).thenReturn(List.of(person));

        List<Map<String, Object>> result = personService.getPhoneNumbersByStation(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123-456-7890", result.get(0).get("phone"));
        verify(firestationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
    }
    
    @Test
    void testGetPersonsAndStationByAddress() {
        Firestation firestation = new Firestation();
        firestation.setAddress("123 Main St");
        firestation.setStation(1);

        Person person = new Person();
        person.setId(1L);
        person.setLastName("Doe");
        person.setPhone("123-456-7890");
        person.setAddress("123 Main St");

        MedicalRecord record = new MedicalRecord();
        record.setBirthdate("01/01/1990"); // 34 ans
        record.setMedications(List.of("aznol:350mg"));
        record.setAllergies(List.of("nillacilan"));

        when(firestationRepository.findAll()).thenReturn(List.of(firestation));
        when(personRepository.findAll()).thenReturn(List.of(person));
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        List<Map<String, Object>> result = personService.getPersonsAndStationByAddress("123 Main St");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).get("lastName"));
        assertEquals("123-456-7890", result.get(0).get("phone"));
        assertEquals(1, result.get(0).get("stationNumber"));
        verify(firestationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetHouseholdsByStations() {
        Firestation firestation = new Firestation();
        firestation.setAddress("123 Main St");
        firestation.setStation(1);

        Person person = new Person();
        person.setId(1L);
        person.setLastName("Doe");
        person.setPhone("123-456-7890");
        person.setAddress("123 Main St");

        MedicalRecord record = new MedicalRecord();
        record.setBirthdate("01/01/1990");
        record.setMedications(List.of("aznol:350mg"));
        record.setAllergies(List.of("nillacilan"));

        when(firestationRepository.findAll()).thenReturn(List.of(firestation));
        when(personRepository.findAll()).thenReturn(List.of(person));
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        Map<String, List<Map<String, Object>>> result = personService.getHouseholdsByStations(List.of(1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("123 Main St"));
        assertEquals("Doe", result.get("123 Main St").get(0).get("lastName"));
        verify(firestationRepository, times(1)).findAll();
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetPersonInfoByLastName() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setEmail("john.doe@example.com");

        MedicalRecord record = new MedicalRecord();
        record.setBirthdate("01/01/1990");
        record.setMedications(List.of("aznol:350mg"));
        record.setAllergies(List.of("nillacilan"));

        when(personRepository.findAll()).thenReturn(List.of(person));
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        List<Map<String, Object>> result = personService.getPersonInfoByLastName("Doe");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).get("lastName"));
        assertEquals("123 Main St", result.get(0).get("address"));
        assertEquals("john.doe@example.com", result.get(0).get("email"));
        verify(personRepository, times(1)).findAll();
        verify(medicalRecordRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetCommunityEmailsByCity() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setCity("Springfield");
        person.setEmail("john.doe@example.com");

        when(personRepository.findAll()).thenReturn(List.of(person));

        List<Map<String, Object>> result = personService.getCommunityEmailsByCity("Springfield");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john.doe@example.com", result.get(0).get("email"));
        verify(personRepository, times(1)).findAll();
    }
}