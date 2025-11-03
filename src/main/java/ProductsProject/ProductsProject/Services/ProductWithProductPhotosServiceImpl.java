package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Entities.ProductPhotoEntity;
import ProductsProject.ProductsProject.Mappers.ProductDtoMapper;
import ProductsProject.ProductsProject.Repositories.ProductRepository;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductWithProductPhotosServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductDtoMapper productDtoMapper;

    private ProductEntity getById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
        return productEntity;
    }

    @Override
    @Transactional
    @Cacheable(value = "product", key = "#id", cacheManager = "product")
    public ProductDto getProductDtoById(Long id) {
        return productDtoMapper.toProductDtoWithProductPhotos(getById(id));
    }

    @Override
    @Transactional
    @Cacheable(value = "products", key = "'page_' + #id", cacheManager = "products")
    public List<ProductDto> getAllProductsDto(Long id) {
        List<Long> ids = productRepository.findProductIdsByPage(id, PageRequest.of(0, 10));
        if (ids.isEmpty())  {
            return Collections.emptyList();
        }

        List<ProductEntity> productEntities = productRepository.findProductsWithPhotosByIds(ids);

        return productEntities.stream()
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true, cacheManager = "products")
    public ProductDto create(ProductCreateRequest productCreateRequest) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productCreateRequest.name());
        productEntity.setPrice(productCreateRequest.price());
        productEntity.setDescription(productCreateRequest.description());

        List<ProductPhotoEntity> photos = productCreateRequest.productPhotosUrl()
                .stream()
                .map(url -> productDtoMapper.toPhotoEntity(url, productEntity))
                .collect(Collectors.toList());

        productEntity.setProductPhotos(photos);

        return productDtoMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "products", allEntries = true, cacheManager = "products")
    })
    public ProductDto update(Long id, ProductUpdateRequest productUpdateRequest) {
        ProductEntity productEntity = getById(id);

        productEntity.setName(productUpdateRequest.name());
        productEntity.setPrice(productUpdateRequest.price());
        productEntity.setDescription(productUpdateRequest.description());

        List<ProductPhotoEntity> productPhotoEntity = productEntity.getProductPhotos();
        Map<Long, String> newMap = productUpdateRequest.photoToUpdateMap();

        if (newMap != null && !newMap.isEmpty()) {
            productPhotoEntity.forEach(photo -> {
                String newUrl = newMap.get(photo.getId());
                if (newUrl != null && !newUrl.isBlank()) {
                    photo.setPhotoUrl(newUrl);
                }
            });
        }

        return productDtoMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "products", key = "'page_' + #id", cacheManager = "products")
    })
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}
