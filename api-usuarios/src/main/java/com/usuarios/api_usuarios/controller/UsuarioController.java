package com.usuarios.api_usuarios.controller;
import java.util.List;
import java.util.Optional;
import com.usuarios.api_usuarios.JWT.JwtUtil;
import com.usuarios.api_usuarios.model.UsuarioModel;
import com.usuarios.api_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    //Get devuelve todos los usuarios
    @GetMapping
    public List<UsuarioModel> listarUsuarios(){
        return repo.findAll();
    }

    //Post nuevo usuario si cumple con lo requerido del model, validaciones de jakarta
    @PostMapping("/nuevoUsuario")
    public ResponseEntity<UsuarioModel> crearUsuario(@Valid @RequestBody UsuarioModel nuevoUsuario){
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
                    JwtUtil.generarToken(usuario.getNombre);
                    return ResponseEntity.ok(usuario); // Login exitoso
                    
                }
            }
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
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
