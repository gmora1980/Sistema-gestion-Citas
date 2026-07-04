package com.sistemagestioncitas.hospital.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util. List;


@Entity
@Table(name= "medicos")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String especialidad;
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EspacioCita> espacioCitas;
   


    public Medico() {

    }
    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public String getnombre() {
        return nombre;
    }

    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    public String getespecialidad() {
        return especialidad;
    }

    public void setespecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
