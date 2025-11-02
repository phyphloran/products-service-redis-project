package ProductsProject.ProductsProject.Repositories;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

//    Optional<ProductEntity> findById(Long id);

//    @EntityGraph(attributePaths = "productPhotos", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productPhotos WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithProductPhotos(@Param("id") Long id);


//    @Query("""
//    SELECT DISTINCT p
//    FROM ProductEntity p
//    LEFT JOIN FETCH p.productPhotos
//    ORDER BY p.id DESC
//    """)
//    List<ProductEntity> findLastWithProductsPhotos(Pageable pageable);

    @Query("SELECT p.id FROM ProductEntity p")
    Page<Long> findProductIds(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productPhotos WHERE p.id IN :ids")
    List<ProductEntity> findProductWithProductPhotos(List<Long> ids);

}
