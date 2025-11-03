package ProductsProject.ProductsProject.Requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;


public record ProductCreateRequest (

        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name is too long")
        String name,

        @NotNull(message = "Incorrect price")
        @Min(value = 0, message = "Price must be non-negative")
        BigDecimal price,

        @NotBlank(message = "Incorrect description")
        @Size(max = 300, message = "Description is too long")
        String description,

        @Size(max = 3, message = "Maximum of 3 photos")
        List<String> productPhotosUrl

) {}

