package ec.com.learning.webflux.client.app.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.*;
import org.springframework.stereotype.Service;
import static org.springframework.web.reactive.function.BodyInserters.*;
import org.springframework.web.reactive.function.client.WebClient;

import ec.com.learning.webflux.client.app.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private WebClient client;

	@Override
	public Flux<Product> findAll() {
		return client.get().accept(APPLICATION_JSON_UTF8).exchange()
				.flatMapMany(response -> response.bodyToFlux(Product.class));
	}

	@Override
	public Mono<Product> findById(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return client.get().uri("/{id}", params).accept(APPLICATION_JSON_UTF8).retrieve().bodyToMono(Product.class);
		// .exchange()
		// .flatMap(response -> response.bodyToMono(Product.class));
	}

	@Override
	public Mono<Product> save(Product product) {
		return client.post().accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
				// .body(fromObject(product))
				.syncBody(product).retrieve().bodyToMono(Product.class);
	}

	@Override
	public Mono<Product> update(Product product, String id) {
		return client.put().uri("/{id}", Collections.singletonMap("id", id)).accept(APPLICATION_JSON_UTF8)
				.contentType(APPLICATION_JSON_UTF8)
				// .body(fromObject(product))
				.syncBody(product).retrieve().bodyToMono(Product.class);
	}

	@Override
	public Mono<Void> delete(String id) {
		return client.delete().uri("/{id}", Collections.singletonMap("id", id)).exchange().then();
	}

}
