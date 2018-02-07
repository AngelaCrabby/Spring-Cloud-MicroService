package demo.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import demo.domain.Order;
import demo.domain.OrderDeliveryRequest;
import demo.service.OrderDeliveryRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrderDeliveryRequestServiceImpl implements OrderDeliveryRequestService {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public OrderDeliveryRequest generateOrderDeliveryRequest(Order order) {
        OrderDeliveryRequest orderDeliveryRequest = new OrderDeliveryRequest();
        orderDeliveryRequest.setId(order.getId());
        orderDeliveryRequest.setUser(order.getUser());
        orderDeliveryRequest.setRestaurant(order.getRestaurant());
        orderDeliveryRequest.setFoodItems(order.getFoodItems());

        return orderDeliveryRequest;
    }

    /** send OrderDeliveryRequest to Food-Delivery-Service */
    // if sendOrderDeliveryRequest() has error, then call sendOrderDeliveryRequestFallback()
    @HystrixCommand(fallbackMethod = "sendOrderDeliveryRequestFallback")
    @Override
    public void sendOrderDeliveryRequest(OrderDeliveryRequest orderDeliveryRequest) {
        log.info("OrderDeliveryRequest @food-order " + orderDeliveryRequest);
        String foodDelivery = "http://localhost:9003";
        this.restTemplate.postForLocation(foodDelivery + "/api/delivery", orderDeliveryRequest);
        log.info("End of sending OrderDeliveryRequest");
    }

    // Plan B : backup fall back method
    public void sendOrderDeliveryRequestFallback(OrderDeliveryRequest orderDeliveryRequest) {
        log.error("Hystrix Fallback Method. Unable to send OrderDeliveryRequest. " +
                "Send a email to notify delivery info");

        // can send a email to notify delivery info
    }
}
