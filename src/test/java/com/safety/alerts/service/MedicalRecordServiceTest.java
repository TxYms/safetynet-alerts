package com.safety.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.service.MedicalRecordService;

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
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/1980");

        // 🔥 Réinitialisation des mocks avant chaque test
        Mockito.reset(medicalRecordRepository);
    }

    @Test
    void testGetAllMedicalRecords() {
        List<MedicalRecord> medicalRecords = Arrays.asList(medicalRecord);
        when(medicalRecordRepository.findAll()).thenReturn(medicalRecords);

        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();

        assertNotNull(result);
        assertFalse(result.isEmpty()); // Vérifie que la liste n'est pas vide
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findAll();
    }

    @Test
    void testGetMedicalRecordById_Found() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(medicalRecord));

        Optional<MedicalRecord> result = medicalRecordService.getMedicalRecordById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(medicalRecordRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMedicalRecordById_NotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = medicalRecordService.getMedicalRecordById(1L);

        assertFalse(result.isPresent());
        verify(medicalRecordRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveMedicalRecord() {
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecord result = medicalRecordService.saveMedicalRecord(medicalRecord);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(medicalRecordRepository, times(1)).save(medicalRecord);
    }

    @Test
    void testDeleteMedicalRecord() {
        doNothing().when(medicalRecordRepository).deleteById(1L);

        medicalRecordService.deleteMedicalRecord(1L);

        verify(medicalRecordRepository, times(1)).deleteById(1L);
    }
}