package com.eeit1475th.eshop.member.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserVipDTO {
    private Integer vipId;
    private Boolean isVip;
    private Integer vipLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String vipPhoto;

    // Getters and Setters
    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getVipPhoto() {
        return vipPhoto;
    }

    public void setVipPhoto(String vipPhoto) {
        this.vipPhoto = vipPhoto;
    }
}