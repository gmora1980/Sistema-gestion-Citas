package com.sistemagestioncitas.hospital.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemagestioncitas.hospital.model.Cita;
import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.CitaRepository;
import com.sistemagestioncitas.hospital.repository.EspacioCitaRepository;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;
    @Autowired 
    private EspacioCitaRepository espacioCitaRepository;
    /**
     * RN 1 y RN 2 Crean la cita con validacion de Disponilidad
     * Transccional ( Transactional) para evitar doble reserva
     */
    @Transactional
    public Cita crearCita(Long usuarioId, Long espacioCitaId,Usuario usuario){
       //Validacion de espacion existente
       EspacioCita espacio = espacioCitaRepository.findById(espacioCitaId)
       .orElseThrow(()->new RuntimeException("Espacio de cita no encontrado"));
       // RN1 Verificar que el espacio no ese ocupado
       if(citaRepository.existsByEspacioCitaAndEstadoIn(espacioCitaId, List.of("PENDIENTE","CONFIRMADA"))){
        throw new RuntimeException("El espacio seleccionado ya esta ocupado");
       }
       //RN4: Validacion de fecha y hora pasada ( no es permitido)
       LocalDateTime ahora = LocalDateTime.now();
       LocalDateTime fechaHoraCita = LocalDateTime.of(espacio.getFecha(),espacio.getHorainicio());
       if(fechaHoraCita.isBefore(ahora)){
        throw new RuntimeException("No se pueden crear citas en fechas u horas pasadas");
       }
       //RN6 : Verificar que el usuario no tenga citas solapadas 
       List<Cita> citasSolapadas = citaRepository.findByUsuarioIdAndHorarioSolapado(
        usuarioId,
        espacio.getFecha(),
        espacio.getHorainicio(),
        espacio.getHorafin()); 
        if(!citasSolapadas.isEmpty()){
            throw new RuntimeException("Ya tiene una cita programada en este horario");
        }
        // Crear la Cita
        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setMedico(espacio.getMedico());
        cita.setEspacio(espacio);
        cita.setEstado("PENDIENTE");
        //Marcar el espacio como ocupado
        espacio.setOcupado(true);
        espacioCitaRepository.save(espacio);
        return citaRepository.save(cita);       

    }
    /**
     * Confirmar cita ( SOLO ADMIN)
     */
    @Transactional
    public Cita confirmarCita(Long citaId){
        Cita cita = citaRepository.findById(citaId)
        .orElseThrow(()->new RuntimeException("Cita no fue encontrada"));
        // RN3 Solo confirma si la cita pendiente 
        if(!"PENDIENTE".equals(cita.getEstado())){
             throw new RuntimeException("Solo se puede confirmar citas que esten pendientes");
        }
        cita.confirmar();
        return citaRepository.save(cita);            
    }
    /**
     * Cancelacion de Cita por usuario (RN5)
     */
    @Transactional
    public Cita cancelarCitaUsuario (Long citaId,Usuario usuario){
        Cita cita = citaRepository.findById(citaId)
        .orElseThrow(()->new RuntimeException("Cita no fue encontrada"));
        // RN 8 Solo puede cancelar su propia cita
        if(!cita.getUsuario().getId().equals(usuario.getId())){
            throw new RuntimeException("No tiene permiso para cancelar esta cita");
        }
        // RN 5 Solo puede cancelarse si la fecha aun no pasado
        LocalDateTime ahora= LocalDateTime.now();
        LocalDateTime fechaHoraCita = LocalDateTime.of(
            cita.getEspacio().getFecha(),
            cita.getEspacio().getHorainicio()
        );
        if(fechaHoraCita.isBefore(ahora)){
            throw new RuntimeException("No se puede cancelar una cita cuya cita ya passo");
        }
        //RN 3 : Solo puede cancelar si la cita esta PENDIENTE O CONFIRMADA
        if(!"PENDIENTE". equals(cita.getEstado())&& !"CONFIRMADA". equals(cita.getEstado())){
            throw new RuntimeException("Solo se pueden cancerlar cita pendiente o confirmadas");
        }
        cancelarCitaInternamente(cita,"Cancelada por el usuario");
        return citaRepository.save(cita);


    }
    /**
     * Cancelacion de citas por administrador( Sin restricciones)
     */
    @Transactional
    public Cita cancelarCitaAdmin(Long citaId, String motivo){
        Cita cita = citaRepository.findById(citaId)
        .orElseThrow(()->new RuntimeException("Cita no fue encontrada"));
        //RN 3 Solo se puede cancelar si esta PENDIENTE, CONFIRMADA O AUSENTE 
        if(!"PENDIENTE". equals(cita.getEstado())&& !"CONFIRMADA". equals(cita.getEstado())&& ! "AUSENTE". equals(cita.getEstado())){
            throw new RuntimeException("Solo se pueden cancerlar cita pendiente , confirmadas o si el paciente que ausente");
    }
    cancelarCitaInternamente(cita, motivo !=null ? motivo: "Cancelada por el administrador");
        return citaRepository.save(cita);
    
}
/**
 * Marcar cita como Presente
 */
@Transactional
public Cita marcarComoPresente(Long citaId){
   Cita cita = citaRepository.findById(citaId)
        .orElseThrow(()->new RuntimeException("Cita no fue encontrada")); 
        //RN 3 Solo se marca preente si esta pendiente o confirmada
        if(!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())){
          throw new RuntimeException("Solo se pueden marcar como presentes citas pendiente o confirmadas");  
        }
        cita.marcarComoPresente();
        return citaRepository.save(cita);
}
/**
 * Marcar cita como Ausente
 */
@Transactional
public Cita marcarComoAusente(Long citaId,String motivo){
    Cita cita = citaRepository.findById(citaId)
          .orElseThrow(()->new RuntimeException("Cita no fue encontrada")); 
        if(!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())){
          throw new RuntimeException("Solo se pueden marcar como presentes citas pendiente o confirmadas");  
        }  
        cita.marcarComoAusente(motivo);      
        //RN 7: Liberar el espacio  inmediatamente
        EspacioCita espacio= cita.getEspacio();
        espacio.setOcupado(false);
        espacioCitaRepository.save(espacio);
        return citaRepository.save(cita);
}

private void cancelarCitaInternamente(Cita cita,String motivo){
    cita.cancelar(motivo);
    //RN 7: Liberar el espacio  inmediatamente
    EspacioCita espacio= cita.getEspacio();
    espacio.setOcupado(false);
    espacioCitaRepository.save(espacio);
}
// Metodos de consulta
public List<Cita> getCitasPorUsuario(Long usuarioId){
    return citaRepository.findByUsuarioId(usuarioId);

}
public List<Cita> getCitasPorUsuarioYEstado(Long usuarioId,String estado){
    return citaRepository.findByUsuarioIdAndEstado(usuarioId,estado);

}
public List<Cita> getCitasPorMedico(Long medicoId){
    return citaRepository.findByMedicoId(medicoId);
}
public List<Cita> getCitasPorEstado(String estado){
    return citaRepository.findByEstado(estado);
}
public List<Cita> getAllCitas() {
    return citaRepository.findAll();
}
public Optional<Cita>getCitaById(long id){
    return citaRepository.findById(id);
}


}
