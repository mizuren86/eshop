package com.eeit1475th.eshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedStore {

	private String storeID;
	private String storeName;
	private String storeAddress;
	private String storePhone;

	@Override
	public String toString() {
		return "SelectedStore{" + "storeID='" + storeID + '\'' + ", storeName='" + storeName + '\'' + ", storeAddress='"
				+ storeAddress + '\'' + ", storePhone='" + storePhone + '\'' + '}';
	}

}
