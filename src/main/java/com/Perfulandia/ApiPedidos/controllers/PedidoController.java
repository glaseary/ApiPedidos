package com.Perfulandia.ApiPedidos.controllers;

import com.Perfulandia.ApiPedidos.dto.PedidoRequestDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoResponseDTO;
import com.Perfulandia.ApiPedidos.services.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * LEER todos los pedidos
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * LEER un pedido por su ID
     * GET /api/pedidos/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            PedidoResponseDTO pedido = pedidoService.obtenerPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * CREAR un nuevo pedido
     * POST /api/pedidos
     */
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody PedidoRequestDTO requestDTO) {
        try {
            // Asigna la fecha actual al momento de la creación
            requestDTO.setFechaPedido(java.time.LocalDate.now());
            PedidoResponseDTO pedidoCreado = pedidoService.crearPedido(requestDTO);
            return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Este error ocurre si el usuarioId no existe
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ACTUALIZAR un pedido existente
     * PUT /api/pedidos/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Integer id, @RequestBody PedidoRequestDTO requestDTO) {
        try {
            PedidoResponseDTO pedidoActualizado = pedidoService.actualizarPedido(id, requestDTO);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ELIMINAR un pedido
     * DELETE /api/pedidos/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ======================================================
    // MÉTODOS HATEOAS
    // ======================================================

    @GetMapping("/hateoas/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerHATEOAS(@PathVariable Integer id) {
        // Este método no cambia
        try {
            PedidoResponseDTO dto = pedidoService.obtenerPorId(id);
            String gatewayUrl = "http://localhost:8888/api/proxy/pedidos";

            dto.add(Link.of(gatewayUrl + "/hateoas/" + dto.getIdPedido()).withSelfRel());
            dto.add(Link.of(gatewayUrl + "/hateoas").withRel("todos-los-pedidos"));
            dto.add(Link.of(gatewayUrl + "/" + dto.getIdPedido()).withRel("actualizar").withType("PUT"));
            dto.add(Link.of(gatewayUrl + "/" + dto.getIdPedido()).withRel("eliminar").withType("DELETE"));

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todos los pedidos y añade enlaces HATEOAS a cada uno,
     * incluyendo el enlace para crear un nuevo pedido.
     */
    @GetMapping("/hateoas")
    public ResponseEntity<List<PedidoResponseDTO>> listarHATEOAS() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        String gatewayUrl = "http://localhost:8888/api/proxy/pedidos";

        for (PedidoResponseDTO dto : pedidos) {
            // Link a los detalles de este pedido (self)
            dto.add(Link.of(gatewayUrl + "/hateoas/" + dto.getIdPedido()).withSelfRel());

            // **ENLACE AÑADIDO PARA CREAR (POST)**
            dto.add(Link.of(gatewayUrl).withRel("crear-pedido").withType("POST"));
        }

        return ResponseEntity.ok(pedidos);
    }
}
