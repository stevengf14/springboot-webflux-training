package ec.com.learning.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ec.com.learning.reactor.app.models.Comments;
import ec.com.learning.reactor.app.models.User;
import ec.com.learning.reactor.app.models.UserComments;
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
		// toStringExample();
		// collectListExample();
		// userCommentsFlatMapExample();
		// userCommentsZipWithExample();
		// userCommentsZipWithExample2();
		userCommentsZipRangesWithExample();
	}

	public void userCommentsZipRangesWithExample() {

		Flux.just(1, 2, 3, 4).map(i -> (i * 2))
				.zipWith(Flux.range(0, 4), (one, two) -> String.format("First flux:, %d, Second Flux: %d", one, two))
				.subscribe(text -> log.info(text));
	}

	public void userCommentsZipWithExample2() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
		Mono<Comments> userMonoComments = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hey this is an example!");
			comments.addComment("We're learning webflux");
			comments.addComment("I'm working so hard!");
			return comments;
		});

		Mono<UserComments> userWithComments = userMono.zipWith(userMonoComments).map(tuple -> {
			User u = tuple.getT1();
			Comments c = tuple.getT2();
			return new UserComments(u, c);
		});

		userWithComments.subscribe(uc -> log.info(uc.toString()));
	}

	public void userCommentsZipWithExample() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
		Mono<Comments> userMonoComments = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hey this is an example!");
			comments.addComment("We're learning webflux");
			comments.addComment("I'm working so hard!");
			return comments;
		});

		Mono<UserComments> userWithComments = userMono.zipWith(userMonoComments,
				(user, comments) -> new UserComments(user, comments));

		userWithComments.subscribe(uc -> log.info(uc.toString()));
	}

	public void userCommentsFlatMapExample() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("John", "Doe"));
		Mono<Comments> userMonoComments = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hey this is an example!");
			comments.addComment("We're learning webflux");
			comments.addComment("I'm working so hard!");
			return comments;
		});

		userMono.flatMap(u -> userMonoComments.map(c -> new UserComments(u, c)))
				.subscribe(uc -> log.info(uc.toString()));
	}

	public void collectListExample() {
		List<User> usersList = new ArrayList<>();
		usersList.add(new User("Andres", "Guzman"));
		usersList.add(new User("Pedro", "Fulano"));
		usersList.add(new User("Diego", "Sultano"));
		usersList.add(new User("Juan", "Megano"));
		usersList.add(new User("Bruce", "Lee"));
		usersList.add(new User("Bruce", "Willis"));

		Flux.fromIterable(usersList).collectList().subscribe(list -> {
			list.forEach(item -> log.info(item.toString()));
		});
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
