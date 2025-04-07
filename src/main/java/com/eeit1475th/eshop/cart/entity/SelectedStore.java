package com.eeit1475th.eshop.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "selected_store")
public class SelectedStore {
	
	@Id
    @Column(name = "selected_store_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer selectedStoreId;

    @Column(name = "CVS_store_id")
    private String CVSStoreId;
    
    @Column(name = "CVS_store_name")
    private String CVSStoreName;
    
    @Column(name = "CVS_address")
    private String CVSAddress;
    
    @Column(name = "CVS_telephone")
    private String CVSTelephone;

    public SelectedStore(String CVSStoreId, String CVSStoreName, String CVSAddress, String CVSTelephone) {
    	this.CVSStoreId = CVSStoreId;        
    	this.CVSStoreName = CVSStoreName;
        this.CVSAddress = CVSAddress;
        this.CVSTelephone = CVSTelephone;
    }
}
