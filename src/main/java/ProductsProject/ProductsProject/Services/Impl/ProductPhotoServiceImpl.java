package ProductsProject.ProductsProject.Services.Impl;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Entities.ProductPhotoEntity;
import ProductsProject.ProductsProject.Mappers.ProductMapper;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;
import ProductsProject.ProductsProject.Services.ProductPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Primary
@RequiredArgsConstructor
public class ProductPhotoServiceImpl implements ProductPhotoService {

    private final ProductMapper productMapper;

    @Override
    public void addPhotoToProduct(ProductEntity productEntity, ProductCreateRequest productCreateRequest) {
        if (productCreateRequest.productPhotosUrl() != null && !productCreateRequest.productPhotosUrl().isEmpty()) {
            List<ProductPhotoEntity> photos = productCreateRequest.productPhotosUrl()
                    .stream()
                    .map(url -> productMapper.toPhotoEntity(url, productEntity))
                    .collect(Collectors.toList());
            productEntity.setProductPhotos(photos);
        }
    }

    @Override
    public void updateProductPhotos(ProductEntity product, ProductUpdateRequest request) {
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
                    ProductPhotoEntity newPhoto = createProduct(product, newUrls.get(i));
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

    private ProductPhotoEntity createProduct(ProductEntity product, String newUrl) {
        return ProductPhotoEntity.builder()
                .photoUrl(newUrl)
                .product(product)
                .build();
    }

}
