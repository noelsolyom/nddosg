package hu.soros.nddosg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NddosgApplication {

	public static void main(String[] args) {
		SpringApplication.run(NddosgApplication.class, args);
	}

}
