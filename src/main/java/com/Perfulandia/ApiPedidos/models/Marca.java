package com.Perfulandia.ApiPedidos.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marca")
@Data
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer idMarca;

    @Column(name = "nombre_marca", nullable = false, length = 30)
    private String nombreMarca;
}