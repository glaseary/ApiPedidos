package com.Perfulandia.ApiPedidos.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCTO")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer costo;

    @ManyToOne
    @JoinColumn(name = "MARCA_id_marca", nullable = false)
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "TIPO_PRODUCTO_id_tipo_producto", nullable = false)
    private TipoProducto tipoProducto;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pedido> pedidos;
}