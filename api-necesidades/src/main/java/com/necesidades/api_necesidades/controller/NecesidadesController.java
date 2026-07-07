package com.necesidades.api_necesidades.controller;

import com.necesidades.api_necesidades.client.UsuarioCliente;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import com.necesidades.api_necesidades.service.NecesidadesService;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Define el controller
@RestController
@RequestMapping("/necesidades")
public class NecesidadesController {

    @Autowired
    private NecesidadesService necesidadesService; // <-- Cambiado por el servicio

    @Autowired
    private UsuarioCliente usuarioClient;

    // Get que devuelve las necesidades registradas (Ahora usa Redis)
    @GetMapping
    public List<NecesidadesModel> listarNecesidades(){
        return necesidadesService.listarTodas();
    }

    // Permite registrar o postear una nueva necesidad
    @PostMapping("/nuevaNecesidad")
    public ResponseEntity<?> registrarDonacion(@Valid @RequestBody NecesidadesModel nuevaNecesidad) {
        try {
            usuarioClient.obtenerUsuario(nuevaNecesidad.getUsuarioId());
            // Guarda usando el servicio para limpiar el caché
            NecesidadesModel guardada = necesidadesService.guardar(nuevaNecesidad);
            return ResponseEntity.ok(guardada);
        } catch (FeignException e) {
            if (e.status() == 404) {
                return ResponseEntity.status(404).body("Usuario no encontrado con id: "+nuevaNecesidad.getUsuarioId());
            }
            if (e.status() == 403) {
                return ResponseEntity.status(403).body("Acceso denegado al servicio de usuarios");
            }
            if (e.status() == 400) {
                return ResponseEntity.status(400).body("Completa los campos obligatorios");
            }
            return ResponseEntity.status(e.status() <= 0 ? 500 : e.status()).body("Error de comunicación con usuarios: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error del servidor");
        }
    }

    // Endpoint que recibe el id para eliminar la necesidad
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNecesidad(@PathVariable String id){
        try{
            // Elimina usando el servicio para invalidar el caché viejo
            necesidadesService.eliminar(id);
            return ResponseEntity.ok("Se eliminó correctamente");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se encontró");
        }
    }
}
