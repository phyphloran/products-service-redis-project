package ProductsProject.ProductsProject.Services.Impl;


import ProductsProject.ProductsProject.DTO.PaginationDto;
import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPageDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Mappers.ProductMapper;
import ProductsProject.ProductsProject.Repositories.ProductRepository;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import ProductsProject.ProductsProject.Services.ProductPhotoService;
import ProductsProject.ProductsProject.Services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class ProductWithProductPhotosServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductPhotoService productPhotoService;

    @Override
    public ProductDto getProductById(Long id) {
        return productMapper.toProductDtoWithProductPhotos(getById(id));
    }

    @Override
    @Cacheable(value = "productsPage", key = "'page_' + #page + '_size_' + #size", cacheManager = "productsPage")
    public ProductPageDto getProducts(int page, int size) {
        Page<Long> productEntityIdsPage = productRepository.findIdsPage(PageRequest.of(page, size));

        List<ProductDto> productDtoList = productRepository.findByIdIn(productEntityIdsPage
                        .getContent(), Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(productEntity -> productMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        return new ProductPageDto(productDtoList, getPaginationInfo(productEntityIdsPage)
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    public ProductDto createProduct(ProductCreateRequest productCreateRequest) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productCreateRequest.name());
        productEntity.setPrice(productCreateRequest.price());
        productEntity.setDescription(productCreateRequest.description());
        productPhotoService.addPhotoToProduct(productEntity, productCreateRequest);
        return productMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    @Override
    @Transactional
    @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    public ProductDto updateProduct(Long id, ProductUpdateRequest productUpdateRequest) {
        ProductEntity productEntity = getById(id);
        updateProductFields(productEntity, productUpdateRequest);
        productPhotoService.updateProductPhotos(productEntity, productUpdateRequest);
        return productMapper.toProductDtoWithProductPhotos(productRepository.save(productEntity));
    }

    @Override
    public ProductPageDto searchProductsByName(String name, int page, int size) {
        Page<Long> productEntityIdsPage = productRepository
                .findIdsByNameContainingIgnoreCase(name, PageRequest.of(page, size));

        List<ProductDto> productDtoList = productRepository.findByIdIn(productEntityIdsPage
                        .getContent(), Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(productEntity -> productMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());

        return new ProductPageDto(productDtoList, getPaginationInfo(productEntityIdsPage)
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "productsPage", allEntries = true, cacheManager = "productsPage")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductEntity getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
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

    private PaginationDto getPaginationInfo(Page<Long> productEntityIdsPage) {
        return new PaginationDto(productEntityIdsPage.getTotalPages(), productEntityIdsPage.getNumber());
    }

}
