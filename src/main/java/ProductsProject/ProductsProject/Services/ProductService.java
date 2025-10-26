package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import org.springframework.data.domain.Page;


public interface ProductService {

    ProductDto getProductDtoById(Long id);
    Page<ProductDto> getAllProductsDto(int page, int size);
    ProductDto create(ProductCreateRequest productCreateRequest);
    ProductDto update(Long id, ProductUpdateRequest productUpdateRequest);
    void delete(Long id);

}
