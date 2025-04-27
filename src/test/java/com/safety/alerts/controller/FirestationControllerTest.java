package com.safety.alerts.controller;

import com.safetynet.alerts.controller.FirestationController;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;
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
class FirestationControllerTest {

    @Mock
    private FirestationService firestationService;

    @InjectMocks
    private FirestationController firestationController;

    private Firestation firestation;

    @BeforeEach
    void setUp() {
        firestation = new Firestation();
        firestation.setId(1L);
        firestation.setAddress("123 Main St");
        firestation.setStation(1);

        // 🔥 Correction : Réinitialisation du mock à chaque test
        Mockito.reset(firestationService);
    }

    @Test
    void testGetAllFirestations() {
        List<Firestation> firestations = Arrays.asList(firestation);
        when(firestationService.getAllFirestations()).thenReturn(firestations);

        ResponseEntity<List<Firestation>> response = firestationController.getAllFirestations();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, response.getBody().size());
        verify(firestationService, times(1)).getAllFirestations(); // Vérifie l'appel
    }

    @Test
    void testGetFirestationById_Found() {
        when(firestationService.getFirestationById(1L)).thenReturn(Optional.of(firestation));

        ResponseEntity<Firestation> response = firestationController.getFirestationById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(firestationService, times(1)).getFirestationById(1L);
    }

    @Test
    void testGetFirestationById_NotFound() {
        when(firestationService.getFirestationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Firestation> response = firestationController.getFirestationById(1L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(firestationService, times(1)).getFirestationById(1L);
    }

    @Test
    void testCreateFirestation() {
        when(firestationService.saveFirestation(any(Firestation.class))).thenReturn(firestation);

        ResponseEntity<Firestation> response = firestationController.createFirestation(firestation);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        verify(firestationService, times(1)).saveFirestation(any(Firestation.class));
    }

    @Test
    void testDeleteFirestation() {
        doNothing().when(firestationService).deleteFirestation(1L);

        ResponseEntity<Void> response = firestationController.deleteFirestation(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(firestationService, times(1)).deleteFirestation(1L);
    }
}