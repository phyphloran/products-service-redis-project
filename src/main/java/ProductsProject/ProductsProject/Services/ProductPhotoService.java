package ProductsProject.ProductsProject.Services;


import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Requests.ProductCreateRequest;
import ProductsProject.ProductsProject.Requests.ProductUpdateRequest;


public interface ProductPhotoService {

    void addPhotoToProduct(ProductEntity productEntity, ProductCreateRequest productCreateRequest);

    void updateProductPhotos(ProductEntity product, ProductUpdateRequest request);

}
