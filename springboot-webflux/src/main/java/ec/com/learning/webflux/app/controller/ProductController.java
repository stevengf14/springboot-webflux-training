package ec.com.learning.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import io.netty.handler.stream.ChunkedStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class ProductController {

	@Autowired
	private ProductService service;

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@GetMapping({ "/list", "/" })
	public Mono<String> list(Model model) {
		Flux<Product> products = service.findAllWithUpperCaseName();
		products.subscribe(prod -> log.info(prod.getName()));

		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return Mono.just("list");
	}

	@GetMapping("/form")
	public Mono<String> create(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("title", "Product form");
		return Mono.just("form");
	}

	@PostMapping("/form")
	public Mono<String> save(Product product) {
		return service.save(product).doOnNext(p -> {
			log.info("Product saved: " + p.getName() + " Id: " + p.getId());
		}).thenReturn("redirect;/list");
	}

	@GetMapping("/list-datadriver")
	public String listDataDriver(Model model) {
		Flux<Product> products = service.findAllWithUpperCaseName().delayElements(Duration.ofSeconds(1));
		products.subscribe(prod -> log.info(prod.getName()));

		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
		model.addAttribute("title", "List of products");
		return "list";
	}

	@GetMapping("/list-full")
	public String listFull(Model model) {
		Flux<Product> products = service.findAllWithUpperCaseNameRepeat();

		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return "list";
	}

	@GetMapping("/list-chuncked")
	public String listChuncked(Model model) {
		Flux<Product> products = service.findAllWithUpperCaseName();

		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return "list-chuncked";
	}

}
