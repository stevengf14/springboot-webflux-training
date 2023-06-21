package ec.com.learning.webflux.app.controllers;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService service;

	@Value("${config.uploads.path}")
	private String path;

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

	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> create(@Valid @RequestBody Mono<Product> monoProduct) {
		Map<String, Object> response = new HashMap<String, Object>();
		return monoProduct.flatMap(product -> {
			if (product.getCreateAt() == null) {
				product.setCreateAt(new Date());
			}
			return service.save(product).map(p -> {
				response.put("product", p);
				response.put("message", "Product created successfully");
				response.put("timestamp", new Date());
				response.put("status", HttpStatus.OK.value());
				return ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))

						.contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
			});
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class).flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "The field " + fieldError.getField().toUpperCase() + " " + fieldError.getDefaultMessage())
					.collectList().flatMap(list -> {
						response.put("errors", list);
						response.put("timestamp", new Date());
						response.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(response));
					});
		});
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Product>> edit(@RequestBody Product product, @PathVariable String id) {
		return service.findById(id).flatMap(p -> {
			p.setName(product.getName());
			p.setPrice(product.getPrice());
			p.setCategory(product.getCategory());
			return service.save(p);
		}).map(p -> ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	/*
	 * @DeleteMapping("/{id}") public Mono<ResponseEntity<Object>>
	 * delete(@PathVariable String id) { return service.findById(id).flatMap(p -> {
	 * return service.delete(p).then(Mono.just(ResponseEntity.noContent().build()));
	 * }).defaultIfEmpty(ResponseEntity.notFound().build()); }
	 */

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
		return service.findById(id).flatMap(p -> {
			return service.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Product>> upload(@PathVariable String id, @RequestPart FilePart file) {
		return service.findById(id).flatMap(p -> {
			p.setPhoto(UUID.randomUUID().toString() + "-"
					+ file.filename().replace(" ", "").replace(":", "").replace("\\", ""));
			return file.transferTo(new File(path + p.getPhoto())).then(service.save(p));
		}).map(p -> ResponseEntity.ok(p)).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping("/v2")
	public Mono<ResponseEntity<Product>> createWithPhoto(Product product, @RequestPart FilePart file) {
		if (product.getCreateAt() == null) {
			product.setCreateAt(new Date());
		}

		product.setPhoto(UUID.randomUUID().toString() + "-"
				+ file.filename().replace(" ", "").replace(":", "").replace("\\", ""));

		return file.transferTo(new File(path + product.getPhoto())).then(service.save(product))
				.map(p -> ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON_UTF8).body(p));
	}

}
