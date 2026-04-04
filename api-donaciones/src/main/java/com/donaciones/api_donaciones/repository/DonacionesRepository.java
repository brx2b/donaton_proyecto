package com.donaciones.api_donaciones.repository;

import com.donaciones.api_donaciones.model.DonacionesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DonacionesRepository extends MongoRepository<DonacionesModel,String> {
}
