package com.eeit1475th.eshop.member.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_vip_history")
public class UserVipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private Integer vipLevel;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String vipPhoto;

}