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

//Define el controller
@RestController

//Enpoint principal de necesidades
@RequestMapping("/necesidades")
public class NecesidadesController {
    @Autowired
    private NecesidadesRepository repo;
    @Autowired
    private UsuarioCliente usuarioClient;

    //Get que devuelve las necesidades registradas en la bd
    @GetMapping
    public List<NecesidadesModel> listarNecesidades(){
        return repo.findAll();
    }

    //Permite registrar o postear una nueva necesidad
    @PostMapping("/nuevaNecesidad")
    public ResponseEntity<?> registrarDonacion(@Valid @RequestBody NecesidadesModel nuevaNecesidad) { //recibe la nueva necesidad para verificaciones
        try {
            //verifica si existe el id del usuario para realizar el POST
            usuarioClient.obtenerUsuario(nuevaNecesidad.getUsuarioId());
            return ResponseEntity.ok(repo.save(nuevaNecesidad)); //si existe lo guarda
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
    //endpoint que recibe el id para eliminar la necesidad
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNecesidad(@PathVariable String id){ //recibe el id como entrada
        try{
            //si existe la id elimina la necesidad (solo el que recibe de entrada en caso de tener más de 1 solo elimina el que recibe desde el BFF (1))
            repo.deleteById(id);
            return ResponseEntity.ok("Se eleminó correctamente");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se encontró");
        }
    }
}
