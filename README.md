# Spring-Cloud-MicroService

## Start Spring-Cloud-Food-Delivery:
~~~
1.  Terminal 1: 
        docker-compose up   
    Result:   rabbitMQ port sets up
2.  Terminal, [Eureka-Server]:
        cd /platform/eureka-server/target
    --> java -jar eureka-server-0.0.1-SNAPSHOT.jar 
    Result:   eureka-server port sets up
3.  Terminal, [Hystrix-Dashboard]:
        cd /platform/hystrix-dashboard/target
    --> java -jar hystrix-dashboard-0.0.1-SNAPSHOT.jar
    Result:   hystrix port sets up
4.  Terminal, [Food-Order-Service]:
        cd /food-order/target
    --> java -jar food-order-1.0.0.BUILD-SNAPSHOT.jar 
    Result:   food-order port sets up
5.  Terminal, [Food-Payment-Service]:
        cd /food-payment/target
    --> java -jar food-payment-1.0.0.BUILD-SNAPSHOT.jar 
    Result:   food-payment port sets up
6.  Terminal, [Third-Party-Payment]:
        cd /third-party-payment/target
    --> java -jar third-party-payment-1.0.0.BUILD-SNAPSHOT.jar 
    Result:   third-party-payment port sets up
7.  Terminal, [Food-Order-Delivery]:
        cd /food-delivery/target
    --> java -jar food-delivery-1.0.0.BUILD-SNAPSHOT.jar 
    Result:   food-delivery port sets up

# Double check all components are setting up, ^0^~   
1.  Chrome, [Eureka]:
        URL:  http://localhost:8761/
    Result: Instances currently registered with Eureka
            Application: Food-Delivery; Food-Order; Food-Payment
2.  Chrome, [Hystrix Monitor]
        URL:  http://localhost:7979/ 
         --> Hystrix Dashboard
         --> Substitude  [http://hostname:port/turbine/turbine.stream] to
                    URL:  http://localhost:9001/hystrix.stream
         --> Monitor Stream
3.  Chrome, [RabbitMQ Management]
        URL:  http://localhost:15672/
         --> Username: guest
         --> Password: guest
         --> Login
~~~
## Run Spring-Cloud-Food-Delivery:
~~~
1) user can search restaurant 
Postman:  [GET] --> URL:  localhost:9000/restaurants --> Send
Response (to front end): a list of restaurants

Postman:  [GET] --> URL:  localhost:9000/restaurants/Elyse Restaurant --> Send
Response (to front end): menu of this restaurant

2) user can order food & fill delivery address
Postman: [Post] --> URL:  localhost:9000/order
                --> Body --> raw --> Json --> requestBody in file  [food-order/src/main/resources/order-request.json]
                --> Send
Response (to front end) : orderID, calculated subtotal, tax, total Amount

3) user can pay food order
Postman:  [Get] --> URL:  localhost:9001/payment/Angela --> Send
Response: user's payment info except security code 

Chrome, [Food Delivery WebSocket]:   
         URL:  http://localhost:9003/     -->    Connect
Postman: [Post] --> URL:  localhost:9001/payment
                --> Body --> raw --> Json --> requestBody in file [food-payment/src/main/resources/payment-request.json]
                --> Change "orderId": "5a73c66715f6487e118aee31" in payment-request.json to
                           "orderId": " ~^0^~ " in [Postman: Response --> "id": " ~^0^~ " 
                                                    from postRequst 2) user can order food & fill delivery address]
                --> Send

Response (to Chrome, Food Delivery WebSocket): orderId, estimate delivery time
     --> wait 5s,
             Chrome [Food Delivery WebSocket]: orderId, estimate delivery time        

4) user can unsubscribe food delivery info
Chrome, [Food Delivery WebSocket]: 
         URL:  http://localhost:9003/     -->    Disconnect
~~~
## Backend Services Communication: [When User submit payment request for food order]
~~~
1. Terminal, [Food-Payment-Service]:
Log: PaymentOrder @food-payment-service 
            Payment(paymentId=1, ..., paymentStatus=null)

2. RabbitMQ

3. Terminal, [Third-Party-Payment]:
Log: PaymentOrder input @3rd-party-payment 
            Payment(paymentId=1, ..., paymentStatus=null)
Log: Update paymentOrder status @3rd-party-payment
            Payment(paymentId=1, ..., paymentStatus=Success)

4. Terminal, [Food-Payment-Service]:
Log: Received PaymentOrder from 3rd-party-payment 
            Payment(paymentId=1, ..., paymentStatus=Success)

     [PaymentOrder Success]
Log: PaymentOrderResponse @food-payment-service 
            PaymentOrderResponse(paymentId=1, orderId=...)
Log: HystrixFallBack @food-payment-service 
Log: Updated paymentStatus: Success for paymentId: 1 @food-payment-service 

5. Terminal, [Food-Order-Service]:
Log: Received paymentOrderResponse @food-order-service 
            PaymentOrderResponse(paymentId=1, orderId=...)
Log: OrderDeliveryRequest @food-order-service  
            OrderDeliveryRequest(id=...)
Log: HystrixFallBack @food-order-service 

6. Terminal, [Food-Payment-Service]:
Log: End of Sending PaymentOrderResponse

7. Terminal, [Food-Order-Delivery]:
Log: Received OrderDeliveryRequest @food-delivery-service 
            OrderDeliveryRequest(id=..., estimatedDeliveryTime=null)

WebSockt, push msg to Chrome [Food Delivery WebSocket]

Log: Updated deliveryTime @food-delivery-service
          push msg to Chrome [Food Delivery WebSocket]
~~~
