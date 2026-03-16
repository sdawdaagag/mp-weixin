package com.kitchen.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 位置信息DTO
 */
@Data
public class LocationDTO {

    @NotNull(message = "纬度不能为空")
    private Double latitude;

    @NotNull(message = "经度不能为空")
    private Double longitude;

    private String address;
}
