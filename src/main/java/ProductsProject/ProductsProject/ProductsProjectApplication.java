package ProductsProject.ProductsProject;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class ProductsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsProjectApplication.class, args);
	}

}
