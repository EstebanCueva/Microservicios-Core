package com.ads.order.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long customerId;
    private List<OrderLineRequest> lines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineRequest {
        private Long productId;
        private Integer quantity;
    }
}
