package ec.com.learning.webflux.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import ec.com.learning.webflux.app.handler.ProductHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(ProductHandler handler) {
		return route(GET("/api/v2/products").or(GET("/api/v3/products")), handler::list)
				.andRoute(GET("/api/v3/products/{id}"), handler::view);
	}

}
