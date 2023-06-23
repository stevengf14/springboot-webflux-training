package ec.com.learning.webflux.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import ec.com.learning.webflux.app.models.documents.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void listTest() {
		client.get().uri("/api/v2/products").accept(MediaType.APPLICATION_JSON_UTF8).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBodyList(Product.class).hasSize(12);
	}

}
