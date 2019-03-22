package com.cosmeticos.service.order;

import com.cosmeticos.model.*;
import com.cosmeticos.service.VoteService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static com.cosmeticos.model.Payment.Status.PAGO_E_CAPTURADO;
import static com.cosmeticos.model.Payment.Type.CC;

/**
 * Aplicado ao Professional quando o customer confirma realização do servico e avalia o professional
 */
@Service(OrderStatus.Values.READY2CHARGE_NAME)
public class OrderProfessionalEvaluationService implements OrderStatusHandler {

    @Autowired
    private VoteService voteService;

    @Autowired
    private PaymentCaptureHelper paymentCaptureHelper;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {

        ProfessionalCategory persistentProfessionalCategory = persistentOrder.getProfessionalCategory();
        User persistentUser = persistentProfessionalCategory.getProfessional().getUser();

        ProfessionalCategory receivedProfessionalCategory = transientOrder.getProfessionalCategory();
        User receivedUser = receivedProfessionalCategory.getProfessional().getUser();

        if (!receivedUser.getVoteCollection().isEmpty()) {
            Vote receivedvote = receivedUser.getVoteCollection().stream().findFirst().get();

            addVotesToUser(persistentUser, receivedvote);

            MDC.put("professionalVote", String.valueOf(receivedvote.getValue()));
        }


        //ACHEI MELHOR FAZER UMA NOVA VERIFICACAO APOS SALVAR, POIS PRECISAMOS TER ARMAZENADO QUANDO MUDAMOS O STATUS
        //PARA READY2CHARGE E QUANDO FIZEMOS A CAPTURA. POIS COMO ESTAVA ANTES NAO TINHAMOS O REGISTRO DE READY2CHARGE
        //POIS QUANDO ERA ESTE STATUS, JA ENVIAMOS A CAPTURA E, LOGO APOS A CAPTURA, O CORRETO EH MUDAR O STATUS PARA PAYD
        if (persistentOrder.getStatus() == OrderStatus.READY2CHARGE) {
            Payment payment = persistentOrder.getPaymentCollection()
                    .stream()
                    .findFirst()
                    .get();

            if (CC.equals(payment.getType())) {

                if (    // Se ja esta PAGO_E_CAPTURADO nao precisamos capturar mais nada e nao executara a proxima clausula.
                        PAGO_E_CAPTURADO.equals(payment.getStatus()) ||

                                //AQUI TRATAMOS O STATUS READY2CHARGE QUE VAI NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
                                this.paymentCaptureHelper.sendPaymentCapture(payment)) {

                    //ADICIONEI O QUE SEGUE ABAIXO POIS PRECISAMOS TER O REGISTRO DA ATUALIZACAO DOS DOIS STATUS
                    //PRIMEIRO READY2CHARGE E, LOGO EM SEGUIDA, SE A CAPTURA FOR FEITA COM SUCESSO, MUDAMOS PARA PAID
                    //OBS.: COMO NAO TEMOS O STATUS PAID, MUDEI PARA CLOSED
                    persistentOrder.setStatus(OrderStatus.CLOSED);
                    persistentOrder.setLastStatusUpdate(Calendar.getInstance().getTime());

                } else {
                    persistentOrder.setStatus(OrderStatus.FAILED_ON_PAYMENT);
                    persistentOrder.setLastStatusUpdate(Calendar.getInstance().getTime());
                }

            } else if (Payment.Type.CASH.equals(payment.getType())) {

                //TODO - SE ORDER NO BANCO FOR READY2CHARGE E PAGAMENTO EM DINHEIRO, ENTAO MUDAMOS O STATUS PARA O SOLICITADO???
                //NAO ESTOU ENTENDENDO ISSO!!!
                //TODO - VERIFICAR POIS SER FOR ENVIADO CLOSED PODE BATER AQUI E GERAR PROBLEMA
                // ACCEPTED ou READY2CHARGE?  Deivison quer que pague so apos executar o servico
                // Garry: Ta estranho mesmo.. vamos apagar esta instrucao
                persistentOrder.setStatus(OrderStatus.CLOSED);

            } else {
                boolean mustPersistOrder = false;
            }
        }
    }

    private void addVotesToUser(User persistentUser, Vote receivedVote) {

        persistentUser.addVote(receivedVote);
        voteService.create(receivedVote);
        persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
    }

    @Override
    public void onAfterOrderPesistence(Order persistentOrder) {
        // Nada a implementar
    }
}
