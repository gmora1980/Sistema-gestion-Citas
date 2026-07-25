package com.sistemagestioncitas.hospital.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.sistemagestioncitas.hospital.model.Cita;
import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.services.CitaService;
import com.sistemagestioncitas.hospital.services.EspacioCitaService;
import com.sistemagestioncitas.hospital.services.MedicoService;
import com.sistemagestioncitas.hospital.services.UsuarioService;

import aj.org.objectweb.asm.commons.TryCatchBlockSorter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class citaController {
    @Autowired
    private CitaService citaService;
    @Autowired
    private MedicoService medicoService;
    @Autowired
    private EspacioCitaService espacioCitaService;
    @Autowired
    private UsuarioService usuarioService; // Se necesita para obtener el usuario real de la base de datos

    /**
     * Metodo auxiliar para obtener el usuario autenticado de manera segura
     */
    private Usuario obtenerUsuarioActual(Principal principal) {
        return usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la sesion"));

    }

    /**
     * Listar Citas del usuario actual( filtro opcional por estado)
     */
    @GetMapping("/cita/mis-citas")
    public String listarMisCitas(Principal principal, Model model,
            @RequestParam(required = false) String estado) {
        Usuario usuarioActual = obtenerUsuarioActual(principal);
        List<Cita> citas;
        if (estado != null && !estado.isEmpty()) {
            citas = citaService.getCitasPorUsuarioYEstado(usuarioActual.getId(), estado);
        } else {
            citas = citaService.getCitasPorUsuario(usuarioActual.getId());

        }
        model.addAttribute("citas", citas);
        return "cita/misCitas";
    }

    /**
     * Formulario para crear nuevas citas
     */
    @GetMapping("/cita/nueva")
    public String mostrarFormularioNuevaCita(Model model, Principal principal) {
        Usuario usuarioActual = obtenerUsuarioActual(principal);
        model.addAttribute("medicos", medicoService.listarTodos());
        model.addAttribute("usuarioId", usuarioActual.getId());// Para el campo oculto del form
        return "cita/formularioNuevaCita";

    }

    /**
     * Endpoint AJAX : Obtener espacios DISPONIBLES de un medico especifico
     */
    @GetMapping("/cita/espacios/{medicoId}")
    @ResponseBody
    public List<EspacioCita> getEspaciosPorMedico(@PathVariable Long medicoId) {
        return espacioCitaService.listarDisponiblePorMedico(medicoId);
    }

    /**
     * Crear nueva cita (RESERVAS) usuario normal y admin
     */
    @PostMapping("/cita/guardar")
    public String guardarCita(@RequestParam Long espacioCitaId,
            @RequestParam(required=false) Long usuarioIdSeleccionado,// Nuevo parametro opcional
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioActual = obtenerUsuarioActual(principal);
            Usuario usuarioDestino;
            //1 - Determinar para quien es la cita
            if(usuarioActual.getRol().equals("ADMIN")){
                if(usuarioIdSeleccionado==null){ 
                    throw new RuntimeException(" El administrador debe seleccionar un paciente");
                }
                usuarioDestino=usuarioService.obtenerPorId(usuarioIdSeleccionado)
                .orElseThrow(()->new RuntimeException("Paciente seleccionado no encontrado"));
            }else{
                //Usuario normal: Usa su propio ID(Seguridad RN8)
                usuarioDestino= usuarioActual;
            }
            //2 Crear la cita.  El service se hace cargo de las RN
            Cita cita = citaService.crearCita(usuarioDestino.getId(), espacioCitaId, usuarioDestino);
            // 3- Mensaje y redireccion segun el rol
            if(usuarioActual.getRol().equals("ADMIN")){
                redirectAttributes.addFlashAttribute("exito", "Cita reservada exitosamente para el paciente seleccionado");
            return "redirect:/cita/admin";

            } else {
                redirectAttributes.addFlashAttribute("exito", "Cita reservada exitosamente");
            return "redirect:/cita/mis-citas";
            }
            

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reservar:" + e.getMessage());
            return "redirect:/cita/nueva";

        }

    }

    /**
     * Cancelar Cita (USUARIO)
     * 
     */
    @GetMapping("/cita/cancelar{id}")
    public String cancelarCitaUsuario(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try{
            Usuario usuarioActual = obtenerUsuarioActual (principal);
            // El service valida que la cita y fecha no hayan pasado
            citaService.cancelarCitaUsuario(id, usuarioActual);
             redirectAttributes.addFlashAttribute("exito", "Cita cancelada");

        } catch (Exception e ){
            redirectAttributes.addFlashAttribute("error", "Error al reservar:" + e.getMessage());

        }
        return "redirect:/cita/nueva";

    }
    /**
     * Panel de administracion de citas para el administrador(Aplicacion de Filtros)
     */
    @GetMapping("/cita/admin")
    public String adminCitas(Model model, @RequestParam(required = false)String estado,@RequestParam(required = false)Long medicoId){
        List<Cita> cita;
        if(estado!=null && !estado.isEmpty()){
            cita=citaService.getCitasPorEstado(estado);
        }else if(medicoId!=null){
            cita=citaService.getCitasPorMedico(medicoId);
        }else {
            cita=citaService.getAllCitas();
        }
        model.addAttribute("citas", cita);
        model.addAttribute("medicos",medicoService.listarTodos());
        // Datos Opcionales para estadisticas en Dashboard
        model.addAttribute("totalPendientes",citaService.getCitasPorEstado("PENDIENTE").size());
        model.addAttribute("totalConfirmadas",citaService.getCitasPorEstado("CONFIRMDAS").size());
        model.addAttribute("totalPresenes",citaService.getCitasPorEstado("PRESENTE").size());
        model.addAttribute("totalAusentes",citaService.getCitasPorEstado("AUSENTE").size());
        model.addAttribute("totalCanceladas",citaService.getCitasPorEstado("CANCELADA").size());
        return"cita/admin/cita";

    }
    /**
     * Confirmar Citas
     */
    @GetMapping("cita/admin/confirmar/{id}")
    public String confirmarCitaAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try{
            citaService.confirmarCita(id);
            redirectAttributes.addFlashAttribute("exito", "Cita confirmada de forma Exitosa");

        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Error al confirmar la cita:" + e.getMessage());
        }
        return "redirect:/cita/admin";
        
    }
    /**
     * Cancelar Cita Admin
     */
    @PostMapping("/cita/admin/cancelar/{id}")
    public String cancelarCitaAdmin(@PathVariable Long id,
                                    @RequestParam(required=false) String motivo,
                                    RedirectAttributes redirectAttributes){
    try{
            citaService.cancelarCitaAdmin(id,motivo);
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada exitosamente. Espacio liberado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la cita: " + e.getMessage());
        }
        return "redirect:/citas/admin";
    }    
    
    
}
    


