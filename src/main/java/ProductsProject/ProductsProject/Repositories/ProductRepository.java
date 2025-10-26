package ProductsProject.ProductsProject.Repositories;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
