package ProductsProject.ProductsProject.Requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;


public record ProductUpdateRequest (

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name is too long")
    String name,

    @NotNull(message = "Incorrect price")
    @Min(value = 0, message = "Price must be non-negative")
    BigDecimal price,

    @NotBlank(message = "Incorrect description")
    @Size(max = 500, message = "Description is too long")
    String description,

    Map<Long, String> photoToUpdateMap

) {}
