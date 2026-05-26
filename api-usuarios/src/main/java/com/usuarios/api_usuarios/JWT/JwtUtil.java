package com.usuarios.api_usuarios.JWT;
import java.util.Base64;

//Clase utilitaria para crear tokens
public class JwtUtil {
    //metodo estatico para crear tokens
    public static String generarToken( String usuario){
        //Datos simples con los que se genera el token
        String datos=usuario + ":ADMIN";

        //Codificar los datos con Base64
        return Base64.getEncoder().encodeToString(datos.getBytes());
        //1 datos convierto el String a bytes //datos.getBytes()
        //2 convierto byes a texto base64
        //3 Encoder es el objeto que transforma los datos


    }
}