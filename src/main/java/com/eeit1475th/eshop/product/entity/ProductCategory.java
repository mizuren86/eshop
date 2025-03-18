package com.eeit1475th.eshop.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_category")
public class ProductCategory {

	@Id
	@Column(name ="category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoryId;
	
	@Column(name ="category_name")
	private String categoryName;

    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Products> products = new ArrayList<>();
    
    @Override
    public String toString() {
        return "ProductCategory{" +
                "id=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", products=" + products +
                '}';
    }
}
