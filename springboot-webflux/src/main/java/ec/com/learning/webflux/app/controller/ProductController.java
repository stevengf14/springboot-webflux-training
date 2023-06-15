package ec.com.learning.webflux.app.controller;

import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import ec.com.learning.webflux.app.models.documents.Product;
import ec.com.learning.webflux.app.models.services.ProductService;
import io.netty.handler.stream.ChunkedStream;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes("product")
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
		model.addAttribute("button", "Create");
		return Mono.just("form");
	}

	@GetMapping("/form/{id}")
	public Mono<String> edit(@PathVariable String id, Model model) {
		Mono<Product> monoProduct = service.findById(id).doOnNext(p -> {
			log.info("Product: " + p.getName());
		}).defaultIfEmpty(new Product());
		model.addAttribute("title", "Edit Product");
		model.addAttribute("product", monoProduct);
		model.addAttribute("button", "Edit");
		return Mono.just("form");
	}

	@GetMapping("/form-v2/{id}")
	public Mono<String> editV2(@PathVariable String id, Model model) {
		return service.findById(id).doOnNext(p -> {
			log.info("Product: " + p.getName());
			model.addAttribute("title", "Edit Product");
			model.addAttribute("product", p);
			model.addAttribute("button", "Edit");
		}).defaultIfEmpty(new Product()).flatMap(p -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("Product does not exists"));
			}
			return Mono.just(p);
		}).then(Mono.just("form")).onErrorResume(ex -> Mono.just("redirect:/list?error=product+does+not+exists"));
	}

	@PostMapping("/form")
	public Mono<String> save(@Valid Product product, BindingResult result, Model model, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("title", "Errors in Product Form");
			model.addAttribute("button", "Save");
			return Mono.just("form");
		} else {
			status.setComplete();
			if (product.getCreateAt() == null) {
				product.setCreateAt(new Date());
			}
			return service.save(product).doOnNext(p -> {
				log.info("Product saved: " + p.getName() + " Id: " + p.getId());
			}).thenReturn("redirect:/list?success=product+saved+successfully");
		}
	}

	@GetMapping("/delete/{id}")
	public Mono<String> delete(@PathVariable String id) {
		return service.findById(id).defaultIfEmpty(new Product()).flatMap(p -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("Product does not exists"));
			}
			return Mono.just(p);
		}).flatMap(service::delete).then(Mono.just("redirect:/list?success=product+deleted+successfully"))
				.onErrorResume(ex -> Mono.just("redirect:/list?error=product+does+not+exists"));
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
