package com.inventario.api_inventario.controller;

import com.inventario.api_inventario.model.InventarioModel;
import com.inventario.api_inventario.repository.InventarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
public class InventarioController {
    @Autowired
    private InventarioRepository repo;

    @GetMapping
    public List<InventarioModel> listarInventario(){
        return repo.findAll();
    }
    @GetMapping("/nombre")
    public List<InventarioModel> encontrarSede(@RequestParam String nombre){
        return repo.findBySede(nombre);
    }
    @PostMapping
    public ResponseEntity<?> registrarInventario(@Valid @RequestBody InventarioModel nuevoInventario){
        try{
            List<InventarioModel> nombreSede = repo.findBySede(nuevoInventario.getSede());
            if(nombreSede.isEmpty()){
                return ResponseEntity.ok(repo.save(nuevoInventario));
            }
            return ResponseEntity.ok("Ya existe el nombre de sede");
        }catch (Exception e){
            return ResponseEntity.status(404).body("error :V");
        }
    }
    @DeleteMapping("/{id}")
    private ResponseEntity<?> eliminarInventario(@PathVariable String id){
        repo.deleteById(id);
        return ResponseEntity.ok("Inventario eliminado");

    } //verificacion si no existe la id


}
