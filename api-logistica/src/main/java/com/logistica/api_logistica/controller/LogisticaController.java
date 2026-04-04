package com.logistica.api_logistica.controller;

import com.logistica.api_logistica.model.LogisticaModel;
import com.logistica.api_logistica.repository.LogisticaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/logistica")
public class LogisticaController {
    @Autowired
    private LogisticaRepository repo;
    @GetMapping
    public List<LogisticaModel> listarLogistica(){
        return repo.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id){
        Optional<LogisticaModel> logistica = repo.findById(id);
        return ResponseEntity.ok(logistica);
    }

    @PostMapping
    public ResponseEntity<?> RegistrarLogistica(@Valid @RequestBody LogisticaModel nuevaLogistica){
        try{
            List<LogisticaModel> res = repo.findByMatricula(nuevaLogistica.getMatricula());
            if(res.isEmpty()){
                return ResponseEntity.ok(repo.save(nuevaLogistica));
            }else{
                return ResponseEntity.ok("Ya existe la matricula en circulacion");
            }
        }catch (Exception e){
            return ResponseEntity.ok("Ha ocurrido un problema = "+e.getMessage());
        }
    }
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
}
