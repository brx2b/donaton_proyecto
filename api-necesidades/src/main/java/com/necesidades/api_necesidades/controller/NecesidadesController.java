package com.necesidades.api_necesidades.controller;

import com.necesidades.api_necesidades.client.UsuarioCliente;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private List<NecesidadesModel> listarNecesidades(){
        return repo.findAll();
    }
    @PostMapping
    public ResponseEntity<?> registrarDonacion(@RequestBody NecesidadesModel nuevaNecesidad){
        try{
            usuarioClient.obtenerUsuario(nuevaNecesidad.getUsuarioId());
            return ResponseEntity.ok(repo.save(nuevaNecesidad));
        }catch(Exception e){
            return ResponseEntity.status(404).body("error usuario con id "
                    + nuevaNecesidad.getUsuarioId() + "no existe");
        }
    }


}
