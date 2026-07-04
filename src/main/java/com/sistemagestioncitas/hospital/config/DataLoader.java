package com.sistemagestioncitas.hospital.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
  
    private final PasswordEncoder passwordEncoder;
    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder= passwordEncoder;

    }
    @Override
    public void run(String...args) {
        if(!usuarioRepository.existsByCorreo("gusmorji@gmail.com")){
            Usuario admin = new Usuario ();
            admin.setnombre("Administrador");
            admin.setcorreo("gusmorji@gmail.com");
            admin.setcedula("110890207");
            admin.setcontacto("8528-0465");
            admin.setpassword(passwordEncoder.encode("admin123"));
            admin.setrol("ADMIN");
            admin.setactivo(true);
            usuarioRepository.save(admin);
            System.out.println("USUARIO ADMIN CREADO: gusmorji@gmail.com/ admin123");

        }
    }



}
