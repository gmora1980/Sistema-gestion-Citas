package com.sistemagestioncitas.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistemagestioncitas.hospital.model.Cita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository

public interface CitaRepository  extends JpaRepository<Cita,Long>{
    //Busqueda de Citas por usuario
    List<Cita> findByUsuarioId(Long usuarioId);
     //bussqueda de citas por medico
     List<Cita> findByMedicoId (Long medicoId);
     // busqueda de citas por estado
     List<Cita> findByEstado(String estado);
     //busqueda de citas por usuario y estado
     List<Cita> findByUsuarioIdAndEstado(Long UsuarioId,String estado);
     //verificar si el espacion tiene cita activa
     Optional<Cita>  findByEspacioIdAndEstadoIn(Long espacioCitaId, List<String> estados);
     // verificar si existe una cita en una espacio especifico 
     boolean existsByEspacioCitaAndEstadoIn(Long espacioCitaId, List<String> estados);
     // busqueda de citas por medico y estado
     List<Cita> findByMedicoIdAndEstado(Long medicoId, String estado);
     // busqueda de citas en Rango de fechas 
     @Query("SELECT c FROM Cita c WHERE  c.espacioCita.fecha BETWEEN : fechaInicio AND : fechaFin")
     List<Cita> findByRandoFechas(@Param("fechaInicio")LocalDateTime fechaInicio,@Param("fechaFin") LocalDateTime fechaFin);  
     //verificar si el usuario tiene cita que choque en el mismo horario
     @Query("SELECT c FROM Cita c WHERE c.usuario.id=:usuarioId" +"AND c.estado IN('PENDIENTE ','CONFIRMADA')" +
        "AND c.espacioCita.fecha=:fecha" +
    "AND (("+
     " (:horaInicio>=c,espacioCita.horaInicio AND: horaInicio < c.espacioCita.horaFin)OR " +
    " (:horaFin> c.espacioCita.horaInicio AND :horaFin<=c.espacioCita.horaFin) OR " +
"(:horaInicio<=c.espacioCIta.horaInicio AND :horaFin>=c.espacioCita.horafin)" +
"))" )
List<Cita> findByUsuarioIdAndHorarioSolapado(@Param("usuarioId")Long usaurioId,
                                          @Param("fecha") LocalDate fecha,
                                        @Param("horaInicio") LocalTime horaInicio,
                                    @Param("horaFin")LocalTime horaFin);

}

    

