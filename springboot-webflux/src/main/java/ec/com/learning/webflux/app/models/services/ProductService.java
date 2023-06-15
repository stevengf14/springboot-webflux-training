package ec.com.learning.webflux.app.models.services;

import ec.com.learning.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

	public Flux<Product> findAll();

	public Flux<Product> findAllWithUpperCaseName();

	public Flux<Product> findAllWithUpperCaseNameRepeat();

	public Mono<Product> findById(String id);

	public Mono<Product> save(Product product);

	public Mono<Void> delete(Product product);
}
