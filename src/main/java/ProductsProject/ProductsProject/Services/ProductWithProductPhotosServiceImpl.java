package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.PaginationDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
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
    public ProductDto getProductById(Long id) {
        return productDtoMapper.toProductDtoWithProductPhotos(getById(id));
    }

    @Override
    @Cacheable(value = "productsPage", key = "'page_' + #page + '_size_' + #size", cacheManager = "productsPage")
    public ProductPageDto getProducts(int page, int size) {
        Page<Long> productEntityPage = productRepository.findIdsPage(PageRequest.of(page, size));

        List<ProductDto> productDtoList = productRepository.findByIdIn(productEntityPage.getContent(), Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        return new ProductPageDto(
                productDtoList,
                getPaginationInfo(productEntityPage)
        );
    }

    private PaginationDto getPaginationInfo(Page<Long> productEntityPage) {
        return new PaginationDto(productEntityPage.getTotalPages(), productEntityPage.getNumber());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    })
    public ProductDto createProduct(ProductCreateRequest productCreateRequest) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productCreateRequest.name());
        productEntity.setPrice(productCreateRequest.price());
        productEntity.setDescription(productCreateRequest.description());

        if (productCreateRequest.productPhotosUrl() != null) {

            List<ProductPhotoEntity> photos = productCreateRequest.productPhotosUrl()
                    .stream()
                    .map(url -> productDtoMapper.toPhotoEntity(url, productEntity))
                    .collect(Collectors.toList());

            productEntity.setProductPhotos(photos);
        }

        return productDtoMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    })
    public ProductDto updateProduct(Long id, ProductUpdateRequest productUpdateRequest) {
        ProductEntity productEntity = getById(id);
        updateProductFields(productEntity, productUpdateRequest);
        updateProductPhotos(productEntity, productUpdateRequest);
        return productDtoMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    private void updateProductFields(ProductEntity productEntity, ProductUpdateRequest productUpdateRequest) {
        if (productUpdateRequest.name() != null && !productUpdateRequest.name().isBlank()) {
            productEntity.setName(productUpdateRequest.name());
        }
        if (productUpdateRequest.price() != null) {
            productEntity.setPrice(productUpdateRequest.price());
        }
        if (productUpdateRequest.description() != null && !productUpdateRequest.description().isBlank()) {
            productEntity.setDescription(productUpdateRequest.description());
        }
    }

    private void updateProductPhotos(ProductEntity product, ProductUpdateRequest request) {
        List<ProductPhotoEntity> existingPhotos = product.getProductPhotos();
        List<String> newUrls = request.newPhotoUrls();
        if (newUrls == null) {
            return;
        }
        updateExistingPhotos(existingPhotos, newUrls);
        if (existingPhotos.size() > newUrls.size()) {
            existingPhotos.subList(newUrls.size(), existingPhotos.size()).clear();
        }
        else if (existingPhotos.size() < newUrls.size()) {
            for (int i = existingPhotos.size(); i < newUrls.size(); i++) {
                if (newUrls.get(i) != null && !newUrls.get(i).isBlank()) {
                    ProductPhotoEntity newPhoto = ProductPhotoEntity.builder()
                            .photoUrl(newUrls.get(i))
                            .product(product)
                            .build();
                    existingPhotos.add(newPhoto);
                }
            }
        }
    }

    private void updateExistingPhotos(List<ProductPhotoEntity> existingPhotos, List<String> newUrls) {
        int minSize = Math.min(existingPhotos.size(), newUrls.size());
        for (int i = 0; i < minSize; i++) {
            if (newUrls.get(i) != null && !newUrls.get(i).isBlank()) {
                existingPhotos.get(i).setPhotoUrl(newUrls.get(i));
            }
        }
    }

    @Override
    @Cacheable(value = "productsPageSearch", key = "'name_' + #name + '_page_' + #page + '_size_' + #size", cacheManager = "productsPage")
    public ProductPageDto searchProductsByName(String name, int page, int size) {
        Page<Long> productEntityPage = productRepository.findIdsByNameContainingIgnoreCase(name, PageRequest.of(page, size));

        List<ProductDto> productDtoList = productRepository.findByIdIn(productEntityPage.getContent(), Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        return new ProductPageDto(
                productDtoList,
                getPaginationInfo(productEntityPage)
        );
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id", cacheManager = "product"),
            @CacheEvict(value = "productsPageSearch", allEntries = true, cacheManager = "productsPage"),
            @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    })
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
