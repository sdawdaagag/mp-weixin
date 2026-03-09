package com.kitchen.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatusUpdateDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;
}

