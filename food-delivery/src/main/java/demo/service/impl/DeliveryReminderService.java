package demo.service.impl;

import demo.model.OrderDeliveryDisplay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * use this method to update deliveryTime for pick up reminder
 * send the msg to msg broker
 * msg broker will broadcast to all subscribers
 */
@Service
@Slf4j
public class DeliveryReminderService {

    //@SendTo("/topic/food")  // msg broker "topic/food" will broadcast to all subscribers
    public OrderDeliveryDisplay updateMessage(OrderDeliveryDisplay deliveryDisplay) throws Exception {
        Thread.sleep(5000); // simulated delay
        deliveryDisplay.setEstimatedDeliveryTime(5);  // remind delivery time < 5 mins
        log.info("updated deliveryTime @food-delivery-service");

        return deliveryDisplay;
    }

}
