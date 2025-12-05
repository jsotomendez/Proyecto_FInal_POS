package com.unicordoba.FinalProject.repository;

import com.unicordoba.FinalProject.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Query("SELECT p FROM Pago p WHERE " +
            "(:fechaInicio IS NULL OR CAST(p.fechaHora AS date) >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR CAST(p.fechaHora AS date) <= :fechaFin) AND " +
            "(:metodo IS NULL OR p.metodoPago = :metodo)")
    List<Pago> filtrarPagos(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("metodo") String metodo);
}