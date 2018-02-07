package demo.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import demo.domain.Payment;
import demo.domain.PaymentOrderResponse;
import demo.service.PaymentOrderResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class PaymentOrderResponseServiceImpl implements PaymentOrderResponseService {

    @Autowired   // Eureka Discovery Client will inject RestTemplate
    private RestTemplate restTemplate;  // Service to Service Call

    //private RestTemplate restTemplate = new RestTemplate();

    @Override
    public PaymentOrderResponse generatePaymentOrderResponse(Payment paymentOrder) {
        PaymentOrderResponse paymentOrderResponse = new PaymentOrderResponse();
        paymentOrderResponse.setPaymentId(paymentOrder.getPaymentId());
        paymentOrderResponse.setOrderId(paymentOrder.getOrder().getOrderId());

        return paymentOrderResponse;
    }

    /**
     * Send PaymentOrderResponse to Food-Order-Service
     */
    @HystrixCommand(fallbackMethod = "sendPaymentOrderResponseFallback")
    @Override
    public void sendPaymentOrderResponse(PaymentOrderResponse paymentOrderResponse) {
        log.info("PaymentOrderResponse @food-payment " + paymentOrderResponse);

        // Eureka Server will find the distribution eureka client, so not need to write server port
        String foodOrder = "http://food-order";  // http://[service name]
        //String foodOrder = "http://localhost:9000";
        this.restTemplate.put(foodOrder + "/api/order", paymentOrderResponse);
        log.info("End of sending PaymentOrderResponse ");
    }

    // Plan B : backup fall back method
    public void sendPaymentOrderResponseFallback(PaymentOrderResponse paymentOrderResponse) {
        log.error("Hystrix Fallback Method. Unable to send PaymentOrderResponse " +
                "to food-order-service");

    }

}
