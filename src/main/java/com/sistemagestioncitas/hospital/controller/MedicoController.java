package com.sistemagestioncitas.hospital.controller;

import java.text.AttributedCharacterIterator.Attribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.services.EspacioCitaService;
import com.sistemagestioncitas.hospital.services.MedicoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/medico")

public class MedicoController {
    @Autowired
    private MedicoService medicoService;
    @Autowired
    private EspacioCitaService espacioCitaService;
    //Listar Medicos
    @GetMapping("/")
    public String  listarnMedicos(Model model) {
        model.addAttribute("medicos", medicoService.listarTodos());
        return "medicos/listaMedicos";
    }
    // Formulario nuevo medico
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medico", new Medico());
        return "medicos/formularioMedico";
    }
    
    // Editar Medico 
    @GetMapping("/editar/{id}")
    public String editarMedico(@PathVariable Long id, Model model){
        model.addAttribute("medico", medicoService.obtenerPorId(id).orElse(new Medico()));
        return "medicos/formularioMedico";

    }
    // Guardar Medico
    @PostMapping("guardar")
    public String guardarMedico(@ModelAttribute Medico medico) {
        medicoService.guardar(medico);
        return "redirect:/medicos/";
    }
    // Eliminar Medico
    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.eliminar(id);
        return "redirect:/medico/";
    }
    //Espacios Medicos
    @GetMapping("/{id}/espacios")
    public String verEspacios(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.obtenerPorId(id).orElse(null));
        model.addAttribute("espacios", espacioCitaService.listarPorMedico(id));
        return "espacio/listaEspacios";  
    }

    //Nuevos Espacios
    @GetMapping("/espacio/nuevo/{medicoId}")
    public String nuevoEspacio(@PathVariable Long medicoId, Model model){
        model.addAttribute("espacio", new EspacioCita());
        model.addAttribute("medicoId", medicoId);
        return "espacio/formularioEspacio";
    }
    // Guarda Espacio
    @PostMapping("/espacio/guardar")
    public String guardarEspacio(@ModelAttribute EspacioCita espacio, @RequestParam Long medicoId) {
        Medico medico = medicoService.obtenerPorId(medicoId).orElseThrow(() -> new RuntimeException("MEDICO NO ENCONTRADO"));
        espacio.setMedico(medico);
        espacioCitaService.guardar(espacio);
        return "redirect:/medicos/" + medicoId + "/espacios";


}

}
