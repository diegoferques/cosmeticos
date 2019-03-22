package com.cosmeticos.service.order;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.OrderStatus;
import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cosmeticos.model.Payment.Type.CC;

/**
 * Aplicado ao Customer quando o professional encerra o servico
 */
@Slf4j
@Service(OrderStatus.Values.ACCEPTED_NAME)
public class OrderPaymentSenderService implements OrderStatusHandler {

    @Value("${order.payment.secheduled.startDay}")
    private String daysToStartPayment;

    @Value("${order.payment.secheduled.daysBeforeStartToNotification}")
    private String daysBeforeStartToNotification;

    @Autowired
    @Qualifier("charger")
    private Charger paymentService;

    @Autowired
    private PaymentCaptureHelper paymentCaptureHelper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {

        //AQUI TRATAMOS O STATUS ACCEPTED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        // Utilizamos a order persistente pois ela possui TODOS os atributos setados
        if (transientOrder.getStatus() == OrderStatus.ACCEPTED) {


            for (Payment newPayment : persistentOrder.getPaymentCollection()) {
                if (CC.equals(newPayment.getType())) {
                    this.sendPaymentRequest(newPayment);
                }
            }
        }

    }

    private Boolean sendPaymentRequest(Payment payment) {

        ChargeResponse<Object> retornoTransacaoSuperpay = paymentService.reserve(new ChargeRequest<>(payment));

        return ResponseCode.SUCCESS.equals(retornoTransacaoSuperpay.getResponseCode());
    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //TODO - ACHEI NECESSARIO CRIAR UM CRON PARA PEDIDOS QUE AINDA ESTAO EM READY2CHARGE POR ALGUM ERRO OCORRIDO
    @Scheduled(cron = "${order.payment.ready2charge.cron}")
    public void findReady2ChargeOrdersAndSendPaymentCron() throws Exception {

        List<Payment> paymentList = paymentRepository.findByOrderStatus(OrderStatus.READY2CHARGE);

        for (Payment payment : paymentList) {
            this.paymentCaptureHelper.sendPaymentCapture(payment);
        }

    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    @Scheduled(cron = "${order.payment.secheduled.cron}")
    public void findScheduledOrdersValidateAndSendPaymentRequest() throws Exception {

        List<Payment> paymentList = paymentRepository.findByOrderStatus(OrderStatus.SCHEDULED);

        int daysToStart = Integer.parseInt(daysToStartPayment);
        int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //INSTANCIAMOS O CALENDARIO
        Calendar c = Calendar.getInstance();

        //DATA ATUAL
        //EX.: 20/08/2017
        Date now = c.getTime();

        for (Payment payment : paymentList) {
            if (payment.getType() != null) {
                if (payment.getType().equals(CC)) {
                    // TODO: requer refactoring quando comecarmos a fazer pagamento com 2 cartoes.

                    if (Payment.Status.PAGO_E_NAO_CAPTURADO ==
                            payment.getStatus()) {

                        Order order = payment.getOrder();

                        //PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
                        Date scheduleDateStart = order.getScheduleId().getScheduleStart();

                        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
                        c.setTime(scheduleDateStart);

                        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
                        c.add(Calendar.DATE, -daysToStart);

                        //DATA DO AGENDAMENTO MENOS N DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
                        Date dateToStartPayment = c.getTime();


                        //--- CONFIGURACOES PARA DEFINIR A DATA QUE DEVEMOS INICIAR AS NOTIFICACOES AO CLIENTE CASO FALHE ---//
                        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
                        c.setTime(scheduleDateStart);

                        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
                        c.add(Calendar.DATE, -daysBeforeStart);

                        //UM DIA ANTES PARA NOTIFICAR AO CLIENTE SE DER ERRO NA RESERVA NO CARTAO E SUGERIR TROCAR PARA DINHEIRO
                        Date dateToStartNotification = c.getTime();

                        //TODO - VERIFICAR SE E UM DIA ANTES E TENTAR ENVIAR REQUEST, SE DER ERRO, LOGAR PARA DEPOIS NOTIFICAR NO APP
                        if (sdf.format(now).equals(sdf.format(dateToStartNotification))) {
                            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
                            if (!sendPaymentRequest(payment)) {
                                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                                log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
                            }

                            //TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
                        } else if (sdf.format(now).equals(sdf.format(scheduleDateStart))) {
                            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
                            if (!sendPaymentRequest(payment)) {
                                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                                log.error("Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
                                        " forma de pagamento seja alterado para dinheiro");
                            }

                            //TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
                            //SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
                        } else if (now.after(dateToStartPayment)) {
                            //AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
                            sendPaymentRequest(payment);

                            //TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
                        } else {
                            log.error("Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + order.getIdOrder());
                        }
                    }
                }
            } else {
                log.error("Pagamento sem PaymentType: paymentId{}, orderId{}",
                        payment.getId(), payment.getOrder().getIdOrder());
            }
        }
    }

    @Override
    public void onAfterOrderPesistence(Order persistentOrder) {
        // Nada a implementar
    }
}
