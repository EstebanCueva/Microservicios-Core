package com.ads.order.service;

import com.ads.order.dto.OrderRequest;
import com.ads.order.dto.OrderResponse;
import com.ads.order.exception.CustomerNotFoundException;
import com.ads.order.exception.InsufficientStockException;
import com.ads.order.model.Order;
import com.ads.order.model.OrderLine;
import com.ads.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Simulaciones de validación
        validateCustomer(request.getCustomerId());

        // crear entidad Order y OrderLine(s)
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(LocalDateTime.now());

        // mapear líneas y validar stock simulado
        var lines = request.getLines().stream().map(lr -> {
            validateProductStock(lr.getProductId(), lr.getQuantity());
            // Price simulation: usar 10.00 * productId (solo para persistir)
            BigDecimal price = BigDecimal.valueOf(10L).multiply(BigDecimal.valueOf(Math.max(1, lr.getProductId())));
            OrderLine ol = OrderLine.builder()
                    .productId(lr.getProductId())
                    .quantity(lr.getQuantity())
                    .priceAtTimeOfOrder(price)
                    .order(order)
                    .build();
            return ol;
        }).collect(Collectors.toList());

        order.setLines(lines);

        // calcular total
        BigDecimal total = lines.stream()
                .map(l -> l.getPriceAtTimeOfOrder().multiply(BigDecimal.valueOf(l.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // map to response
        OrderResponse resp = new OrderResponse();
        resp.setOrderId(saved.getOrderId());
        resp.setCustomerId(saved.getCustomerId());
        resp.setOrderDate(saved.getOrderDate());
        resp.setTotalAmount(saved.getTotalAmount());
        resp.setLines(saved.getLines().stream().map(l ->
                new OrderResponse.OrderLineResp(l.getProductId(), l.getQuantity(), l.getPriceAtTimeOfOrder())
        ).collect(Collectors.toList()));

        return resp;
    }

    // Simulaciones requeridas
    private void validateCustomer(Long customerId) {
        if (customerId == null || customerId == 0L) {
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
    }

    private void validateProductStock(Long productId, Integer quantity) {
        // regla dada: si quantity > 5 => insuficiente stock
        if (quantity == null) quantity = 0;
        if (quantity > 5) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
    }
}
