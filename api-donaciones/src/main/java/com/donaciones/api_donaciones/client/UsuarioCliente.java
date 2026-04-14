package com.donaciones.api_donaciones.client;

import com.donaciones.api_donaciones.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//apunta a el servicio en docker ms-usuarios no localhost
@FeignClient(name = "api-usuarios",url = "http://ms-usuarios:8081/usuarios")
public interface UsuarioCliente {
    @GetMapping("/{id}")
    UsuarioDto obtenerUsuario(@PathVariable("id") String id);
}
