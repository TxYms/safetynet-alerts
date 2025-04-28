package com.safetynet.alerts.model;

import jakarta.persistence.*;

// Represente une caserne de pompier dans l'application
@Entity
public class Firestation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private int station;

    // Constructeur vide requis pas JPA
    public Firestation() {
    }

    public Firestation(Long id, String address, int station) {
        this.id = id;
        this.address = address;
        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Firestation{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", station=" + station +
                '}';
    }
}