package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import java.util.List;


public interface ProductService {

    ProductDto getProductDtoById(Long id);

    List<ProductDto> getAllProductsDto(Long id);

    ProductDto create(ProductCreateRequest productCreateRequest);

    ProductDto update(Long id, ProductUpdateRequest productUpdateRequest);

    void delete(Long id);

}
