package com.Perfulandia.ApiPedidos.services;

import com.Perfulandia.ApiPedidos.dto.CuponDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoRequestDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoResponseDTO;
import com.Perfulandia.ApiPedidos.dto.ProductoDTO;
import com.Perfulandia.ApiPedidos.dto.UsuarioDTO;
import com.Perfulandia.ApiPedidos.models.Pedido;
import com.Perfulandia.ApiPedidos.models.Usuario;
import com.Perfulandia.ApiPedidos.repository.PedidoRepository;
import com.Perfulandia.ApiPedidos.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public PedidoResponseDTO obtenerPorId(Integer id) {
        return pedidoRepository.findById(id).map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));
    }

    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId()));

        Pedido pedido = new Pedido();
        pedido.setFechaPedido(requestDTO.getFechaPedido());
        pedido.setEstado(requestDTO.getEstado());
        pedido.setTotalNeto(requestDTO.getTotalNeto());
        pedido.setUsuario(usuario);
        
        return toResponseDTO(pedidoRepository.save(pedido));
    }
    
    public PedidoResponseDTO actualizarPedido(Integer id, PedidoRequestDTO requestDTO) {
        // 1. Busca el pedido que se va a actualizar
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));

        // 2. Valida que el nuevo usuario (si se cambia) exista
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId()));
        
        // 3. Actualiza todos los campos
        pedidoExistente.setFechaPedido(requestDTO.getFechaPedido());
        pedidoExistente.setEstado(requestDTO.getEstado());
        pedidoExistente.setTotalNeto(requestDTO.getTotalNeto());
        pedidoExistente.setUsuario(usuario);

        return toResponseDTO(pedidoRepository.save(pedidoExistente));
    }

    public void eliminarPedido(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    // --- Método de conversión ---
    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotalNeto(pedido.getTotalNeto());

        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setIdUsuario(pedido.getUsuario().getIdUsuario());
        usuarioDto.setNombreUsuario(pedido.getUsuario().getNombreUsuario());
        usuarioDto.setEmail(pedido.getUsuario().getEmail());
        dto.setUsuario(usuarioDto);

        CuponDTO cuponDTO = new CuponDTO();
        if (pedido.getCupon() != null) {
            cuponDTO.setIdCupon(pedido.getCupon().getIdCupon());
            cuponDTO.setNombreCupon(pedido.getCupon().getNombreCupon());
            dto.setCupon(cuponDTO);
        }

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setIdProducto(pedido.getProducto().getIdProducto());
        productoDTO.setNombre(pedido.getProducto().getNombre());
        productoDTO.setCosto(pedido.getProducto().getCosto());
        productoDTO.setDescripcion(pedido.getProducto().getDescripcion());
        productoDTO.setMarcaId(pedido.getProducto().getMarca().getIdMarca());
        productoDTO.setNombreMarca(pedido.getProducto().getMarca().getNombreMarca());
        productoDTO.setTipoProductoId(pedido.getProducto().getTipoProducto().getIdTipoProducto());
        productoDTO.setNombreTipoProducto(pedido.getProducto().getTipoProducto().getNombreTipoproducto());
        productoDTO.setPrecio(pedido.getProducto().getPrecio());
        dto.setProducto(productoDTO);

        return dto;
    }
}