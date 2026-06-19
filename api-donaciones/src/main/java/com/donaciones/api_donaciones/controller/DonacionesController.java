package com.donaciones.api_donaciones.controller;

import com.donaciones.api_donaciones.client.UsuarioCliente;
import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.repository.DonacionesRepository;
import com.donaciones.api_donaciones.service.DonacionFactory;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//define clase como controller
@RestController
@RequestMapping("/donaciones") //endpoint principal GET
public class DonacionesController {
    @Autowired
    private DonacionesRepository repo;

    @Autowired
    private DonacionFactory factory; //injeccion factory

    @Autowired
    private UsuarioCliente usuarioClient;
    @GetMapping
    public List<DonacionesModel> listarDonaciones(){
        return repo.findAll();
    }  //GET de las donaciones
    //Circuitbreaker con metodo en caso de muchas solicitudes fallidas (application.yml configuración usuariosCB)
    @CircuitBreaker(name ="usuariosCB",fallbackMethod = "fallbackUsuarios")
    //Endpoint POST para registrar nueva donacion en la BD
    @PostMapping("/donar")
    //Valida los campos ingresador para registrar con @Valid dentro del modelo
    public ResponseEntity<?> registrarDonacion(
            @Valid @RequestBody DonacionesModel nuevaDonacion,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        try{
            // Pasar el token a través de ThreadLocal para que el Feign interceptor lo pueda acceder
            if (authHeader != null) {
                com.donaciones.api_donaciones.config.FeignConfiguration.setAuthorizationToken(authHeader);
                System.out.println("📤 [DonacionesController] Token guardado en ThreadLocal: " + authHeader.substring(0, Math.min(authHeader.length(), 20)) + "...");
            }
            
            usuarioClient.obtenerUsuario(nuevaDonacion.getUsuarioId()); //valida si existe el user
            DonacionesProcesador procesador= factory.getProcesador(nuevaDonacion.getTipo());

            procesador.procesar(nuevaDonacion);
            return ResponseEntity.ok(repo.save(nuevaDonacion)); //si responde 200 se guarda
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("Error en el tipo de donacion "+e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(404).body("error usuario con id "
                    + nuevaDonacion.getUsuarioId()+" "+e.getMessage());
        }
        finally {
            // Limpiar el ThreadLocal después de usar
            com.donaciones.api_donaciones.config.FeignConfiguration.clearAuthorizationToken();
        }
    }
    //Fallback de circuitbreaker al fallar la nueva donacion
    public ResponseEntity<?> fallbackUsuarios(DonacionesModel nuevaDonacion,Exception e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error interno");
    }
    //Endpoint para eliminar donacion mediante la id
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminarDonacion(@PathVariable String id){ //recibe el id
        try{
            repo.deleteById(id);
            return ResponseEntity.ok("Se ha eliminado correctamente la donacion");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se ha encontrado");
        }
    }

}
