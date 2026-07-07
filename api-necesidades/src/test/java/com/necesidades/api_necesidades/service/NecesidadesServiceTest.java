package com.necesidades.api_necesidades.service;


import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NecesidadesServiceTest {

    @Mock
    private NecesidadesRepository repo;

    @InjectMocks
    private NecesidadesService service;

    private NecesidadesModel necesidadEjemple;

    @BeforeEach
    void setUp() {
        necesidadEjemple = new NecesidadesModel();
        necesidadEjemple.setId("1");
        necesidadEjemple.setUsuarioId("69cf015746e205b1b607b33e");
        necesidadEjemple.setDesc("Test de cobertura para JaCoCo");
        necesidadEjemple.setSede("Puerto Montt");
    }

    @Test
    void listarTodas_DeberiaTraerDeBaseDatos() {
        when(repo.findAll()).thenReturn(Collections.singletonList(necesidadEjemple));

        List<NecesidadesModel> resultado = service.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Puerto Montt", resultado.get(0).getSede());
        verify(repo, times(1)).findAll();
    }

    @Test
    void guardar_DeberiaPersistirElemento() {
        when(repo.save(any(NecesidadesModel.class))).thenReturn(necesidadEjemple);

        NecesidadesModel guardado = service.guardar(necesidadEjemple);

        assertNotNull(guardado);
        assertEquals("1", guardado.getId());
        verify(repo, times(1)).save(necesidadEjemple);
    }

    @Test
    void eliminar_Exitoso() {
        doNothing().when(repo).deleteById("1");

        assertDoesNotThrow(() -> service.eliminar("1"));
        verify(repo, times(1)).deleteById("1");
    }

    @Test
    void eliminar_DeberiaLanzarExcepcionCuandoFalla() {
        doThrow(new RuntimeException("Error simulado")).when(repo).deleteById("1");

        assertThrows(RuntimeException.class, () -> service.eliminar("1"));
        verify(repo, times(1)).deleteById("1");
    }
}