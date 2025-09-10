package com.aej.cecarticulo.dao;

import com.aej.cecarticulo.model.ArticuloModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends MongoRepository<ArticuloModel, String> {
    // Repositorio para manejar las operaciones CRUD en la colección de artículos
}