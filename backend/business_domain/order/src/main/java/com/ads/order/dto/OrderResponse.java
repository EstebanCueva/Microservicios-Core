package com.ads.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderLineResp> lines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineResp {
        private Long productId;
        private Integer quantity;
        private BigDecimal priceAtTimeOfOrder;
    }
}
