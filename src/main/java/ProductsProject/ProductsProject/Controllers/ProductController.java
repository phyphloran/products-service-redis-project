package ProductsProject.ProductsProject.Controllers;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import ProductsProject.ProductsProject.Services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPageDto> getProducts(
            @RequestParam @Min(value = 0, message = "Incorrect value of page") int page,
            @RequestParam @Min(value = 1, message = "Incorrect value of size")
            @Max(value = 20, message = "The size cannot exceed 20") int size
    ) {
        return ResponseEntity.ok().body(productService.getProducts(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductPageDto> searchProductsByName(
            @RequestParam @NotNull(message = "Name can not be null")
            @NotEmpty(message = "Name can not be empty") String name,
            @RequestParam @Min(value = 0, message = "Incorrect value of page") int page,
            @RequestParam @Min(value = 1, message = "Incorrect value of size")
            @Max(value = 20, message = "The size cannot exceed 20") int size
    ) {
        return ResponseEntity.ok().body(productService.searchProductsByName(name, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productCreateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProductUpdateRequest productUpdateRequest
    ) {
        return ResponseEntity.ok().body(productService.updateProduct(id, productUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") @Min(value = 1, message = "Incorrect id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
