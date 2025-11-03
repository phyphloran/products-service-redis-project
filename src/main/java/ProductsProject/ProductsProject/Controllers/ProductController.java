package ProductsProject.ProductsProject.Controllers;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import ProductsProject.ProductsProject.Services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    //2 realization: productWithProductPhotosServiceImpl and productServiceImpl
    public ProductController(@Qualifier("productWithProductPhotosServiceImpl") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "100000000000000000") @Min(value = 0, message = "Incorrect value of page") Long page
    ) {
        List<ProductDto> productDtoPage = productService.getAllProductsDto(page);
        return ResponseEntity.ok(productDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        ProductDto productDto = productService.getProductDtoById(id);
        return ResponseEntity.ok(productDto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
        ProductDto productDto = productService.create(productCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProductUpdateRequest productUpdateRequest
    ) {
        ProductDto productDto = productService.update(id, productUpdateRequest);
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
