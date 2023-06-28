package ec.com.learning.webflux.client.app.handler;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.springframework.http.MediaType.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
		return ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(service.findAll(), Product.class);
	}

	public Mono<ServerResponse> view(ServerRequest request) {
		String id = request.pathVariable("id");
		return service.findById(id).flatMap(p -> ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).syncBody(p))
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> create(ServerRequest request) {
		Mono<Product> product = request.bodyToMono(Product.class);

		return product.flatMap(p -> {
			if (p.getCreateAt() == null) {
				p.setCreateAt(new Date());
			}
			return service.save(p);
		}).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
				.contentType(APPLICATION_JSON_UTF8).syncBody(p)).onErrorResume(error -> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
						return ServerResponse.badRequest().contentType(APPLICATION_JSON_UTF8)
								.syncBody(errorResponse.getResponseBodyAsString());
					}
					return Mono.error(errorResponse);
				});
	}

	public Mono<ServerResponse> edit(ServerRequest request) {
		Mono<Product> product = request.bodyToMono(Product.class);
		String id = request.pathVariable("id");
		return product.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id)))
				.contentType(APPLICATION_JSON_UTF8).body(service.update(p, id), Product.class));
	}

	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");
		return service.delete(id).then(ServerResponse.noContent().build());
	}
}
