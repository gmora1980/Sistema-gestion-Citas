package com.sistemagestioncitas.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.services.UsuarioService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller

public class RegistroController {
    @Autowired
    private UsuarioService usuarioService;
    @PostMapping("/registar")
    public String registarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes){ {
        //validar
        if (usuarioService.existeCorreo(usuario.getcorreo())) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado");
            return "redirect:/registro";
       }
       if (usuarioService.existeCedula(usuario.getcedula())) {
            redirectAttributes.addFlashAttribute("error", "La cédula ya está registrada");
            return "redirect:/registro";
        }
         /// Asignacion rol por defecto
         usuario.setrol("ROL_USUARIO");
         usuario.setactivo(true);
         usuarioService.guardar(usuario);
         redirectAttributes.addFlashAttribute("Exito", "El resgitro fue existoso, por favor inicia sesion");
            return "redirect:/login";        
        
    }
    
    

}
}