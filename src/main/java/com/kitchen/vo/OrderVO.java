package com.kitchen.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private String nickname;
    private Integer status;
    private String remark;
    private Integer totalCount;
    private LocalDateTime createTime;
    private List<OrderDetailVO> details;

    @Data
    public static class OrderDetailVO {
        private Long dishId;
        private String dishName;
        private Integer quantity;
    }
}
