package com.eeit1475th.eshop.member.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_vip")
public class UserVip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vipId;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(nullable = false)
    private Boolean isVip = true;

    private Integer vipLevel;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String vipPhoto;

}