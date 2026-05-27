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

//Se define controller
@RestController
//enpoint principal
@RequestMapping("/logistica")
public class LogisticaController {
    @Autowired
    private LogisticaRepository repo;

    //devuelve las logisticas de la bd
    @GetMapping
    public List<LogisticaModel> listarLogistica(){
        return repo.findAll();
    }

    //Permite ingresar id y buscar logistica especifica
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id){ //Recibe el id
        Optional<LogisticaModel> logistica = repo.findById(id);
        return ResponseEntity.ok(logistica);
    }
    //Enpoint que permite ingresar una nueva logitstica
    @PostMapping("/nuevaLogistica")
    public ResponseEntity<?> RegistrarLogistica(@Valid @RequestBody LogisticaModel nuevaLogistica){ //valida los campos
        try{
            List<LogisticaModel> res = repo.findByMatricula(nuevaLogistica.getMatricula()); //Verifica si la matricula ya se encuentra registrada (envio en curso/sin disponibilidad)
            if(res.isEmpty()){
                return ResponseEntity.ok(repo.save(nuevaLogistica)); //Lo guarda si la matricula no esta en logistica
            }else{
                return ResponseEntity.ok("Ya existe la matricula en circulacion");
            }
        }catch (Exception e){
            return ResponseEntity.ok("Ha ocurrido un problema = "+e.getMessage());
        }
    }
    //Endpoint para eliminar logistica mediante la matricula del vehiculo (al marcar llegada al destino)
    @DeleteMapping("/{matricula}")
    public ResponseEntity<?> eliminarEncargo(@PathVariable String matricula){ //recibe la matricula como entrada
        try{
            String matri = matricula.toUpperCase(); //Convierte el texto a mayus
            if(!repo.existsByMatricula(matri)){
                return ResponseEntity.status(404).body("No se encontro en circulacion"); //si no se encuentra la matricula en logistica
            }
            repo.deleteByMatricula(matri); //si lo encuentra lo elimina
            return ResponseEntity.ok("Viaje eliminado");
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno del servidor");
        }

    }
}
