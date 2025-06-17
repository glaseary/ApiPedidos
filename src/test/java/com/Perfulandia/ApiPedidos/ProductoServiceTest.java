package com.Perfulandia.ApiPedidos;

import com.Perfulandia.ApiPedidos.dto.ProductoDTO;
import com.Perfulandia.ApiPedidos.models.Marca;
import com.Perfulandia.ApiPedidos.models.Producto;
import com.Perfulandia.ApiPedidos.models.TipoProducto;
import com.Perfulandia.ApiPedidos.repository.ProductoRepository;
import com.Perfulandia.ApiPedidos.services.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Anotación para activar Mockito
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // @Mock crea una simulación del repositorio. No es el objeto real.
    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks crea una instancia real de ProductoService
    // e inyecta el mock de arriba en su campo @Autowired.
    @InjectMocks
    private ProductoService productoService;

    private Producto productoDePrueba;

    // Este método prepara los datos antes de cada test
    @BeforeEach
    void setUp() {
        // --- 1. Preparar (Arrange) ---
        Marca marca = new Marca();
        marca.setIdMarca(1);
        marca.setNombreMarca("Nestlé");

        TipoProducto tipo = new TipoProducto();
        tipo.setIdTipoProducto(1);
        tipo.setNombreTipoproducto("Lácteos");

        productoDePrueba = new Producto();
        productoDePrueba.setIdProducto(1);
        productoDePrueba.setNombre("Leche Condensada");
        productoDePrueba.setPrecio(1500);
        productoDePrueba.setMarca(marca);
        productoDePrueba.setTipoProducto(tipo);
    }

    @Test
    void testListarProductos() {
        // Arrange: Le decimos a nuestro mock del repositorio qué debe devolver
        // "Cuando se llame al método findAll(), devuelve una lista con nuestro producto de prueba"
        when(productoRepository.findAll()).thenReturn(Collections.singletonList(productoDePrueba));

        // --- 2. Actuar (Act) ---
        List<ProductoDTO> resultado = productoService.listarProductos();

        // --- 3. Verificar (Assert) ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Leche Condensada", resultado.get(0).getNombre());
        assertEquals("Nestlé", resultado.get(0).getNombreMarca());
        
        // Verificamos que el método findAll() del repositorio fue llamado exactamente 1 vez
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerProductoPorId() {
        // Arrange: Le decimos al mock qué devolver cuando se busque por ID 1
        when(productoRepository.findById(1)).thenReturn(Optional.of(productoDePrueba));

        // Act
        ProductoDTO resultado = productoService.obtenerProductoPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdProducto());
        assertEquals("Lácteos", resultado.getNombreTipoProducto());
    }
}