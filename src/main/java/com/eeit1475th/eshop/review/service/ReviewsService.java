package com.eeit1475th.eshop.review.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.product.repository.ProductsRepository;
import com.eeit1475th.eshop.review.repository.ReviewsRepository;

@Service
public class ReviewsService {

	@Autowired
	private ReviewsRepository reviewsRepository;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ProductsRepository productsRepository;
	
}
