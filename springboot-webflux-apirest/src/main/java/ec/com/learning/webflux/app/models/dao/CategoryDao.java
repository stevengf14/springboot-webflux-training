package ec.com.learning.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import ec.com.learning.webflux.app.models.documents.Category;

public interface CategoryDao extends ReactiveMongoRepository<Category, String>{

}
