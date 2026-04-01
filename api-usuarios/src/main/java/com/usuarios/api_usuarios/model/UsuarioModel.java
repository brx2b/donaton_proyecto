package com.usuarios.api_usuarios.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection ="usuarios")
public class UsuarioModel {
    @Id
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String ubicacion;
}
