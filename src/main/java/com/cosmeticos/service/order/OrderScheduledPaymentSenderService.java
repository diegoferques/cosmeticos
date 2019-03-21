package com.cosmeticos.service.order;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.OrderStatus;
import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.validation.OrderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.cosmeticos.model.Payment.Type.CC;

/**
 * Aplicado ao Customer quando o professional encerra o servico
 */
@Service(OrderStatus.Values.SCHEDULED_NAME)
public class OrderScheduledPaymentSenderService implements OrderStatusHandler {

    @Value("${order.payment.secheduled.startDay}")
    private String daysToStartPayment;

    @Value("${order.payment.secheduled.daysBeforeStartToNotification}")
    private String daysBeforeStartToNotification;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Qualifier("charger")
    private Charger paymentService;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {

        //TIVE QUE COMENTAR A VALIDACAO ABAIXO POIS ESTAVA DANDO O ERRO ABAIXO:
        //QUANDO VAMOS ATUALIZAR PARA SCHEDULED, AINDA NAO TEMOS OS DADOS QUE VAO SER ATUALIZADOS
        //-- VALIDANDO O COMENTARIO ACIMA --//
        //AQUI TRATAMOS O STATUS SCHEDULED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        if (transientOrder.getStatus() == OrderStatus.SCHEDULED) {

            for (Payment newPayment : persistentOrder.getPaymentCollection()) {
                persistentOrder.addPayment(newPayment);
                orderRepository.save(persistentOrder);

                if (CC.equals(newPayment.getType())) {
                    this.validateScheduledAndsendPaymentRequest(newPayment);
                }
            }
        }
    }

    private Boolean validateScheduledAndsendPaymentRequest(Payment payment) {

        Order persistenOrder = payment.getOrder();

        Boolean success = false;

        int daysToStart = Integer.parseInt(daysToStartPayment);
        int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //INSTANCIAMOS O CALENDARIO
        Calendar c = Calendar.getInstance();

        //DATA ATUAL
        //EX.: 20/08/2017
        Date now = c.getTime();

        //PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
        Date scheduleDateStart = persistenOrder.getScheduleId().getScheduleStart();


        //--- CONFIGURACOES PARA DEFINIR A DATA QUE DEVEMOS INICIAR AS COBRANCAS ---//
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
                throw new OrderValidationException(ResponseCode.GATEWAY_FAILURE, "Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
            } else {
                success = true;
            }

            //TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
        } else if (sdf.format(now).equals(sdf.format(scheduleDateStart))) {
            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
            if (!sendPaymentRequest(payment)) {
                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                throw new OrderValidationException(ResponseCode.GATEWAY_FAILURE, "Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
                        " forma de pagamento seja alterado para dinheiro");
            } else {
                success = true;
            }

            //TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
            //SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
        } else if (now.after(dateToStartPayment)) {
            //AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
            if (sendPaymentRequest(payment)) {
                success = true;
            }

            //TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
        } else {
            throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + persistenOrder.getIdOrder());
        }

        return success;

    }

    private Boolean sendPaymentRequest(Payment payment) {

        ChargeResponse<Object> retornoTransacaoSuperpay = paymentService.reserve(new ChargeRequest<>(payment));

        return ResponseCode.SUCCESS.equals(retornoTransacaoSuperpay.getResponseCode());
    }

}
