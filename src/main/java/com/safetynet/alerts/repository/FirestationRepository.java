package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Firestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// l'inteface manipule des entités de type Firestation 
// l'interface FirestationRepository hérite de JpaRepository
@Repository
public interface FirestationRepository extends JpaRepository<Firestation, Long> {
}