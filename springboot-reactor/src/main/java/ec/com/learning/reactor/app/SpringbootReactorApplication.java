package ec.com.learning.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
		// userCommentsZipRangesWithExample();
		// intervalExample();
		// delayElementsExample();
		// infiniteIntervalExample();
		// intervalFromCreateExample();
		backPressureExample();
	}

	public void backPressureExample() {

		Flux.range(1, 10)
		.log()
		.limitRate(5)
		.subscribe(/*new Subscriber<Integer>() {

			private Subscription s;

			private Integer limit = 5;
			private Integer consumed = 0;

			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				s.request(limit);
			}

			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumed++;
				if (consumed == limit) {
					consumed = 0;
					s.request(limit);
				}
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub

			}
		}*/);
	}

	public void intervalFromCreateExample() {
		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				private Integer counter = 0;

				@Override
				public void run() {
					emitter.next(++counter);
					if (counter == 10) {
						timer.cancel();
						emitter.complete();
					}

					if (counter == 5) {
						timer.cancel();
						emitter.error(new InterruptedException("Error, we stop flux at 5!"));
					}
				}
			}, 1000, 1000);
		}).subscribe(next -> log.info(next.toString()), error -> log.error(error.getMessage()),
				() -> log.info("Process finished!"));
	}

	public void infiniteIntervalExample() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);

		Flux.interval(Duration.ofSeconds(1)).doOnTerminate(latch::countDown).flatMap(i -> {
			if (i >= 5) {
				return Flux.error(new InterruptedException("5!!!"));
			}
			return Flux.just(i);
		}).map(i -> "Hi " + i).retry(2).subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

		latch.await();
	}

	public void delayElementsExample() {
		Flux<Integer> range = Flux.range(1, 12).delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));
		range.blockLast();
	}

	public void intervalExample() {
		Flux<Integer> range = Flux.range(1, 12);
		Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));

		range.zipWith(delay, (ra, de) -> ra).doOnNext(i -> log.info(i.toString())).blockLast();
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
