package ec.com.learning.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.com.learning.webflux.app.models.dao.ProductDao;
import ec.com.learning.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

	@Autowired
	private ProductDao dao;

	private static final Logger log = LoggerFactory.getLogger(ProductRestController.class);

	@GetMapping
	public Flux<Product> index() {
		Flux<Product> products = dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		}).doOnNext(prod -> log.info(prod.getName()));

		return products;
	}

	@GetMapping("/{id}")
	public Mono<Product> show(@PathVariable String id) {
		// return dao.findById(id);
		Flux<Product> products = dao.findAll();
		Mono<Product> product = products.filter(p -> p.getId().equals(id)).next()
				.doOnNext(prod -> log.info(prod.getName()));
		return product;
	}

}
