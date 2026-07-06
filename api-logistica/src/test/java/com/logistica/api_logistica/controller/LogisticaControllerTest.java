package com.logistica.api_logistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.api_logistica.model.ElementoModel;
import com.logistica.api_logistica.model.LogisticaModel;
import com.logistica.api_logistica.repository.LogisticaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogisticaController.class)
public class LogisticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LogisticaRepository repo;

    private LogisticaModel viajeEjemplo;

    @BeforeEach
    void setUp() {
        // 1. Creamos un elemento válido para la lista de carga (cumpliendo @NotBlank y @Positive)
        ElementoModel item = new ElementoModel();
        item.setNombre("Kits de Primeros Auxilios");
        item.setCantidad(25);

        List<ElementoModel> listaCarga = new ArrayList<>();
        listaCarga.add(item);

        // 2. Armamos el objeto principal de Logística llenando todos sus campos obligatorios
        viajeEjemplo = new LogisticaModel();
        viajeEjemplo.setId("100");
        viajeEjemplo.setChofer("Diego Silva");
        viajeEjemplo.setMatricula("AA-BB-11");
        viajeEjemplo.setOrigen("Centro de Acopio Central");
        viajeEjemplo.setDestino("Sede Puerto Montt");
        viajeEjemplo.setCarga(listaCarga); // Pasamos la lista que creamos arriba
    }

    // ==========================================
    // TESTS PARA GET (Listar y Buscar)
    // ==========================================
    @Test
    void listarLogistica_DeberiaDevolverLista() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(viajeEjemplo));

        mockMvc.perform(get("/logistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("100"))
                .andExpect(jsonPath("$[0].matricula").value("AA-BB-11"));
    }

    @Test
    void buscarPorId_DeberiaDevolverObjeto() throws Exception {
        when(repo.findById("100")).thenReturn(Optional.of(viajeEjemplo));

        mockMvc.perform(get("/logistica/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("100"));
    }

    // ==========================================
    // TESTS PARA POST (Registrar Logística)
    // ==========================================
    @Test
    void registrarLogistica_Exitoso_DeberiaGuardar() throws Exception {
        // Si la búsqueda por matrícula retorna vacío, se puede registrar
        when(repo.findByMatricula("AA-BB-11")).thenReturn(new ArrayList<>());
        when(repo.save(any(LogisticaModel.class))).thenReturn(viajeEjemplo);

        mockMvc.perform(post("/logistica/nuevaLogistica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(viajeEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("100"));
    }

    @Test
    void registrarLogistica_MatriculaDuplicada_DeberiaAvisar() throws Exception {
        // Simulamos que ya existe un viaje activo con esa matrícula
        List<LogisticaModel> listaConMatricula = Collections.singletonList(viajeEjemplo);
        when(repo.findByMatricula("AA-BB-11")).thenReturn(listaConMatricula);

        mockMvc.perform(post("/logistica/nuevaLogistica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(viajeEjemplo)))
                .andExpect(status().isOk()) // Tu controlador responde 200 en este flujo
                .andExpect(content().string("Ya existe la matricula en circulacion"));
    }

    @Test
    void registrarLogistica_Error_DeberiaEntrarAlCatch() throws Exception {
        // Forzamos un error en la base de datos al buscar
        when(repo.findByMatricula("AA-BB-11")).thenThrow(new RuntimeException("Simulado"));

        mockMvc.perform(post("/logistica/nuevaLogistica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(viajeEjemplo)))
                .andExpect(status().isOk())
                .andExpect(content().string("Ha ocurrido un problema = Simulado"));
    }

    // ==========================================
    // TESTS PARA DELETE POR ID
    // ==========================================
    @Test
    void eliminarEncargoId_Exitoso() throws Exception {
        mockMvc.perform(delete("/logistica/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Envío eliminado"));
    }

    @Test
    void eliminarEncargoId_Error_DeberiaDar404() throws Exception {
        // Forzamos excepción en el deleteById para levantar el catch
        doThrow(new RuntimeException()).when(repo).deleteById("100");

        mockMvc.perform(delete("/logistica/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontro envío con id 100"));
    }

    // ==========================================
    // TESTS PARA DELETE POR MATRÍCULA
    // ==========================================
    @Test
    void eliminarEncargoPorMatricula_Exitoso() throws Exception {
        when(repo.existsByMatricula("AA-BB-11")).thenReturn(true);

        mockMvc.perform(delete("/logistica/matricula/aa-bb-11")) // Mandamos minúsculas para testear el .toUpperCase()
                .andExpect(status().isOk())
                .andExpect(content().string("Viaje eliminado"));
    }

    @Test
    void eliminarEncargoPorMatricula_NoExiste_DeberiaDar404() throws Exception {
        when(repo.existsByMatricula("AA-BB-11")).thenReturn(false);

        mockMvc.perform(delete("/logistica/matricula/aa-bb-11"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontro en circulacion"));
    }

    @Test
    void eliminarEncargoPorMatricula_Error_DeberiaDar500() throws Exception {
        // Forzamos excepción al verificar la existencia
        when(repo.existsByMatricula("AA-BB-11")).thenThrow(new RuntimeException());

        mockMvc.perform(delete("/logistica/matricula/aa-bb-11"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor"));
    }
}