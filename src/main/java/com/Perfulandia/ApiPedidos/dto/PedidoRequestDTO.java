package com.Perfulandia.ApiPedidos.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PedidoRequestDTO {
    private LocalDate fechaPedido;
    private String estado;
    private Integer totalNeto;
    private Integer usuarioId;
    private Integer productoId;
    private Integer cuponId;
}