package com.Perfulandia.ApiPedidos.services;

import com.Perfulandia.ApiPedidos.dto.ProductoDTO;
import com.Perfulandia.ApiPedidos.models.Producto;
import com.Perfulandia.ApiPedidos.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

  @Autowired
  private ProductoRepository productoRepository;


  public List<ProductoDTO> listarProductos() {
    return productoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
  }

  public ProductoDTO obtenerProductoPorId(Integer id) {
    return toDTO(findProductoById(id));
  }

  public Producto findProductoById(Integer id) {
    return productoRepository.findById(id)
    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
  }

  public ProductoDTO toDTO(Producto producto) {
    ProductoDTO dto = new ProductoDTO();
    dto.setIdProducto(producto.getIdProducto());
    dto.setNombre(producto.getNombre());
    dto.setDescripcion(producto.getDescripcion());
    dto.setPrecio(producto.getPrecio());
    dto.setCosto(producto.getCosto());

    // Extraer directamente los nombres
    dto.setNombreMarca(producto.getMarca().getNombreMarca());
    dto.setNombreTipoProducto(producto.getTipoProducto().getNombreTipoproducto());

    return dto;
}
}