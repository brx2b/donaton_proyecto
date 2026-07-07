package com.necesidades.api_necesidades.service;
import com.necesidades.api_necesidades.model.NecesidadesModel;
import com.necesidades.api_necesidades.repository.NecesidadesRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NecesidadesService {
    private final NecesidadesRepository repo;

    public NecesidadesService(NecesidadesRepository repo) {
        this.repo = repo;
    }

    // Al usar un GET general, guardamos la lista con una clave estática (por ejemplo, 'todas')
    @Cacheable(value = "necesidades", key = "'todas'")
    public List<NecesidadesModel> listarTodas() {
        System.out.println("======> CACHE MISS: Buscando necesidades en la base de datos...");
        return repo.findAll();
    }

    // Si se guarda una nueva necesidad, limpiamos el caché de 'necesidades'
    @CacheEvict(value = "necesidades", allEntries = true)
    public NecesidadesModel guardar(NecesidadesModel nuevaNecesidad) {
        return repo.save(nuevaNecesidad);
    }

    // Si se elimina, también limpiamos el caché para evitar datos fantasma
    @CacheEvict(value = "necesidades", allEntries = true)
    public void eliminar(String id) {
        repo.deleteById(id);
    }
}
