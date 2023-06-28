package ec.com.learning.webflux.client.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ec.com.learning.webflux.client.app.models.Product;
import ec.com.learning.webflux.client.app.service.ProductService;
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
				.flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).syncBody(p))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}
