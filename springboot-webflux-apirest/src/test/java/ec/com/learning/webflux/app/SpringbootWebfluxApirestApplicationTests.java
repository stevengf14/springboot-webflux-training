package ec.com.learning.webflux.app;

import static org.mockito.ArgumentMatchers.nullable;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import ec.com.learning.webflux.app.models.documents.Category;
import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductService service;

	@Test
	void listTest() {
		client.get().uri("/api/v2/products").accept(MediaType.APPLICATION_JSON_UTF8).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBodyList(Product.class)
				/* .hasSize(12) */
				.consumeWith(response -> {
					List<Product> products = response.getResponseBody();
					products.forEach(p -> {
						System.out.println(p.getName());
					});
					Assertions.assertThat(products.size() > 0).isTrue();
				});
	}

	@Test
	void viewTest() {
		Product product = service.findByName("Xiaomi Redmi Note Pro 11").block();
		client.get().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
				.accept(MediaType.APPLICATION_JSON_UTF8).exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				/*
				 * .expectBody().jsonPath("$.id").isNotEmpty()
				 * .jsonPath("$.name").isEqualTo("Xiaomi Redmi Note Pro 11");
				 */
				.expectBody(Product.class).consumeWith(response -> {
					Product p = response.getResponseBody();
					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getId().length() > 0).isTrue();
					Assertions.assertThat(p.getName()).isEqualTo("Xiaomi Redmi Note Pro 11");
				});
	}

	@Test
	void createTest() {
		Category category = service.findCategoryByName("Furniture").block();
		Product product = new Product("Bike", 150.00, category);
		client.post().uri("/api/v2/products").contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(product), Product.class).exchange()
				.expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.id").isNotEmpty().jsonPath("$.name").isEqualTo("Bike").jsonPath("$.category.name")
				.isEqualTo("Furniture");
	}

	@Test
	void create2Test() {
		Category category = service.findCategoryByName("Furniture").block();
		Product product = new Product("Bike", 150.00, category);
		client.post().uri("/api/v2/products").contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(product), Product.class).exchange()
				.expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody(Product.class).consumeWith(response -> {
					Product p = response.getResponseBody();
					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getName()).isEqualTo("Bike");
					Assertions.assertThat(p.getCategory().getName()).isEqualTo("Furniture");
				});
	}

	@Test
	void editTest() {
		Product product = service.findByName("Canon T7i").block();
		Category category = service.findCategoryByName("Furniture").block();
		Product editProduct = new Product("Bike", 150.00, category);
		client.put().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(editProduct), Product.class).exchange()
				.expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.id").isNotEmpty().jsonPath("$.name").isEqualTo("Bike").jsonPath("$.category.name")
				.isEqualTo("Furniture");
	}

	@Test
	void deleteTest() {
		Product product = service.findByName("Shoes Under Armour Running").block();
		client.delete().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId())).exchange()
				.expectStatus().isNoContent().expectBody().isEmpty();

		client.get().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId())).exchange()
				.expectStatus().isNotFound().expectBody().isEmpty();
	}

}
