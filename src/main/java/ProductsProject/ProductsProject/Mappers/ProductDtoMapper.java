package ProductsProject.ProductsProject.Mappers;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.DTO.ProductPhotoDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import ProductsProject.ProductsProject.Entities.ProductPhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ProductDtoMapper {

    ProductEntity toEntity(ProductDto productDto);

    ProductDto toProductDto(ProductEntity productEntity);

    @Mapping(target = "product_id", source = "product.id")
    ProductPhotoDto toProductPhotoDto(ProductPhotoEntity productPhotoEntity);

    @Mapping(target = "photos", source = "productPhotos")
    ProductDto toProductDtoWithProductPhotos(ProductEntity productEntity);

    @Mapping(target = "photoUrl", source = "url")
    @Mapping(target = "product", source = "product")
    ProductPhotoEntity toPhotoEntity(String url, ProductEntity product);


}
