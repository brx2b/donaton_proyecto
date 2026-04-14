package com.donaciones.api_donaciones.controller;

import com.donaciones.api_donaciones.client.UsuarioCliente;
import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.repository.DonacionesRepository;
import com.donaciones.api_donaciones.service.DonacionFactory;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
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
    private DonacionFactory factory; //injeccion factory

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
            usuarioClient.obtenerUsuario(nuevaDonacion.getUsuarioId()); //valida si existe el user
            DonacionesProcesador procesador= factory.getProcesador(nuevaDonacion.getTipo());

            procesador.procesar(nuevaDonacion);
            return ResponseEntity.ok(repo.save(nuevaDonacion));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("Error en el tipo de donacion "+e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(404).body("error usuario con id "
                    + nuevaDonacion.getUsuarioId()+" "+e.getMessage());
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

}
