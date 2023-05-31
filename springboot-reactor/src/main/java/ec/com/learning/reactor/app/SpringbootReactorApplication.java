package ec.com.learning.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ec.com.learning.reactor.app.models.User;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<User> names = Flux
				.just("Andres Guzman", "Pedro Fulano", "Diego Sultano", "Juan Megano", "Bruce Lee", "Bruce Willis")
				.map(name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
				.filter(user -> user.getName().toLowerCase().equals("bruce")).doOnNext(user -> {
					if (user == null) {
						throw new RuntimeException("Names can't be empty");
					}
					System.out.println(user.getName() + " " + user.getLastName());
				}).map(user -> {
					user.setName(user.getName().toLowerCase());
					return user;
				});

		names.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Observable process's finalized successfully");

			}
		});

	}

}
