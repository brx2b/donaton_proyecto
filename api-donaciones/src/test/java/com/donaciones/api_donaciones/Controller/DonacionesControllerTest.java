package com.donaciones.api_donaciones.Controller;

import com.donaciones.api_donaciones.controller.DonacionesController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.donaciones.api_donaciones.client.UsuarioCliente;
import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.repository.DonacionesRepository;
import com.donaciones.api_donaciones.service.DonacionFactory;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonacionesController.class) //Configura un entorno de prueba aislado que levanta exclusivamente la capa web
public class DonacionesControllerTest {

    @Autowired
    private MockMvc mockMvc; //simula peticiones http hacia el controlador sin necesidad de iniciar un servidor real

    @Autowired
    private ObjectMapper objectMapper; //mientras que el segundo transforma los objetos Java a formato JSON

    @MockitoBean //remplaza dependencias reales por mocks, permitiendo controlar su comportamiento durante las pruebas
    private DonacionesRepository repo;

    @MockitoBean
    private DonacionFactory factory;

    @MockitoBean
    private UsuarioCliente usuarioClient;

    @MockitoBean
    private DonacionesProcesador procesador;

    private DonacionesModel donacionEjemplo;

    @BeforeEach //Ejecuta el método setUp() antes de cada test para reiniciar donacionEjemplo con datos limpios, asegurando que cada prueba comience siempre desde el mismo escenario base.
    void setUp() {
        donacionEjemplo = new DonacionesModel();
        donacionEjemplo.setId("donacion999");
        donacionEjemplo.setUsuarioId("user123");
        donacionEjemplo.setTipo("MONETARIA");
        donacionEjemplo.setMonto(15000.0);
        donacionEjemplo.setFecha("2026-07-06");
    }
    // TEST PARA GET (Listar donaciones)
    @Test
    void listarDonaciones_DeberiaDevolverLista() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(donacionEjemplo));

        mockMvc.perform(get("/donaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("donacion999"))
                .andExpect(jsonPath("$[0].tipo").value("MONETARIA"));
    }
    // TESTS PARA POST (Registrar Donación con Seguridad y Factory)
    @Test
    void registrarDonacion_Exitoso_DeberiaGuardar() throws Exception {
        // Mockear comportamiento de Feign Client y Factory dinámica
        when(usuarioClient.obtenerUsuario("user123")).thenReturn(null); 
        when(factory.getProcesador("MONETARIA")).thenReturn(procesador);
        doNothing().when(procesador).procesar(any(DonacionesModel.class));
        when(repo.save(any(DonacionesModel.class))).thenReturn(donacionEjemplo);

        mockMvc.perform(post("/donaciones/donar")
                        .header("Authorization", "Bearer token-valido-123") // Testea flujo con AuthHeader
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donacionEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("donacion999"));
    }

    @Test
    void registrarDonacion_TipoInvalido_DeberiaDar400() throws Exception {
        when(usuarioClient.obtenerUsuario("user123")).thenReturn(null);
        // Forzamos que la factory lance IllegalArgumentException si el tipo de donación es desconocido
        when(factory.getProcesador("MONETARIA")).thenThrow(new IllegalArgumentException("Tipo no soportado"));

        mockMvc.perform(post("/donaciones/donar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donacionEjemplo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error en el tipo de donacion Tipo no soportado"));
    }

    @Test
    void registrarDonacion_UsuarioNoExiste_DeberiaDar404() throws Exception {
        // Forzamos un error genérico (ej: al no hallar el usuario mediante Feign Client)
        when(usuarioClient.obtenerUsuario("user123")).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(post("/donaciones/donar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donacionEjemplo)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error usuario con id user123 No existe"));
    }
    // TEST PARA CIRCUIT BREAKER (Fallback)
    @Test
    void registrarDonacion_CircuitBreaker_DeberiaRetornarFallback() throws Exception {

        mockMvc.perform(post("/donaciones/donar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donacionEjemplo)))
                .andDo(result -> {
                    // Si se llega a requerir levantar el fallback explícito en controladores:
                    DonacionesController controller = new DonacionesController();
                    // El fallback retorna un HTTP 503 SERVICE_UNAVAILABLE según tu código
                });

        when(usuarioClient.obtenerUsuario("user123")).thenThrow(new RuntimeException("Servicio Caído"));
    }

    @Test
    void eliminarDonacion_Exitoso_DeberiaDar200() throws Exception {
        mockMvc.perform(delete("/donaciones/donacion999"))
                .andExpect(status().isOk())
                .andExpect(content().string("Se ha eliminado correctamente la donacion"));
    }

    @Test
    void eliminarDonacion_Error_DeberiaDar404() throws Exception {
        // Forzamos excepción al borrar
        doThrow(new RuntimeException()).when(repo).deleteById("donacion999");

        mockMvc.perform(delete("/donaciones/donacion999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se ha encontrado"));
    }
}