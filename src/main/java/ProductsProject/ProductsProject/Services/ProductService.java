package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;


public interface ProductService {

    ProductDto getProductById(Long id);

    ProductPageDto getProducts(int page, int size);

    ProductPageDto searchProductsByName(String name, int page, int size);

    ProductDto createProduct(ProductCreateRequest productCreateRequest);

    ProductDto updateProduct(Long id, ProductUpdateRequest productUpdateRequest);

    void deleteProduct(Long id);

}
