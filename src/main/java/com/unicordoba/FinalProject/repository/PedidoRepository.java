package com.unicordoba.FinalProject.repository;

import com.unicordoba.FinalProject.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // CORREGIDO: Agregamos nativeQuery = true para que MySQL entienda DATE()
    @Query(value = "SELECT COUNT(*) FROM pedido WHERE DATE(fecha_hora) = CURDATE()", nativeQuery = true)
    Long contarVentasHoy();

    // CORREGIDO: nativeQuery = true
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM pedido WHERE DATE(fecha_hora) = CURDATE() AND estado = 'PAGADO'", nativeQuery = true)
    BigDecimal sumarIngresosHoy();

    // Este ya lo tenías bien, pero lo dejo aquí para completar
    @Query(value = "SELECT DATE(fecha_hora) as fecha, SUM(total) as total " +
            "FROM pedido WHERE estado = 'PAGADO' " +
            "GROUP BY DATE(fecha_hora) ORDER BY fecha DESC LIMIT 7", nativeQuery = true)
    List<Object[]> obtenerVentasUltimosDias();
}