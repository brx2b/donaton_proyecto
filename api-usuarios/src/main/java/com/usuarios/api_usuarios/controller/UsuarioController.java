package com.usuarios.api_usuarios.controller;
import java.util.List;
import java.util.Optional;

import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository repo;
    @GetMapping
    public List<UsuarioModel> listarUsuarios(){
        return repo.findAll();
    }
    @PostMapping
    public UsuarioModel crearUsuario(@RequestBody UsuarioModel nuevoUsuario){
        return repo.save(nuevoUsuario);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable String id){
        Optional<UsuarioModel> usuario = repo.findById(id);
        //si existe OK 200, sino not found 404
        return usuario.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }
}
