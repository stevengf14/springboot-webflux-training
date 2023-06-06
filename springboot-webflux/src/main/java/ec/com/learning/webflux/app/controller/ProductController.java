package ec.com.learning.webflux.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ec.com.learning.webflux.app.models.dao.ProductDao;
import ec.com.learning.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;

@Controller
public class ProductController {

	@Autowired
	private ProductDao dao;

	@GetMapping({ "/list", "/" })
	public String list(Model model) {
		Flux<Product> products = dao.findAll();
		model.addAttribute("products", products);
		model.addAttribute("title", "List of products");
		return "list";
	}

}
