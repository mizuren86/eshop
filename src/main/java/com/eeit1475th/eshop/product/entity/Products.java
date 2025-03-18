package com.eeit1475th.eshop.product.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.OrderItems;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Products {

	@Id
	@Column(name ="product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productId;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	@JsonBackReference
	private ProductCategory productCategory;

	@Column(name = "sku")
	private String sku;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "description")
	private String description;

	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "active")
	private boolean active;

	@Column(name = "unit_in_stock")
	private Integer unitInStock;

	@Column(name = "date_create", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime dateCreate;

	@Column(name = "last_update", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime lastUpdate;
	
	@OneToMany(mappedBy = "products", cascade = CascadeType.ALL)
	@JsonManagedReference(value="product-cartItems")
	private List<CartItems> cartItems = new ArrayList<>();
	
	@OneToMany(mappedBy = "products", cascade = CascadeType.ALL)
	@JsonManagedReference(value = "product-orderItems")
	private List<OrderItems> orderItems = new ArrayList<>();

    @Override
    public String toString() {
        return "Product{" +
                "id=" + productId +
                ", category=" + productCategory +
                ", sku='" + sku + '\'' +
                ", name='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", imageUrl='" + imageUrl + '\'' +
                ", active=" + active +
                ", unitInStock=" + unitInStock +
                ", dateCreate=" + dateCreate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
