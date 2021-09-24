package de.adesso.bookstore.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("paypal")
public class PaypalPayment implements PaymentService {

    @Override
    public void pay(double amount) {
        log.info("pay {} with Paypal.", amount);
   }
}
