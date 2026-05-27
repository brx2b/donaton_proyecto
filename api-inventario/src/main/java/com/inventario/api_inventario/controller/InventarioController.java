package com.inventario.api_inventario.controller;

import com.inventario.api_inventario.model.InventarioModel;
import com.inventario.api_inventario.repository.InventarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//se define controller
@RestController
//endpoint principal
@RequestMapping("/inventario")
public class InventarioController {
    @Autowired
    private InventarioRepository repo;

    //endpoint que devuelve los inventarios
    @GetMapping
    public List<InventarioModel> listarInventario(){
        return repo.findAll();
    }

    //enpoint que muestra el inventario buscando por su nombre (sede)
    @GetMapping("/nombre")
    public List<InventarioModel> encontrarSede(@RequestParam String nombre){
        return repo.findBySede(nombre);
    }

    //Endpoint que permite ingresar un nuevo inventario
    @PostMapping("/nuevoInventario")
    public ResponseEntity<?> registrarInventario(@Valid @RequestBody InventarioModel nuevoInventario){
        try{
            List<InventarioModel> nombreSede = repo.findBySede(nuevoInventario.getSede()); //busca las sedes de los inventarios para verificar
            if(nombreSede.isEmpty()){
                return ResponseEntity.ok(repo.save(nuevoInventario));//si la sede no existe lo guarda
            }
            return ResponseEntity.ok("La sede ya se encuentra registrada");
        }catch (Exception e){
            return ResponseEntity.status(404).body("Ocurrió un problema verifica los campos");
        }
    }
    //Busca la id del inventario para eliminarlo
    @DeleteMapping("/{id}")
    private ResponseEntity<?> eliminarInventario(@PathVariable String id){ //obtiene el id
        try {
            repo.deleteById(id); //si lo encuentra lo elimina completamente
            return ResponseEntity.ok("Inventario eliminado");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se encontro inventario con id "+id);
        }
    }


}
