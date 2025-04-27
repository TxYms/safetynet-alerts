package com.safety.alerts.controller;

import com.safetynet.alerts.controller.MedicalRecordController;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
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
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/1980");

        // 🔥 Réinitialisation des mocks avant chaque test
        Mockito.reset(medicalRecordService);
    }

    @Test
    void testGetAllMedicalRecords() {
        List<MedicalRecord> medicalRecords = Arrays.asList(medicalRecord);
        when(medicalRecordService.getAllMedicalRecords()).thenReturn(medicalRecords);

        ResponseEntity<List<MedicalRecord>> response = medicalRecordController.getAllMedicalRecords();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, response.getBody().size());
        verify(medicalRecordService, times(1)).getAllMedicalRecords();
    }

    @Test
    void testGetMedicalRecordById_Found() {
        when(medicalRecordService.getMedicalRecordById(1L)).thenReturn(Optional.of(medicalRecord));

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(medicalRecordService, times(1)).getMedicalRecordById(1L);
    }

    @Test
    void testGetMedicalRecordById_NotFound() {
        when(medicalRecordService.getMedicalRecordById(1L)).thenReturn(Optional.empty());

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordById(1L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(medicalRecordService, times(1)).getMedicalRecordById(1L);
    }

    @Test
    void testCreateMedicalRecord() {
        when(medicalRecordService.saveMedicalRecord(any(MedicalRecord.class))).thenReturn(medicalRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.createMedicalRecord(medicalRecord);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        verify(medicalRecordService, times(1)).saveMedicalRecord(any(MedicalRecord.class));
    }

    @Test
    void testDeleteMedicalRecord() {
        doNothing().when(medicalRecordService).deleteMedicalRecord(1L);

        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecord(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(medicalRecordService, times(1)).deleteMedicalRecord(1L);
    }
}