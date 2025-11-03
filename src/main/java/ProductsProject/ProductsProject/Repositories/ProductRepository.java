package ProductsProject.ProductsProject.Repositories;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = "productPhotos", type = EntityGraph.EntityGraphType.FETCH)
    Optional<ProductEntity> findById(@Param("id") Long id);

    @Query("SELECT p.id FROM ProductEntity p WHERE p.id < :id ORDER BY p.id DESC")
    List<Long> findProductIdsByPage(@Param("id") Long id, Pageable pageable);

    @EntityGraph(attributePaths = "productPhotos")
    @Query("SELECT p FROM ProductEntity p WHERE p.id IN :ids ORDER BY p.id DESC")
    List<ProductEntity> findProductsWithPhotosByIds(@Param("ids") List<Long> ids);




}
