package com.donaciones.api_donaciones.controller;

import com.donaciones.api_donaciones.client.UsuarioCliente;
import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.repository.DonacionesRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones") //endpoint principal GET
public class DonacionesController {
    @Autowired
    private DonacionesRepository repo;
    @Autowired
    private UsuarioCliente usuarioClient;
    @GetMapping
    public List<DonacionesModel> listarDonaciones(){
        return repo.findAll();
    }

    @CircuitBreaker(name ="usuariosCB",fallbackMethod = "fallbackUsuarios")
    @PostMapping("/donar")
    public ResponseEntity<?> registrarDonacion(@Valid @RequestBody DonacionesModel nuevaDonacion){
        try{
            usuarioClient.obtenerUsuario(nuevaDonacion.getUsuarioId());
            return ResponseEntity.ok(repo.save(nuevaDonacion));
        }catch(Exception e){
            return ResponseEntity.status(404).body("error usuario con id "
                    + nuevaDonacion.getUsuarioId());
        }
    }
    public ResponseEntity<?> fallbackUsuarios(DonacionesModel nuevaDonacion,Exception e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error interno");
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminarDonacion(@PathVariable String id){
        try{
            repo.deleteById(id);
            return ResponseEntity.ok("Se ha eliminado correctamente la donacion");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se ha encontrado");
        }
    }

    /*/
    @PostMapping //Post nuevo usuario si cumple con lo requerido del model, validaciones de jakarta
    public ResponseEntity<UsuarioModel> crearUsuario(@Valid @RequestBody UsuarioModel nuevoUsuario){
        UsuarioModel usuarioGuardado = repo.save(nuevoUsuario);
        return ResponseEntity.ok(usuarioGuardado); //si es ok 200 se guarda el usuario luego de las validaciones
    }
    /*/
}
