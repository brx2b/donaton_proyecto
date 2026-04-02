package com.usuarios.api_usuarios.controller;
import java.util.List;
import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
