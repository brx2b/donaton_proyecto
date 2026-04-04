package com.usuarios.api_usuarios.controller;
import java.util.List;
import java.util.Optional;

import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @GetMapping //Get devuelve todos los usuarios
    public List<UsuarioModel> listarUsuarios(){
        return repo.findAll();
    }

    @PostMapping("/nuevoUsuario") //Post nuevo usuario si cumple con lo requerido del model, validaciones de jakarta
    public ResponseEntity<UsuarioModel> crearUsuario(@Valid @RequestBody UsuarioModel nuevoUsuario){
        UsuarioModel usuarioGuardado = repo.save(nuevoUsuario);
        return ResponseEntity.ok(usuarioGuardado); //si es ok 200 se guarda el usuario luego de las validaciones
    }

    @GetMapping("/{id}") //obtiene usuario mediante la id única
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable String id){
        Optional<UsuarioModel> usuario = repo.findById(id);
        //si existe OK 200, sino not found 404
        return usuario.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

}
