package ec.com.learning.webflux.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "categories")
public class Category {

	@Id
	@NotBlank(message = "Can't be empty")
	private String id;

	@NotBlank(message = "Can't be empty")
	private String name;

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
