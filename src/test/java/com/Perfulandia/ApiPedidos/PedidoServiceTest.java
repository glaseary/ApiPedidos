package com.Perfulandia.ApiPedidos;

import com.Perfulandia.ApiPedidos.dto.PedidoRequestDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoResponseDTO;
import com.Perfulandia.ApiPedidos.models.*;
import com.Perfulandia.ApiPedidos.repository.PedidoRepository;
import com.Perfulandia.ApiPedidos.repository.UsuarioRepository;
import com.Perfulandia.ApiPedidos.services.PedidoService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    // No necesitamos mockear ProductoService porque PedidoService no lo inyecta directamente.

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuarioDePrueba;
    private PedidoRequestDTO pedidoRequest;

    @BeforeEach
    void setUp() {
        // 1. Preparar (Arrange)
        usuarioDePrueba = new Usuario();
        usuarioDePrueba.setIdUsuario(5);
        usuarioDePrueba.setNombreUsuario("Cliente Fiel");
        usuarioDePrueba.setEmail("cliente@fiel.com");
        
        // Creamos un DTO que simula la petición del cliente
        pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setUsuarioId(5);
        pedidoRequest.setFechaPedido(LocalDate.now());
        pedidoRequest.setEstado("PENDIENTE");
        pedidoRequest.setTotalNeto(9990);
    }

    @Test
    void testCrearPedidoExitoso() {
        // Arrange
        // "Cuando el servicio llame a usuarioRepository.findById(5), devuelve nuestro usuario de prueba"
        when(usuarioRepository.findById(5)).thenReturn(Optional.of(usuarioDePrueba));

        // "Cuando se llame a pedidoRepository.save con CUALQUIER objeto Pedido..."
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            // "...devuelve el mismo objeto que te pasaron, pero asígnale un ID como si lo hiciera la BD"
            Pedido pedidoParaGuardar = invocation.getArgument(0);
            pedidoParaGuardar.setIdPedido(101); // Simulamos que la BD le asignó el ID 101
            return pedidoParaGuardar;
        });

        // --- 2. Actuar (Act) ---
        PedidoResponseDTO resultado = pedidoService.crearPedido(pedidoRequest);

        // --- 3. Verificar (Assert) ---
        assertNotNull(resultado);
        assertEquals(101, resultado.getIdPedido());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Cliente Fiel", resultado.getUsuario().getNombreUsuario());
        
        // Verificamos que se llamó al repositorio para guardar el pedido.
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_ConUsuarioNoExistente() {
        // Arrange: Simulamos que el usuario NO existe
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        PedidoRequestDTO requestUsuarioFalso = new PedidoRequestDTO();
        requestUsuarioFalso.setUsuarioId(99);

        // Act & Assert: Verificamos que al intentar crear el pedido, se lance la excepción correcta.
        assertThrows(EntityNotFoundException.class, () -> {
            pedidoService.crearPedido(requestUsuarioFalso);
        });
    }
}