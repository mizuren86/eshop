package com.eeit1475th.eshop.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eeit1475th.eshop.member.entity.UserVip;

@Repository
public interface UserVipRepository extends JpaRepository<UserVip, Integer> {
}