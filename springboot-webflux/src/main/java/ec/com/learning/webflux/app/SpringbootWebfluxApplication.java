package ec.com.learning.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import ec.com.learning.webflux.app.models.dao.ProductDao;
import ec.com.learning.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductDao dao;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringbootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("products").subscribe();

		Flux.just(new Product("TV Panasonic LCD", 456.89), new Product("Xiaomi Redmi Note Pro 11", 350.50),
				new Product("IPod touch 256GB", 356.45), new Product("IPad mini 256GB", 850.00),
				new Product("Canon T7i", 650.25), new Product("Asus ROG Strix", 1950.75),
				new Product("TV  TCL 55", 750.25), new Product("Monitor LG 55", 450.72),
				new Product("Lenovo Yoga Tab 128GB", 230.25), new Product("Mouse + Keyboard", 15.43))
				.flatMap(product -> {
					product.setCreateAt(new Date());
					return dao.save(product);
				}).subscribe(product -> log.info("Insert:" + product.getId() + " " + product.getName()));

	}

}
