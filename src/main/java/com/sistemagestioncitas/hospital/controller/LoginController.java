package com.sistemagestioncitas.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistemagestioncitas.hospital.model.Usuario;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String mostrarlogin(){
        return "login";
        
    }
    @GetMapping("/registro")
    public String mostrarRegistro(Model model){
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }
    @GetMapping("/recuperar")
    public String mostarrecuperar() {
        return "recuperar";
    }
    @GetMapping("/")
    public String inicio() {
        return "redirect:/usuario/perfil";
        
    }

}
