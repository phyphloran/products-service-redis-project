package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Mappers.ProductDtoMapper;
import ProductsProject.ProductsProject.Repositories.ProductRepository;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductDtoMapper productDtoMapper;

    private ProductEntity getById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
        return productEntity;
    }

    @Override
    @Cacheable(value = "product", key = "#id", cacheManager = "product")
    public ProductDto getProductDtoById(Long id) {
        return productDtoMapper.toProductDto(getById(id));
    }

    @Override
    @Cacheable(value = "products", key = "'page_' + #page + '_size_' + #size", cacheManager = "products")
    public List<ProductDto> getAllProductsDto(int page, int size) {
        String sort = "id";
        Page<ProductEntity> allProducts = productRepository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, sort)
                )
        );

        return allProducts.getContent().stream()
                .map(productDtoMapper::toProductDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "products", allEntries = true, cacheManager = "products")
    public ProductDto create(ProductCreateRequest productCreateRequest) {
        log.info("Creating product with name {}", productCreateRequest.name());
        ProductEntity productEntity = ProductEntity.builder()
                .name(productCreateRequest.name())
                .price(productCreateRequest.price())
                .description(productCreateRequest.description())
                .build();
        ProductDto productDto = productDtoMapper.toProductDto(productRepository.save(productEntity));
        return productDto;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "products", allEntries = true, cacheManager = "products")
    })
    public ProductDto update(Long id, ProductUpdateRequest productUpdateRequest) {
        log.info("Updated product with name {}", productUpdateRequest.name());
        ProductEntity productEntity = getById(id);
        productEntity.setName(productUpdateRequest.name());
        productEntity.setPrice(productUpdateRequest.price());
        productEntity.setDescription(productUpdateRequest.description());
        ProductDto productDto = productDtoMapper.toProductDto(productRepository.save(productEntity));
        return productDto;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "products", allEntries = true, cacheManager = "products")
    })
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
