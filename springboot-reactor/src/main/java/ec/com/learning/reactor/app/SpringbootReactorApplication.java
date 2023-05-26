package ec.com.learning.reactor.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<String> names = Flux.just("Andres", "Pedro", "Diego", "Juan").doOnNext(System.out::println);
		names.subscribe();

	}

}
