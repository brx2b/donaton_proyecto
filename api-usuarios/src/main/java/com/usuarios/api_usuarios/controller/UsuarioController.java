package com.usuarios.api_usuarios.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import com.usuarios.api_usuarios.DTO.LoginRespuesta;
import com.usuarios.api_usuarios.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    private final UsuarioRepository repo;
    private final JwtService jwtService;

    public UsuarioController(UsuarioRepository repo,JwtService jwtService){
        this.repo=repo;
        this.jwtService=jwtService;
    }

    //Get devuelve todos los usuarios
    @GetMapping
    public List<UsuarioModel> listarUsuarios(){
        return repo.findAll();
    }

    //Post nuevo usuario si cumple con lo requerido del model, validaciones de jakarta
    @PostMapping("/nuevoUsuario")
    public ResponseEntity<UsuarioModel> crearUsuario(@Valid @RequestBody UsuarioModel nuevoUsuario){
        boolean emailExistente = repo.existsByEmail(nuevoUsuario.getEmail());
        if (emailExistente) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //si el email ya existe, devuelve un error 400
        }
        UsuarioModel usuarioGuardado = repo.save(nuevoUsuario);

        return ResponseEntity.ok(usuarioGuardado); //si es ok 200 se guarda el usuario luego de las validaciones
    }

    //Post para login de usuario
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UsuarioModel loginData){
        try {
            // Buscar usuario por nombre
            List<UsuarioModel> usuarios = repo.findAll();
            for (UsuarioModel usuario : usuarios) {
                if (usuario.getNombre().equals(loginData.getNombre()) &&
                    usuario.getPassword().equals(loginData.getPassword())) {
                    String token= jwtService.generarToken(usuario.getNombre()); //crear el token
                    LoginRespuesta respuesta=new LoginRespuesta(token,usuario.getNombre(),usuario.getRol(),usuario.getId()); //guarda la respuesta con el token
                    return ResponseEntity.ok(respuesta); // Login exitoso y muestra el token generado
                    
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }
    //obtiene usuario mediante la id única
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable String id){
        Optional<UsuarioModel> usuario = repo.findById(id);
        //si existe OK 200, sino not found 404
        return usuario.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }
    //Enpoint que elimina según la id del usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable String id){ //recibe como entrada la id
        try{
            //Elimina el usuario si lo encuentra
            repo.deleteById(id);
            return ResponseEntity.ok("Se ha elimado correctamente");
        }catch (Exception e){
            return ResponseEntity.status(404).body("No se ha encontrado usuario con id "+id);
        }
    }
}
