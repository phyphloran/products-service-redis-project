package ProductsProject.ProductsProject.Mappers;


import ProductsProject.ProductsProject.DTO.ProductDto;
import ProductsProject.ProductsProject.Entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ProductDtoMapper {

    ProductEntity toEntity(ProductDto productDto);

    ProductDto toProductDto(ProductEntity productEntity);

}
