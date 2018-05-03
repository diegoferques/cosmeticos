package com.cosmeticos.service;

import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


/**
 * Created by Vinicius on 12/09/2017.
 */

@Slf4j
public class LostPaymentReprocessor {
    @Autowired
    @Qualifier("charger")
    private Charger charger;

    @Autowired
    private PaymentRepository paymentRepository;

    @Scheduled(cron = "0 0 0/6 * * ?")
        public void retryFailedPayments () {

            List<Payment> paymentList = paymentRepository.findFailedPayments();

            for (Payment p : paymentList) {

                ChargeResponse<Object> response = charger.capture(new ChargeRequest<>(p));

                log.info("com paymentId[" + p.getId() + "] -  Resultado da captura: " + response.getResponseCode());
            }
        }
    }

