package com.necesidades.api_necesidades.repository;

import com.necesidades.api_necesidades.model.NecesidadesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NecesidadesRepository extends MongoRepository<NecesidadesModel, String> {
}
