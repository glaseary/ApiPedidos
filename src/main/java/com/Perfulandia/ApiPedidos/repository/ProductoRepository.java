package com.Perfulandia.ApiPedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Perfulandia.ApiPedidos.models.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

}
