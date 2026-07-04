package com.sistemagestioncitas.hospital.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")



public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique=true,nullable=false)
    private String cedula;
    @Column(unique=true,nullable=false)
    private String correo;
    private String contacto;
    @Column(nullable=false)
    private String password;
    @Column(nullable=false)
    private String rol;
    private  boolean activo;

    public Usuario() {
        this.activo = true;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;

    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getcedula() {
        return cedula;
    }
    public void setcedula(String cedula) {
        this.cedula = cedula;
    }

    
    public String getcorreo() {
        return correo;
    }
    public void setcorreo(String correo) {
        this.correo = correo;
    }
    
    
    public String getcontacto() {
        return contacto;
    }
    public void setcontacto(String contacto) {
        this.contacto = contacto;
    }
       
        
    public String getpassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;

    }

    
    public String getrol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;

    }

    
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
