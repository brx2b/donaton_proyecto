package com.donaciones.api_donaciones.controller;

import com.donaciones.api_donaciones.client.UsuarioCliente;
import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.repository.DonacionesRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private List<DonacionesModel> listarDonaciones(){
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> registrarDonacion(@Valid @RequestBody DonacionesModel nuevaDonacion){
        try{
            usuarioClient.obtenerUsuario(nuevaDonacion.getUsuarioId());
            return ResponseEntity.ok(repo.save(nuevaDonacion));
        }catch(Exception e){
            return ResponseEntity.status(404).body("error usuario con id "
                    + nuevaDonacion.getUsuarioId());
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
