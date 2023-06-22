package ec.com.learning.webflux.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyInserters.*;

import java.net.URI;
import java.util.Date;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

	@Autowired
	private ProductService service;

	public Mono<ServerResponse> list(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(service.findAll(), Product.class);
	}

	public Mono<ServerResponse> view(ServerRequest request) {
		String id = request.pathVariable("id");
		return service.findById(id)
				.flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(p)))
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> create(ServerRequest request) {
		Mono<Product> product = request.bodyToMono(Product.class);
		return product.flatMap(p -> {
			if (p.getCreateAt() == null) {
				p.setCreateAt(new Date());
			}
			return service.save(p);
		}).flatMap(p -> ServerResponse.created(URI.create("/api/v2/products/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(p)));
	}

}
