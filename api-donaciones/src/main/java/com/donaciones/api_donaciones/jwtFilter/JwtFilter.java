package com.donaciones.api_donaciones.JwtFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

//filtrar
@Component
public class JwtFilter implements Filter {
    @Override  //metodo principal del filtro que ejecuta automaticamente cada solicitud
    public void doFilter(
            ServletRequest req, //Solicitud generica
            ServletResponse res, //respuesta generica
            FilterChain chain //permite continuar la cadena de filtros
            //Excepciones en caso de error
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        //Convertir la solicitud a http
        String token=request.getHeader("Authorization");

        //Validacion
        if(token==null){
            ((HttpServletResponse)res).sendError(401);
        }
        System.out.print("EL TOKEN"+token);
        //Quitar palabra bearer
        token=token.replace("Bearer ","");
        System.out.print(token);
        //Decpdofocar
        String datos = new String(Base64.getDecoder().decode(token));
        //VALIDA SI EL ROL ES ADMIN
        if(!datos.contains("ADMIN")){
            ((HttpServletResponse)res).sendError(403);
        }
        //Continua la cadena de solicitud
        chain.doFilter(req,res);
    }

}