package ProductsProject.ProductsProject.Repositories;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Query(value = "SELECT p.id FROM product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.id DESC",
            nativeQuery = true)
    Page<Long> findIdsByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @EntityGraph(attributePaths = "productPhotos")
    List<ProductEntity> findByIdIn(List<Long> ids, Sort sort);

    @Query("SELECT p.id FROM ProductEntity p ORDER BY p.id DESC")
    Page<Long> findIdsPage(Pageable pageable);

}
