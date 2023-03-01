package uk.co.setech.EasyBook;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "EasyBook API", version = "1.0", description = "API for the easy book invoicing system"))
public class EasyBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyBookApplication.class, args);
	}

}
