package com.usuarios.api_usuarios.controller;


import com.usuarios.api_usuarios.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private UsuarioRepository repo;

    @GetMapping
    public String probarConexion() {
        repo.count(); // Esto obliga a Spring a conectarse a Atlas
        return "Conexión exitosa a MongoDB Atlas";
    }
}
