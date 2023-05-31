package ec.com.learning.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ec.com.learning.reactor.app.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// iterableExample();
		// flatMapExample();
		toStringExample();
	}

	public void toStringExample() {
		List<User> usersList = new ArrayList<>();
		usersList.add(new User("Andres", "Guzman"));
		usersList.add(new User("Pedro", "Fulano"));
		usersList.add(new User("Diego", "Sultano"));
		usersList.add(new User("Juan", "Megano"));
		usersList.add(new User("Bruce", "Lee"));
		usersList.add(new User("Bruce", "Willis"));

		Flux.fromIterable(usersList)
				.map(user -> user.getName().toUpperCase().concat(" ").concat(user.getLastName().toUpperCase()))
				.flatMap(name -> {
					if (name.contains("bruce".toUpperCase())) {
						return Mono.just(name);
					}
					return Mono.empty();

				}).subscribe(u -> log.info(u.toString()));
	}

	public void flatMapExample() {
		List<String> usersList = new ArrayList<>();
		usersList.add("Andres Guzman");
		usersList.add("Pedro Fulano");
		usersList.add("Diego Sultano");
		usersList.add("Juan Megano");
		usersList.add("Bruce Lee");
		usersList.add("Bruce Willis");

		Flux.fromIterable(usersList)
				.map(name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
				.flatMap(user -> {
					if (user.getName().equalsIgnoreCase("bruce")) {
						return Mono.just(user);
					}
					return Mono.empty();

				}).map(user -> {
					user.setName(user.getName().toLowerCase());
					return user;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void iterableExample() {
		List<String> usersList = new ArrayList<>();
		usersList.add("Andres Guzman");
		usersList.add("Pedro Fulano");
		usersList.add("Diego Sultano");
		usersList.add("Juan Megano");
		usersList.add("Bruce Lee");
		usersList.add("Bruce Willis");

		Flux<String> names = Flux.fromIterable(usersList);
		/*
		 * Flux.just("Andres Guzman", "Pedro Fulano", "Diego Sultano", "Juan Megano",
		 * "Bruce Lee","Bruce Willis");
		 */
		Flux<User> users = names
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

		users.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Observable process's finalized successfully");

			}
		});
	}

}
