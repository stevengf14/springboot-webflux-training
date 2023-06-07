package ec.com.learning.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import ec.com.learning.webflux.app.models.dao.ProductDao;
import ec.com.learning.webflux.app.models.documents.Product;
import io.netty.handler.stream.ChunkedStream;
import reactor.core.publisher.Flux;

@Controller
public class ProductController {

	@Autowired
	private ProductDao dao;

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@GetMapping({ "/list", "/" })
	public String list(Model model) {
		Flux<Product> products = dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});
		products.subscribe(prod -> log.info(prod.getName()));

		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return "list";
	}

	@GetMapping("/list-datadriver")
	public String listDataDriver(Model model) {
		Flux<Product> products = dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		}).delayElements(Duration.ofSeconds(1));
		products.subscribe(prod -> log.info(prod.getName()));

		model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
		model.addAttribute("title", "List of products");
		return "list";
	}
	
	@GetMapping("/list-full")
	public String listFull(Model model) {
		Flux<Product> products = dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		}).repeat(5000);

		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return "list";
	}
}
