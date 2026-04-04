package com.necesidades.api_necesidades.client;


import com.necesidades.api_necesidades.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-usuarios",url = "http://localhost:8081/usuarios")
public interface UsuarioCliente {
    @GetMapping("/{id}")
    UsuarioDto obtenerUsuario(@PathVariable("id") String id);
}
