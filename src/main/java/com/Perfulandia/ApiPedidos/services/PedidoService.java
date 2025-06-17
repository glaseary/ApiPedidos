package com.Perfulandia.ApiPedidos.services;

import com.Perfulandia.ApiPedidos.dto.CuponDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoRequestDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoResponseDTO;
import com.Perfulandia.ApiPedidos.dto.ProductoDTO;
import com.Perfulandia.ApiPedidos.dto.UsuarioDTO;
import com.Perfulandia.ApiPedidos.models.Cupon;
import com.Perfulandia.ApiPedidos.models.Pedido;
import com.Perfulandia.ApiPedidos.models.Producto;
import com.Perfulandia.ApiPedidos.models.Usuario;
import com.Perfulandia.ApiPedidos.repository.CuponRepository; // ¡Importante! Asegúrate de tener este repositorio
import com.Perfulandia.ApiPedidos.repository.PedidoRepository;
import com.Perfulandia.ApiPedidos.repository.ProductoRepository; // ¡Importante!
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

    // --- DEPENDENCIAS AÑADIDAS ---
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CuponRepository cuponRepository; // Asumiendo que tienes un CuponRepository

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public PedidoResponseDTO obtenerPorId(Integer id) {
        return pedidoRepository.findById(id).map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));
    }

    // --- MÉTODO CORREGIDO ---
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        // 1. Buscar las entidades relacionadas
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId()));
        
        Producto producto = productoRepository.findById(requestDTO.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + requestDTO.getProductoId()));

        // 2. Crear y poblar el nuevo pedido
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(requestDTO.getFechaPedido());
        pedido.setEstado(requestDTO.getEstado());
        pedido.setTotalNeto(requestDTO.getTotalNeto());
        pedido.setUsuario(usuario);
        pedido.setProducto(producto); // ¡Asignación clave que faltaba!

        // 3. Asignar cupón si se proporcionó un ID
        if (requestDTO.getCuponId() != null) {
            Cupon cupon = cuponRepository.findById(requestDTO.getCuponId())
                    .orElseThrow(() -> new EntityNotFoundException("Cupón no encontrado con ID: " + requestDTO.getCuponId()));
            pedido.setCupon(cupon); // ¡Asignación clave que faltaba!
        }
        
        // 4. Guardar y convertir a DTO para la respuesta
        return toResponseDTO(pedidoRepository.save(pedido));
    }
    
    // --- MÉTODO CORREGIDO ---
    public PedidoResponseDTO actualizarPedido(Integer id, PedidoRequestDTO requestDTO) {
        // 1. Buscar el pedido existente
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));

        // 2. Buscar las nuevas entidades relacionadas
        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId()));
        
        Producto producto = productoRepository.findById(requestDTO.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + requestDTO.getProductoId()));

        // 3. Actualizar todos los campos
        pedidoExistente.setFechaPedido(requestDTO.getFechaPedido());
        pedidoExistente.setEstado(requestDTO.getEstado());
        pedidoExistente.setTotalNeto(requestDTO.getTotalNeto());
        pedidoExistente.setUsuario(usuario);
        pedidoExistente.setProducto(producto); // ¡Actualización clave que faltaba!

        // 4. Actualizar el cupón (permitiendo que sea nulo)
        if (requestDTO.getCuponId() != null) {
            Cupon cupon = cuponRepository.findById(requestDTO.getCuponId())
                    .orElseThrow(() -> new EntityNotFoundException("Cupón no encontrado con ID: " + requestDTO.getCuponId()));
            pedidoExistente.setCupon(cupon);
        } else {
            pedidoExistente.setCupon(null); // Permite quitar un cupón existente
        }

        return toResponseDTO(pedidoRepository.save(pedidoExistente));
    }

    public void eliminarPedido(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    // --- MÉTODO DE CONVERSIÓN MEJORADO ---
    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotalNeto(pedido.getTotalNeto());

        // Conversión de Usuario
        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setIdUsuario(pedido.getUsuario().getIdUsuario());
        usuarioDto.setNombreUsuario(pedido.getUsuario().getNombreUsuario());
        usuarioDto.setEmail(pedido.getUsuario().getEmail());
        dto.setUsuario(usuarioDto);

        // Conversión de Cupón (con comprobación de nulidad)
        if (pedido.getCupon() != null) {
            CuponDTO cuponDTO = new CuponDTO();
            cuponDTO.setIdCupon(pedido.getCupon().getIdCupon());
            cuponDTO.setNombreCupon(pedido.getCupon().getNombreCupon());
            dto.setCupon(cuponDTO);
        }

        // Conversión de Producto (con comprobación de nulidad)
        if (pedido.getProducto() != null) {
            ProductoDTO productoDTO = new ProductoDTO();
            Producto producto = pedido.getProducto();
            productoDTO.setIdProducto(producto.getIdProducto());
            productoDTO.setNombre(producto.getNombre());
            productoDTO.setCosto(producto.getCosto());
            productoDTO.setDescripcion(producto.getDescripcion());
            productoDTO.setPrecio(producto.getPrecio());

            if (producto.getMarca() != null) {
                productoDTO.setMarcaId(producto.getMarca().getIdMarca());
                productoDTO.setNombreMarca(producto.getMarca().getNombreMarca());
            }

            if (producto.getTipoProducto() != null) {
                productoDTO.setTipoProductoId(producto.getTipoProducto().getIdTipoProducto());
                productoDTO.setNombreTipoProducto(producto.getTipoProducto().getNombreTipoproducto());
            }
            
            dto.setProducto(productoDTO);
        }

        return dto;
    }
}