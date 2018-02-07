package demo.service.impl;

import demo.domain.Payment;
import demo.domain.PaymentRepository;
import demo.domain.Sender;
import demo.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Sender findBySenderUsername(String userName) {
        Sender sender = new Sender();

        Payment payment = this.paymentRepository.findTopBySenderUserName(userName);
        if (payment != null) {
            sender = payment.getSender();
            sender.setSecurityCode("");
        }

        return sender;
    }

    @Override
    public Payment savePaymentOrder(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentStatusById(String paymentStatus, Long paymentId) {
        paymentRepository.updatePaymentStatusById(paymentStatus, paymentId);
        log.info(String.format("Updated paymentStatus: %s for paymentId: %d",
                paymentStatus, paymentId));
        return paymentRepository.findByPaymentId(paymentId);
    }

}
