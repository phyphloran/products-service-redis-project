package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;


public interface ProductService {

    ProductDto getProductDtoById(Long id);

    ProductPageDto getAllProductsDto(int page, int size);

    ProductDto create(ProductCreateRequest productCreateRequest);

    ProductDto update(Long id, ProductUpdateRequest productUpdateRequest);

    void delete(Long id);

    ProductPageDto findByName(String name, int page, int size);

}
