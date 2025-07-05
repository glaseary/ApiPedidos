package com.Perfulandia.ApiPedidos.dto;

import lombok.Data;
import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

@Data
public class PedidoResponseDTO extends RepresentationModel <PedidoResponseDTO>{
    private Integer idPedido;
    private LocalDate fechaPedido;
    private String estado;
    private Integer totalNeto;
    private ProductoDTO producto;
    private UsuarioDTO usuario;
    private CuponDTO cupon;

}