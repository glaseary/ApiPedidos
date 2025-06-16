package com.Perfulandia.ApiPedidos.repository;

import com.Perfulandia.ApiPedidos.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario_IdUsuario(Integer usuarioId);
}