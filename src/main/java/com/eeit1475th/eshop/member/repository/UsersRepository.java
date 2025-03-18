package com.eeit1475th.eshop.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eeit1475th.eshop.member.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
	
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);
    
    
//	Users findByUsername(String username);
//	
//	Users findByEmail(String email);

}