package com.Perfulandia.ApiPedidos.models;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- Importar
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PEDIDO")
@Data
public class Pedido {

    // ... otros campos como idPedido, fechaPedido, etc. se mantienen igual ...
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

    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetallePedido> detalles = new ArrayList<>();
}