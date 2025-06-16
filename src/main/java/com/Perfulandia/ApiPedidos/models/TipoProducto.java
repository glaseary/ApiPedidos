package com.Perfulandia.ApiPedidos.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TIPO_PRODUCTO")
@Data
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto")
    private Integer idTipoProducto;

    @Column(name = "nombre_tipoproducto", nullable = false, length = 50)
    private String nombreTipoproducto;
}