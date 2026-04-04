package com.usuarios.api_usuarios.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection ="usuarios")
@Data

public class UsuarioModel {
    @Id
    private String id;
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @Email(message = "Formato de email invalido!")
    @NotBlank(message = "el email es obligatorio!")
    private String email;
    private String rol;
    private String ubicacion;
}
