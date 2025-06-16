package com.Perfulandia.ApiPedidos.dto;

import lombok.Data;

@Data
public class ProductoDTO {
private Integer idProducto;
private String nombre;
private String descripcion;
private Integer precio;
private Integer costo;
private Integer marcaId;
private String nombreMarca;
private Integer tipoProductoId;
private String nombreTipoProducto;
}