package com.sistemagestioncitas.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface EspacioCitaRepository extends JpaRepository<EspacioCita, Long> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario

}
