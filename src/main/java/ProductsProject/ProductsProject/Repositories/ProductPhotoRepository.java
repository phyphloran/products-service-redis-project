package ProductsProject.ProductsProject.Repositories;


import ProductsProject.ProductsProject.Entities.ProductPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhotoEntity, Long> {
}
