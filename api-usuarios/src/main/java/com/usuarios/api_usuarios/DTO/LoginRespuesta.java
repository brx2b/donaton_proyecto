package com.usuarios.api_usuarios.DTO;

import lombok.Data;

//DTO para creación del token más que nada auxiliar
public class LoginRespuesta {
    private String token;
    private String usuario;

    public LoginRespuesta(String token, String usuario){
        this.token=token;
        this.usuario=usuario;
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
