package com.Perfulandia.ApiPedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Perfulandia.ApiPedidos.models.Cupon;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Integer>{

}
