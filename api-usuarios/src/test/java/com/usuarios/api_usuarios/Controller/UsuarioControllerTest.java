package com.usuarios.api_usuarios.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.usuarios.api_usuarios.controller.UsuarioController;
import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import com.usuarios.api_usuarios.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository repo;

    @MockitoBean
    private JwtService jwtService;

    private UsuarioModel usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new UsuarioModel();
        usuarioEjemplo.setId("user123");
        usuarioEjemplo.setNombre("Brian");
        usuarioEjemplo.setPassword("secreto123");
        usuarioEjemplo.setEmail("brian@correo.com");
        usuarioEjemplo.setRol("ADMIN");

        // 💡 NOTA: Si tu UsuarioModel tiene más campos obligatorios con validaciones (@NotBlank),
        // inicialízalos aquí igual que hicimos con los microservicios anteriores.
    }

    // ==========================================
    // TESTS PARA GET (Listar todos)
    // ==========================================
    @Test
    void listarUsuarios_DeberiaDevolverLista() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(usuarioEjemplo));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user123"))
                .andExpect(jsonPath("$[0].nombre").value("Brian"));
    }

    // ==========================================
    // TESTS PARA POST (Crear Usuario)
    // ==========================================
    @Test
    void crearUsuario_Exitoso_DeberiaGuardar() throws Exception {
        when(repo.existsByEmail("brian@correo.com")).thenReturn(false);
        when(repo.save(any(UsuarioModel.class))).thenReturn(usuarioEjemplo);

        mockMvc.perform(post("/usuarios/nuevoUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"));
    }

    @Test
    void crearUsuario_EmailExistente_DeberiaDar409() throws Exception {
        // Forzamos el flujo donde el email ya existe en la base de datos
        when(repo.existsByEmail("brian@correo.com")).thenReturn(true);

        mockMvc.perform(post("/usuarios/nuevoUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo)))
                .andExpect(status().isConflict()); // Valida HTTP 409 Conflict
    }

    // ==========================================
    // TESTS PARA POST (Login de Usuario)
    // ==========================================
    @Test
    void loginUsuario_Exitoso_DeberiaRetornarToken() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(usuarioEjemplo));
        when(jwtService.generarToken("Brian")).thenReturn("token-jwt-falso-de-prueba");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-falso-de-prueba"))
                .andExpect(jsonPath("$.usuario").value("Brian"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.id").value("user123"));
    }

    @Test
    void loginUsuario_CredencialesIncorrectas_DeberiaDar41() throws Exception {
        when(repo.findAll()).thenReturn(Collections.singletonList(usuarioEjemplo));

        // Creamos una data de login que no va a coincidir con la de la BD simulada
        UsuarioModel loginDataIncorrecta = new UsuarioModel();
        loginDataIncorrecta.setNombre("Brian");
        loginDataIncorrecta.setPassword("clave-erronea-123");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDataIncorrecta)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales incorrectas"));
    }

    @Test
    void loginUsuario_ErrorServidor_DeberiaDar500() throws Exception {
        // Forzamos una excepción al interactuar con el repositorio para cubrir el catch
        when(repo.findAll()).thenThrow(new RuntimeException("Error fatal"));

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error en el servidor: Error fatal"));
    }

    // ==========================================
    // TESTS PARA GET BY ID
    // ==========================================
    @Test
    void obtenerPorId_Existe_DeberiaDar200() throws Exception {
        when(repo.findById("user123")).thenReturn(Optional.of(usuarioEjemplo));

        mockMvc.perform(get("/usuarios/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"));
    }

    @Test
    void obtenerPorId_NoExiste_DeberiaDar404() throws Exception {
        when(repo.findById("user123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/user123"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // TESTS PARA DELETE
    // ==========================================
    @Test
    void eliminarPorId_Exitoso_DeberiaDar200() throws Exception {
        mockMvc.perform(delete("/usuarios/user123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Se ha elimado correctamente"));
    }

    @Test
    void eliminarPorId_Error_DeberiaDar404() throws Exception {
        // Forzamos un error en deleteById para entrar al catch
        doThrow(new RuntimeException()).when(repo).deleteById("user123");

        mockMvc.perform(delete("/usuarios/user123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se ha encontrado usuario con id user123"));
    }
}