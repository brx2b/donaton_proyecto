package com.usuarios.api_usuarios.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
@Service
public class JwtService {
    //Creacion de la cadena de texto para el token
    private static final String cadena_secreta="LLXXÑ2#CADENASEGURA67671Ñ¬SUPERMEGASEGURA2026";
    //encriptado del secreto
    private final SecretKey secretKey= Keys.hmacShaKeyFor(cadena_secreta.getBytes());

    private static final long expiracion_date=1000 * 60 * 60 * 24; //duracion de la secret key

    //genera el token
    public String generarToken(String usuario){
        return Jwts.builder()
                .subject(usuario) //objetivo = el suario
                .issuedAt(new Date(System.currentTimeMillis()+expiracion_date)) //fecha limite
                .signWith(secretKey) //login con clave secreta
                .compact();
    }

    //Metodo de verificacion
    public boolean isTokenValid(String token, String detallesUsuario){
        final String usuario=extractUsuario(token);
        return (usuario.equals(detallesUsuario)) && !tokenVencido(token);
    }
    public String extractUsuario(String token){
        return extractAllClaims(token).getSubject();
    }
    private boolean tokenVencido(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
