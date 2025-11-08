package ProductsProject.ProductsProject.Controllers;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import ProductsProject.ProductsProject.Services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<ProductPageDto> getProducts(
            @RequestParam @Min(value = 0, message = "Incorrect value of page") int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok()
                .body(productService.getProducts(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductPageDto> searchProductsByName(
            @RequestParam @NotNull(message = "Name can not be null") @NotEmpty(message = "Name can not be empty") String name,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok()
                .body(productService.searchProductsByName(name, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
        ProductDto productDto = productService.createProduct(productCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProductUpdateRequest productUpdateRequest
    ) {
        ProductDto productDto = productService.updateProduct(id, productUpdateRequest);
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
