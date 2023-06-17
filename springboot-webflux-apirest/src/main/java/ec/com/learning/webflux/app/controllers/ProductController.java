package ec.com.learning.webflux.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService service;

	/*
	 * @GetMapping public Flux<Product> list() { return service.findAll(); }
	 */

	@GetMapping
	public Mono<ResponseEntity<Flux<Product>>> list() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(service.findAll()));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Product>> get(@PathVariable String id) {
		return service.findById(id).map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
