package com.necesidades.api_necesidades.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.client.UsuarioCliente;
import com.necesidades.api_necesidades.service.NecesidadesService; // <-- Asegúrate de importar tu servicio
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.necesidades.api_necesidades.config.TestCacheConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(NecesidadesController.class)
@Import(TestCacheConfiguration.class)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.data.redis.host=localhost",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration"
})
public class NecesidadesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // REEMPLAZADO: Ahora mockeamos el servicio que tiene la lógica de Redis, no el repo.
    @MockitoBean
    private NecesidadesService necesidadesService;

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
        // Apunta al método del servicio
        when(necesidadesService.listarTodas()).thenReturn(Collections.singletonList(necesidadEjemplo));

        mockMvc.perform(get("/necesidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1")) // Eliminado el casteo molesto
                .andExpect(jsonPath("$[0].sede").value("Puerto Montt"));
    }

    // ==========================================
    // TESTS PARA POST (Registrar)
    // ==========================================
    @Test
    void registrarDonacion_Exitoso_DeberiaDevolver200() throws Exception {
        when(usuarioClient.obtenerUsuario(necesidadEjemplo.getUsuarioId())).thenReturn(null);
        // Apunta al método del servicio
        when(necesidadesService.guardar(any(NecesidadesModel.class))).thenReturn(necesidadEjemplo);

        mockMvc.perform(post("/necesidades/nuevaNecesidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(necesidadEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void registrarDonacion_UsuarioNoExiste_DeberiaDevolver404() throws Exception {
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
        when(usuarioClient.obtenerUsuario(any(String.class))).thenReturn(null);
        when(necesidadesService.guardar(any(NecesidadesModel.class))).thenThrow(new RuntimeException("Error de BD simulado"));

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
                .andExpect(content().string("Se eliminó correctamente"));
    }

    @Test
    void eliminarNecesidad_Error_DeberiaDevolver404() throws Exception {
        // Para métodos void se usa esta estructura: doThrow().when(objeto).metodoVoid()
        doThrow(new RuntimeException("No encontrado"))
                .when(necesidadesService).eliminar("1");

        mockMvc.perform(delete("/necesidades/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró"));
    }
}

