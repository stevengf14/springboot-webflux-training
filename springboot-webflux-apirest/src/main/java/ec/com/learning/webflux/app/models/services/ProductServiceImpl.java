package ec.com.learning.webflux.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.com.learning.webflux.app.models.dao.CategoryDao;
import ec.com.learning.webflux.app.models.dao.ProductDao;
import ec.com.learning.webflux.app.models.documents.Category;
import ec.com.learning.webflux.app.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao dao;

	@Autowired
	private CategoryDao categoryDao;

	@Override
	public Flux<Product> findAll() {
		return dao.findAll();
	}

	@Override
	public Mono<Product> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<Product> save(Product product) {
		return dao.save(product);
	}

	@Override
	public Mono<Void> delete(Product product) {
		return dao.delete(product);
	}

	@Override
	public Flux<Product> findAllWithUpperCaseName() {
		return dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});
	}

	@Override
	public Flux<Product> findAllWithUpperCaseNameRepeat() {
		return findAllWithUpperCaseName().repeat(5000);
	}

	@Override
	public Flux<Category> findAllCategories() {
		return categoryDao.findAll();
	}

	@Override
	public Mono<Category> findCategoryById(String id) {
		return categoryDao.findById(id);
	}

	@Override
	public Mono<Category> saveCategory(Category category) {
		return categoryDao.save(category);
	}

	@Override
	public Mono<Product> findByName(String name) {
		return dao.getByName(name);
	}

	@Override
	public Mono<Category> findCategoryByName(String name) {
		return categoryDao.findByName(name);
	}

}
