package com.logistica.api_logistica.repository;

import com.logistica.api_logistica.model.LogisticaModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LogisticaRepository extends MongoRepository <LogisticaModel,String> {
    List<LogisticaModel> findByMatricula(String matricula);
    List<LogisticaModel> deleteByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
}
