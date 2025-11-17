package ProductsProject.ProductsProject.DTO;


import java.util.List;


public record ProductPageDto (
        List<ProductDto> content,
        PaginationDto info
) {
}
