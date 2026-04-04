package com.inventario.api_inventario.repository;

import com.inventario.api_inventario.model.InventarioModel;
import com.mongodb.client.MongoClient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventarioRepository extends MongoRepository<InventarioModel, String> {
    List<InventarioModel> findBySede(String sede);
}
