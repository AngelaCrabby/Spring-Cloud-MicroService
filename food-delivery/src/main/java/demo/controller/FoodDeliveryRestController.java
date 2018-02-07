package demo.controller;

import demo.model.OrderDeliveryDisplay;
import demo.model.OrderDeliveryRequest;
import demo.service.OrderDeliveryDisplayService;
import demo.service.impl.DeliveryReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@MessageEndpoint  // mark it as msg endpoint for forwarding msg via WebSocket
public class FoodDeliveryRestController {

    @Autowired
    private SimpMessagingTemplate template; // webSocket send msg

    private OrderDeliveryDisplayService orderDeliveryDisplayService;
    private DeliveryReminderService deliveryReminderService;

    @Autowired
    public FoodDeliveryRestController(OrderDeliveryDisplayService orderDeliveryDisplayService,
                                      DeliveryReminderService deliveryReminderService) {
        this.orderDeliveryDisplayService = orderDeliveryDisplayService;
        this.deliveryReminderService = deliveryReminderService;
    }

    /**
     * 1. Receive OrderDeliveryRequest from food-order-service
     * 2. Send estimate delivery time to WebSocket, then broadcast to front end
     * 3. Send reminder of pickup time to WebSocket, then broadcast to front end
     */
    @RequestMapping(value = "/api/delivery", method = RequestMethod.POST)
    public void paymentOrder(@RequestBody OrderDeliveryRequest orderDeliveryRequest) throws Exception {
        log.info("Received OrderDeliveryRequest @food-delivery-service " + orderDeliveryRequest);

        OrderDeliveryDisplay orderDeliveryDisplay = orderDeliveryDisplayService.generateOrderDeliveryDisplay(orderDeliveryRequest);
        OrderDeliveryDisplay deliveryDisplay = orderDeliveryDisplayService.calculateEstimateDeliveryTime(orderDeliveryDisplay);

        // push msg [orderDeliveryRequest] to msg broker [/topic/food]
        this.template.convertAndSend("/topic/food", deliveryDisplay); // in-memory message broker send orderDeliveryRequest

        // push updated msg to msg broker
        OrderDeliveryDisplay updatedDeliveryDisplay = deliveryReminderService.updateMessage(deliveryDisplay);
        this.template.convertAndSend("/topic/food", updatedDeliveryDisplay);
    }

}
