package ec.com.learning.webflux.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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

}
