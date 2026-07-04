package com.sistemagestioncitas.hospital.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity


public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String cedula;
    private String correo;
    private String contato;
    private String password;
    private String rol;
    private  boolean activo;
    public Usuario() {
        this.activo = true;
    }
    public long getId() {
        return id;
    public void setId(long id) {
        this.id = id;
            
    }


}
