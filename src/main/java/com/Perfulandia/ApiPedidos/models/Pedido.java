package com.Perfulandia.ApiPedidos.models;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- Importar
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PEDIDO")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "total_neto", nullable = false)
    private Integer totalNeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_id_usuario", nullable = false)
    @JsonBackReference 
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTO_id_producto", nullable = false)
    @JsonBackReference 
    private Producto producto;
        
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUPON_id_cupon", nullable = true)
    @JsonBackReference // <-- AÑADIR ESTA LÍNEA
    private Cupon cupon;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetallePedido> pedidos;
}