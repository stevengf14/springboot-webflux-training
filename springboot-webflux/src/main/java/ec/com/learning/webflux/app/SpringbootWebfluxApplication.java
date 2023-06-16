package ec.com.learning.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import ec.com.learning.webflux.app.models.documents.Category;
import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductServiceImpl;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductServiceImpl service;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringbootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("products").subscribe();
		mongoTemplate.dropCollection("categories").subscribe();

		Category electronic = new Category("Electronic");
		Category sports = new Category("Sports");
		Category computation = new Category("Computation");
		Category furniture = new Category("Furniture");

		Flux.just(electronic, sports, computation, furniture).flatMap(service::saveCategory).doOnNext(c -> {
			log.info("Category created: " + c.getName() + ", Id: " + c.getId());
		}).thenMany(Flux.just(new Product("TV Panasonic LCD", 456.89, electronic),
				new Product("Xiaomi Redmi Note Pro 11", 350.50, electronic),
				new Product("IPod touch 256GB", 356.45, electronic), new Product("IPad mini 256GB", 850.00, electronic),
				new Product("Canon T7i", 650.25, electronic), new Product("Asus ROG Strix", 1950.75, computation),
				new Product("TV  TCL 55", 750.25, electronic), new Product("Monitor LG 55", 450.72, computation),
				new Product("Shoes Under Armour Running", 150.72, sports),
				new Product("Lenovo Yoga Tab 128GB", 230.25, computation),
				new Product("Desktop - wood", 230.25, furniture), new Product("Mouse + Keyboard", 15.43, computation))
				.flatMap(product -> {
					product.setCreateAt(new Date());
					return service.save(product);
				})).subscribe(product -> log.info("Insert:" + product.getId() + " " + product.getName()));
	}

}
