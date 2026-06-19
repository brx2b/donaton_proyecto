package com.donaciones.api_donaciones.client;

import com.donaciones.api_donaciones.config.FeignConfiguration;
import com.donaciones.api_donaciones.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//apunta a el servicio en docker ms-usuarios no localhost
@FeignClient(
        name = "api-gateway",
        url = "http://api-gateway:8086/usuarios", // Removido /api - endpoint correcto
        configuration = com.donaciones.api_donaciones.config.FeignConfiguration.class
)
public interface UsuarioCliente {

    @GetMapping("/{id}")
    UsuarioDto obtenerUsuario(@PathVariable("id") String id);
}