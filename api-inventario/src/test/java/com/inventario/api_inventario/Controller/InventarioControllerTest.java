package com.inventario.api_inventario.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.api_inventario.controller.InventarioController;
import com.inventario.api_inventario.model.InventarioModel;
import com.inventario.api_inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioRepository repo;

    private InventarioModel inventarioEjemplo;

    @BeforeEach
    void setUp() {
        inventarioEjemplo = new InventarioModel();
        inventarioEjemplo.setId("inv123");
        inventarioEjemplo.setSede("Puerto Montt");
    }

    // TESTS PARA GET (Listar y Filtrar)
    @Test
    void listarInventario_DeberiaDevolverLista() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(inventarioEjemplo));

        mockMvc.perform(get("/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("inv123"))
                .andExpect(jsonPath("$[0].sede").value("Puerto Montt"));
    }

    @Test
    void encontrarSede_DeberiaFiltrarPorParametro() throws Exception {
        when(repo.findBySede("Puerto Montt")).thenReturn(Collections.singletonList(inventarioEjemplo));

        // Testeamos el paso de parámetros por URL (?nombre=Puerto Montt)
        mockMvc.perform(get("/inventario/nombre")
                        .param("nombre", "Puerto Montt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sede").value("Puerto Montt"));
    }

    // TESTS PARA POST (Registrar Inventario)
    @Test
    void registrarInventario_Exitoso_DeberiaGuardar() throws Exception {
        when(repo.findBySede("Puerto Montt")).thenReturn(new ArrayList<>());
        when(repo.save(any(InventarioModel.class))).thenReturn(inventarioEjemplo);

        mockMvc.perform(post("/inventario/nuevoInventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("inv123"));
    }

    @Test
    void registrarInventario_SedeDuplicada_DeberiaAvisar() throws Exception {
        // Simulamos que la sede ya existe devolviendo una lista con un registro
        when(repo.findBySede("Puerto Montt")).thenReturn(Collections.singletonList(inventarioEjemplo));

        mockMvc.perform(post("/inventario/nuevoInventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(content().string("La sede ya se encuentra registrada"));
    }

    @Test
    void registrarInventario_Error_DeberiaEntrarAlCatch() throws Exception {
        // Forzamos un fallo en la query para hacer saltar el bloque catch (HTTP 404)
        when(repo.findBySede("Puerto Montt")).thenThrow(new RuntimeException("Error simulado de BD"));

        mockMvc.perform(post("/inventario/nuevoInventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioEjemplo)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ocurrió un problema verifica los campos"));
    }

    // TESTS PARA DELETE (Eliminar por ID)
    @Test
    void eliminarInventario_Exitoso_DeberiaDar200() throws Exception {
        mockMvc.perform(delete("/inventario/inv123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventario eliminado"));
    }

    @Test
    void eliminarInventario_Error_DeberiaDar404() throws Exception {
        // Forzamos una excepción en el método void deleteById para validar el catch
        doThrow(new RuntimeException()).when(repo).deleteById("inv123");

        mockMvc.perform(delete("/inventario/inv123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontro inventario con id inv123"));
    }
}
