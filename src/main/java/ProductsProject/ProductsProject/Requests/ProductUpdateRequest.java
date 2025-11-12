package ProductsProject.ProductsProject.Requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;


public record ProductUpdateRequest (

    @Size(max = 100, message = "Name is too long")
    String name,

    @Min(value = 0, message = "Price must be non-negative")
    BigDecimal price,

    @Size(max = 500, message = "Description is too long")
    String description,

    @Size(max = 3, message = "Maximum of 3 photos")
    List<String> newPhotoUrls

) {}
