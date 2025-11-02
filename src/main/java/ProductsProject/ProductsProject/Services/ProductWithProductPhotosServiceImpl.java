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
@RequiredArgsConstructor
public class ProductWithProductPhotosServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductDtoMapper productDtoMapper;

    private ProductEntity getById(Long id) {
        ProductEntity productEntity = productRepository.findByIdWithProductPhotos(id)
                .orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
        return productEntity;
    }

    @Override
    //    @Cacheable(value = "product", key = "#id", cacheManager = "product")
    public ProductDto getProductDtoById(Long id) {
        return productDtoMapper.toProductDtoWithProductPhotos(getById(id));
    }

    @Override
//    @Cacheable(value = "products", key = "'page_' + #page + '_size_' + #size", cacheManager = "products")
    public List<ProductDto> getAllProductsDto(int page, int size) {
        String sort = "id";
        Page<Long> ids = productRepository.findProductIds(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort))
        );
        List<ProductEntity> productEntities = productRepository.findProductWithProductPhotos(ids.getContent());
        return productEntities.stream()
                .map(productEntity -> productDtoMapper.toProductDtoWithProductPhotos(productEntity))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto create(ProductCreateRequest productCreateRequest) {
        return null;
    }

    @Override
    public ProductDto update(Long id, ProductUpdateRequest productUpdateRequest) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

}
