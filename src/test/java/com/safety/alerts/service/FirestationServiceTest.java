package com.safety.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.service.FirestationService;

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
class FirestationServiceTest {

    @Mock
    private FirestationRepository firestationRepository;

    @InjectMocks
    private FirestationService firestationService;

    private Firestation firestation;

    @BeforeEach
    void setUp() {
        firestation = new Firestation();
        firestation.setId(1L);
        firestation.setAddress("123 Main St");
        firestation.setStation(1);

        // 🔥 Réinitialisation des mocks avant chaque test
        Mockito.reset(firestationRepository);
    }

    @Test
    void testGetAllFirestations() {
        List<Firestation> firestations = Arrays.asList(firestation);
        when(firestationRepository.findAll()).thenReturn(firestations);

        List<Firestation> result = firestationService.getAllFirestations();

        assertNotNull(result);
        assertFalse(result.isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, result.size());
        verify(firestationRepository, times(1)).findAll();
    }

    @Test
    void testGetFirestationById_Found() {
        when(firestationRepository.findById(1L)).thenReturn(Optional.of(firestation));

        Optional<Firestation> result = firestationService.getFirestationById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(firestationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetFirestationById_NotFound() {
        when(firestationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Firestation> result = firestationService.getFirestationById(1L);

        assertFalse(result.isPresent());
        verify(firestationRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveFirestation() {
        when(firestationRepository.save(any(Firestation.class))).thenReturn(firestation);

        Firestation result = firestationService.saveFirestation(firestation);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(firestationRepository, times(1)).save(firestation);
    }

    @Test
    void testDeleteFirestation() {
        doNothing().when(firestationRepository).deleteById(1L);

        firestationService.deleteFirestation(1L);

        verify(firestationRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteFirestation_Exception() {
        doThrow(new RuntimeException("Deletion failed")).when(firestationRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> firestationService.deleteFirestation(1L));

        verify(firestationRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testGetAllFirestations_EmptyList() {
        when(firestationRepository.findAll()).thenReturn(Arrays.asList());

        List<Firestation> result = firestationService.getAllFirestations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(firestationRepository, times(1)).findAll();
    }
    
    @Test
    void testSaveNewFirestation() {
        Firestation newFirestation = new Firestation();
        newFirestation.setId(2L);
        newFirestation.setAddress("456 Elm St");
        newFirestation.setStation(2);

        when(firestationRepository.save(any(Firestation.class))).thenReturn(newFirestation);

        Firestation result = firestationService.saveFirestation(newFirestation);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("456 Elm St", result.getAddress());
        verify(firestationRepository, times(1)).save(newFirestation);
    }
    
    @Test
    void testGetFirestationById_NullId() {
        when(firestationRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            firestationService.getFirestationById(null);
        });

        verify(firestationRepository, times(1)).findById(null);
    }
    
    @Test
    void testGetPersonsCoveredByStation() {
        // Création d'un mock de la Firestation
        Firestation firestation1 = new Firestation();
        firestation1.setId(1L);
        firestation1.setAddress("123 Main St");
        firestation1.setStation(1);

        Firestation firestation2 = new Firestation();
        firestation2.setId(2L);
        firestation2.setAddress("456 Elm St");
        firestation2.setStation(2);

        // Simulation du repository
        when(firestationRepository.findAll()).thenReturn(Arrays.asList(firestation1, firestation2));

        // Appel de la méthode réelle
        List<Map<String, Object>> result = firestationService.getPersonsCoveredByStation(1);

        // Vérifications
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123 Main St", result.get(0).get("address"));
        assertEquals(1, result.get(0).get("station"));
        verify(firestationRepository, times(1)).findAll();
    }
}