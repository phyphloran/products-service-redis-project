package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Comparator;
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
    @Cacheable(value = "product", key = "#id", cacheManager = "product")
    public ProductDto getProductDtoById(Long id) {
        return productDtoMapper.toProductDtoWithProductPhotos(getById(id));
    }

    @Override
    @Cacheable(value = "productsPage", key = "'page_' + #page + '_size_' + #size", cacheManager = "productsPage")
    public ProductPageDto getAllProductsDto(int page, int size) {
        Page<Long> productEntityPage = productRepository.findIdsPage(PageRequest.of(page, size));

        List<Long> ids = productEntityPage.getContent();

        long totalElements = productEntityPage.getTotalElements();

        List<ProductDto> productDtoList = productRepository.findByIdIn(ids).stream()
                .sorted(Comparator.comparing(ProductEntity::getId).reversed())
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        return new ProductPageDto(productDtoList, totalElements);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    })
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
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
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
    @Cacheable(value = "productsPageSearch", key = "'name_' + #name + '_page_' + #page + '_size_' + #size", cacheManager = "productsPage")
    public ProductPageDto findByName(String name, int page, int size) {
        Page<Long> productEntityPage = productRepository.findIdsByNameContainingIgnoreCase(name, PageRequest.of(page, size));

        List<Long> ids = productEntityPage.getContent();

        List<ProductDto> productDtoList = productRepository.findByIdIn(ids).stream()
                .sorted(Comparator.comparing(ProductEntity::getId).reversed())
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        long totalElements = productEntityPage.getTotalElements();

        return new ProductPageDto(productDtoList, totalElements);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    })
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}
