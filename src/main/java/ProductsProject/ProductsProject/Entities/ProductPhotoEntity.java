package ProductsProject.ProductsProject.Entities;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_photo")
public class ProductPhotoEntity {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

}
