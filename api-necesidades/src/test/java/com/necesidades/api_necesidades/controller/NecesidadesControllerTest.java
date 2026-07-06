package com.necesidades.api_necesidades.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import com.necesidades.api_necesidades.client.UsuarioCliente;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collections;
import java.util.HashMap;

// 1. Mockito (Para simular las respuestas de tu Repo y Feign Client)
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

// 2. MockMvc Requests (Para simular el GET, POST y DELETE hacia tus endpoints)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

// 3. MockMvc Expectations (Para validar los resultados: HTTP status, texto plano o JSON)
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@WebMvcTest(NecesidadesController.class)
public class NecesidadesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NecesidadesRepository repo;

    @MockitoBean
    private UsuarioCliente usuarioClient;

    private NecesidadesModel necesidadEjemplo;

    @BeforeEach
    void setUp() {
        necesidadEjemplo = new NecesidadesModel();
        necesidadEjemplo.setId("1");
        necesidadEjemplo.setUsuarioId("69cf015746e205b1b607b33e");
        necesidadEjemplo.setDesc("Se requiere de material para primeros auxilios");
        necesidadEjemplo.setSede("Puerto Montt");
    }

    // ==========================================
    // TEST PARA GET (Listar)
    // ==========================================
    @Test
    void listarNecesidades_DeberiaDevolverLista() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(necesidadEjemplo));

        mockMvc.perform(get("/necesidades"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value("1"))
                .andExpect((ResultMatcher) jsonPath("$[0].sede").value("Puerto Montt"));
    }

    // ==========================================
    // TESTS PARA POST (Registrar)
    // ==========================================
    @Test
    void registrarDonacion_Exitoso_DeberiaDevolver200() throws Exception {
        // Simulamos que el cliente Feign encuentra al usuario (no hace nada / retorna void u objeto)
        when(usuarioClient.obtenerUsuario(necesidadEjemplo.getUsuarioId())).thenReturn(null);
        // Simulamos que el repositorio guarda con éxito
        when(repo.save(any(NecesidadesModel.class))).thenReturn(necesidadEjemplo);

        mockMvc.perform(post("/necesidades/nuevaNecesidad")
                        .contentType(MediaType.APPLICATION_JSON) // 1. Indica que envías un JSON
                        .content(objectMapper.writeValueAsString(necesidadEjemplo))) // 2. Pasa el JSON real aquí
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void registrarDonacion_UsuarioNoExiste_DeberiaDevolver404() throws Exception {
        // Forzamos que Feign lance una excepción 404
        FeignException.NotFound feignException = Mockito.mock(FeignException.NotFound.class);
        when(feignException.status()).thenReturn(404);
        doThrow(feignException).when(usuarioClient).obtenerUsuario(any(String.class));

        mockMvc.perform(post("/necesidades/nuevaNecesidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(necesidadEjemplo)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado con id: " + necesidadEjemplo.getUsuarioId()));
    }

    @Test
    void registrarDonacion_Error400EnFeign_DeberiaDevolver400() throws Exception {
        // Forzamos que Feign lance un error 400
        FeignException.BadRequest feignException = Mockito.mock(FeignException.BadRequest.class);
        when(feignException.status()).thenReturn(400);
        doThrow(feignException).when(usuarioClient).obtenerUsuario(any(String.class));

        mockMvc.perform(post("/necesidades/nuevaNecesidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(necesidadEjemplo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Completa los campos obligatorios"));
    }

    @Test
    void registrarDonacion_ErrorGeneral_DeberiaDevolver500() throws Exception {
        // Forzamos una excepción genérica de Java en el Guardado para cubrir el último catch
        when(usuarioClient.obtenerUsuario(any(String.class))).thenReturn(null);
        doThrow(new RuntimeException("Error de BD simulado")).when(repo).save(any(NecesidadesModel.class));

        mockMvc.perform(post("/necesidades/nuevaNecesidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(necesidadEjemplo)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("error del servidor"));
    }

    // ==========================================
    // TESTS PARA DELETE (Eliminar)
    // ==========================================
    @Test
    void eliminarNecesidad_Exitoso_DeberiaDevolver200() throws Exception {
        mockMvc.perform(delete("/necesidades/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Se eleminó correctamente"));
    }

    @Test
    void eliminarNecesidad_Error_DeberiaDevolver404() throws Exception {
        // Forzamos que deleteById lance una excepción para entrar al catch
        doThrow(new RuntimeException("No encontrado")).when(repo).deleteById("1");

        mockMvc.perform(delete("/necesidades/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró"));
    }
}

