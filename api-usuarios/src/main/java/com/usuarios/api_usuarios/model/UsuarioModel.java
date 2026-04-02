package com.usuarios.api_usuarios.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection ="usuarios")
@Data
@NoArgsConstructor

public class UsuarioModel {
    @Id
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String ubicacion;
}
