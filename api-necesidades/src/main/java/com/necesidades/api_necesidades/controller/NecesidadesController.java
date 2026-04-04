package com.necesidades.api_necesidades.controller;

import com.necesidades.api_necesidades.client.UsuarioCliente;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/necesidades")
public class NecesidadesController {
    @Autowired
    private NecesidadesRepository repo;
    @Autowired
    private UsuarioCliente usuarioClient;
    @GetMapping
    public List<NecesidadesModel> listarNecesidades(){
        return repo.findAll();
    }
    @PostMapping("/nuevaNecesidad")
    public ResponseEntity<?> registrarDonacion(@Valid @RequestBody NecesidadesModel nuevaNecesidad) {
        try {
            usuarioClient.obtenerUsuario(nuevaNecesidad.getUsuarioId());
            return ResponseEntity.ok(repo.save(nuevaNecesidad));
        } catch (Exception e) {
            String error = e.getMessage().substring(1,4);
            if(error.equals("404")){
                return ResponseEntity.status(404).body("Usuario no encontrado con id: "+nuevaNecesidad.getUsuarioId());
            }
            if(error.equals("400")){
                return ResponseEntity.status(400).body("Completa los campos obligatorios");
            }
            return ResponseEntity.status(500).body("error del servidor");
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNecesidad(@PathVariable String id){
        try{
            repo.deleteById(id);
            return ResponseEntity.ok("Se eleminó correctamente");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se encontró");
        }
    }
}
/*/
@DeleteMapping("/{matricula}")
    public ResponseEntity<?> eliminarEncargo(@PathVariable String matricula){

        try{
            String matri = matricula.toUpperCase();
            if(!repo.existsByMatricula(matri)){
                return ResponseEntity.status(404).body("No se encontro en circulacion");
            }
            repo.deleteByMatricula(matri);
            return ResponseEntity.ok("Viaje eliminado");
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno del servidor");
        }

    }

 /*/