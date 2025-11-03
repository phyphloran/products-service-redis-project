package ProductsProject.ProductsProject.DTO;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


public record ProductDto(

        Long id,

        String name,

        BigDecimal price,

        String description,

        Instant createdAt,

        Instant updatedAt,

        List<ProductPhotoDto> photos

) {
}
