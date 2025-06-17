package com.Perfulandia.ApiPedidos;

import com.Perfulandia.ApiPedidos.dto.PedidoRequestDTO;
import com.Perfulandia.ApiPedidos.dto.PedidoResponseDTO;
import com.Perfulandia.ApiPedidos.models.*;
import com.Perfulandia.ApiPedidos.repository.CuponRepository;
import com.Perfulandia.ApiPedidos.repository.PedidoRepository;
import com.Perfulandia.ApiPedidos.repository.ProductoRepository;
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

    // --- MOCKS AÑADIDOS ---
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository; // Necesario ahora
    @Mock
    private CuponRepository cuponRepository;       // Necesario ahora

    @InjectMocks
    private PedidoService pedidoService;

    // --- OBJETOS DE PRUEBA ---
    private Usuario usuarioDePrueba;
    private Producto productoDePrueba;
    private Cupon cuponDePrueba;
    private PedidoRequestDTO pedidoRequest;

    @BeforeEach
    void setUp() {
        // 1. Preparar (Arrange)
        usuarioDePrueba = new Usuario();
        usuarioDePrueba.setIdUsuario(5);
        usuarioDePrueba.setNombreUsuario("Cliente Fiel");
        usuarioDePrueba.setEmail("cliente@fiel.com");
        
        // --- PREPARACIÓN ADICIONAL NECESARIA ---
        productoDePrueba = new Producto();
        productoDePrueba.setIdProducto(25);
        productoDePrueba.setNombre("Perfume Aqua");
        // Para evitar NullPointerException en toResponseDTO, debemos simular las relaciones
        productoDePrueba.setMarca(new Marca()); 
        productoDePrueba.setTipoProducto(new TipoProducto());

        cuponDePrueba = new Cupon();
        cuponDePrueba.setIdCupon(10);
        cuponDePrueba.setNombreCupon("DESCUENTO10");

        // Creamos un DTO completo que simula la petición del cliente
        pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setUsuarioId(usuarioDePrueba.getIdUsuario());
        pedidoRequest.setProductoId(productoDePrueba.getIdProducto()); // <-- CAMPO AÑADIDO
        pedidoRequest.setCuponId(cuponDePrueba.getIdCupon());         // <-- CAMPO AÑADIDO
        pedidoRequest.setFechaPedido(LocalDate.now());
        pedidoRequest.setEstado("PENDIENTE");
        pedidoRequest.setTotalNeto(9990);
    }

    @Test
    void testCrearPedidoExitoso() {
        // Arrange
        // --- SIMULACIONES AÑADIDAS ---
        // "Cuando se busque el usuario, producto y cupón, devuélvelos"
        when(usuarioRepository.findById(5)).thenReturn(Optional.of(usuarioDePrueba));
        when(productoRepository.findById(25)).thenReturn(Optional.of(productoDePrueba));
        when(cuponRepository.findById(10)).thenReturn(Optional.of(cuponDePrueba));

        // --- SIMULACIÓN MEJORADA ---
        // "Cuando se llame a pedidoRepository.save con CUALQUIER objeto Pedido..."
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            // "...devuelve el mismo objeto, pero completo y con un ID"
            Pedido pedidoParaGuardar = invocation.getArgument(0);
            pedidoParaGuardar.setIdPedido(101); // Simulamos que la BD le asignó el ID 101
            
            // ¡Importante! El objeto guardado debe tener todo lo que toResponseDTO necesita
            pedidoParaGuardar.setUsuario(usuarioDePrueba);
            pedidoParaGuardar.setProducto(productoDePrueba);
            pedidoParaGuardar.setCupon(cuponDePrueba);
            
            return pedidoParaGuardar;
        });

        // 2. Actuar (Act)
        PedidoResponseDTO resultado = pedidoService.crearPedido(pedidoRequest);

        // 3. Verificar (Assert)
        assertNotNull(resultado);
        assertEquals(101, resultado.getIdPedido());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Cliente Fiel", resultado.getUsuario().getNombreUsuario());
        assertEquals("Perfume Aqua", resultado.getProducto().getNombre()); // Nueva verificación
        assertEquals("DESCUENTO10", resultado.getCupon().getNombreCupon());  // Nueva verificación
        
        // Verificamos que se llamó a los métodos correctos de los repositorios
        verify(usuarioRepository, times(1)).findById(5);
        verify(productoRepository, times(1)).findById(25);
        verify(cuponRepository, times(1)).findById(10);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_ConUsuarioNoExistente() {
        // Arrange: Simulamos que el usuario NO existe
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // El DTO debe tener todos los campos requeridos por el servicio para evitar un NullPointerException antes de tiempo
        PedidoRequestDTO requestUsuarioFalso = new PedidoRequestDTO();
        requestUsuarioFalso.setUsuarioId(99);
        requestUsuarioFalso.setProductoId(25); // Aunque el usuario no exista, el productoId es necesario

        // Act & Assert: Verificamos que al intentar crear el pedido, se lance la excepción correcta.
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            pedidoService.crearPedido(requestUsuarioFalso);
        });
        
        // Opcional: Verificar que el mensaje de error sea el esperado
        assertTrue(exception.getMessage().contains("Usuario no encontrado con ID: 99"));
        
        // Verificamos que el guardado nunca se intentó
        verify(pedidoRepository, never()).save(any());
    }
}