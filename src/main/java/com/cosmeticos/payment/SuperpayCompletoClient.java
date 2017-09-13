package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.ws.completo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;

/**
 * Created by Vinicius on 08/09/2017.
 */
@Component
@lombok.extern.slf4j.Slf4j
public class SuperpayCompletoClient {

    @Autowired
    @Qualifier("webServiceTemplateCompleto")
    private WebServiceTemplate webserviceTemplate;

    @Value("${superpay.senha}")
    private String password;

    @Value("${superpay.login}")
    private String user;

    @Value("${superpay.estabelecimento}")
    private String codigoEstabelecimento;

    public ResultadoPagamentoWS capturePayment(String codigoSeguranca,
                                               String ip,
                                               String nomeTitularCarttaoCredito,
                                               Long valor, String urlCampainha){

        ObjectFactory factory = new ObjectFactory();

        TransacaoCompletaWS transacaoCompletaWS = factory.createTransacaoCompletaWS();
        transacaoCompletaWS.setCodigoEstabelecimento(codigoEstabelecimento);
        //transacaoCompletaWS.setCodigoFormaPagamento(codigoFormaPagamento);
        transacaoCompletaWS.setCodigoSeguranca(codigoSeguranca);
        //transacaoCompletaWS.setDadosUsuarioTransacao(dadosUsuarios);
        //transacaoCompletaWS.setDataValidadeCartao(dataValidadeCartao);
        //transacaoCompletaWS.setIdioma(idioma);
        transacaoCompletaWS.setIP(ip);
        transacaoCompletaWS.setNomeTitularCartaoCredito(nomeTitularCarttaoCredito);
        //transacaoCompletaWS.setNumeroTransacao(numeroTransacao);
        //transacaoCompletaWS.setParcelas(parcelas);
        transacaoCompletaWS.setUrlCampainha(urlCampainha);
        //transacaoCompletaWS.setUrlRedirecionamentoNaoPago(urlRedirecionamentoNaoPago);
        //transacaoCompletaWS.setUrlRedirecionamentoPago(urlRedirecionamentoPago);
        transacaoCompletaWS.setValor(valor);
        //transacaoCompletaWS.setValorDesconto(valorDesconto);
        //transacaoCompletaWS.setVencimentoBoleto(vencimentoBoleto);


         CapturarTransacaoCompleta capturarTransacaoCompleta = factory.createCapturarTransacaoCompleta();
         capturarTransacaoCompleta.setUsuario(user);
         capturarTransacaoCompleta.setSenha(password);
         capturarTransacaoCompleta.setTransacao(transacaoCompletaWS);


       // OperacaoVO vo = factory.createOperacaoVO();
       // vo.setCodigoEstabelecimento(codigoEstabelecimento);
       // vo.setNumeroTransacao(numeroTransacao);
       // vo.setOperacao(operacao); // Código que identifica o processo que deseja realizar. Para captura, deve-se enviar o valor 1

        JAXBElement<CapturarTransacaoCompleta> requestBody =
                factory.createCapturarTransacaoCompleta(capturarTransacaoCompleta);

        JAXBElement<CapturarTransacaoCompletaResponse> jaxbResponse =
                (JAXBElement<CapturarTransacaoCompletaResponse>) webserviceTemplate.marshalSendAndReceive(requestBody);

        CapturarTransacaoCompletaResponse oneclickResponse = jaxbResponse.getValue();


        log.info("Client reeceived result='{}'", oneclickResponse.getReturn());

        return oneclickResponse.getReturn();// vo mudar o tipo de retorno do metodo. tbm fiz isso.. rsrsrsrsrsrs


    }

}
