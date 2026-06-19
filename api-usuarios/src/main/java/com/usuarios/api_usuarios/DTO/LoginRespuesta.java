package com.usuarios.api_usuarios.DTO;

import lombok.Data;

//DTO para creación del token más que nada auxiliar
public class LoginRespuesta {
    private String token;
    private String usuario;
    private String rol;
    private String id;
    public LoginRespuesta(String token, String usuario,String rol,String id){

        this.token=token;
        this.usuario=usuario;
        this.rol=rol;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
