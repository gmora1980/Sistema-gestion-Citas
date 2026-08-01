package com.sistemagestioncitas.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemagestioncitas.hospital.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
        List<Medico> findByActivoTrue();

}
