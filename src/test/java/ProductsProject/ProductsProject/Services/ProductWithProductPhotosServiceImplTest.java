package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Mappers.ProductDtoMapper;
import ProductsProject.ProductsProject.Repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductWithProductPhotosServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductWithProductPhotosServiceImpl productService;


    @Test
    void getProductById_WhenProductExists() {
        Long id = 1L;
        Instant now = Instant.now();
        ProductEntity productEntity = createProductEntity(id, now);
        ProductDto productDto = createProductDto(id, now);

        when(productRepository.findById(id)).thenReturn(Optional.of(productEntity));
        when(productDtoMapper.toProductDtoWithProductPhotos(productEntity)).thenReturn(productDto);

        ProductDto result = productService.getProductById(id);

        assertNotNull(result);
        assertEquals(productDto, result);
        verify(productRepository, times(1)).findById(id);
        verify(productDtoMapper, times(1)).toProductDtoWithProductPhotos(productEntity);
    }

    @Test
    void getProductById_WhenProductNotFound() {
        Long incorrectId = -1L;

        when(productRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(incorrectId));

        verify(productRepository, times(1)).findById(incorrectId);
        verify(productDtoMapper, never()).toProductDtoWithProductPhotos(any());
    }

    @Test
    void getProducts() {
    }

    @Test
    void createProduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void searchProductsByName() {
    }

    @Test
    void deleteProduct() {
    }

    private ProductEntity createProductEntity(Long id, Instant now) {
        ProductEntity entity = new ProductEntity(
                id, "test", BigDecimal.valueOf(254.87), "description for test", now, now, List.of()
        );
        return entity;
    }

    private ProductDto createProductDto(Long id, Instant now) {
        return new ProductDto(
                id, "test", BigDecimal.valueOf(254.87), "description for test", now, now, List.of()
        );
    }

}