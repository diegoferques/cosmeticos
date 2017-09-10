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

    public ResultadoPagamentoWS capturePayment(Long numeroTransacao, int operacao){

        ObjectFactory factory = new ObjectFactory();


         CapturarTransacaoCompleta transacaoCompleta = factory.createCapturarTransacaoCompleta();
         transacaoCompleta.setUsuario(user);
         transacaoCompleta.setSenha(password);


        OperacaoVO vo = factory.createOperacaoVO();
        vo.setCodigoEstabelecimento(codigoEstabelecimento);
        vo.setNumeroTransacao(numeroTransacao);
        vo.setOperacao(operacao); // Código que identifica o processo que deseja realizar. Para captura, deve-se enviar o valor 1

        JAXBElement<CapturarTransacaoCompleta> requestBody =
                factory.createCapturarTransacaoCompleta(transacaoCompleta);

        JAXBElement<CapturarTransacaoCompletaResponse> jaxbResponse =
                (JAXBElement<CapturarTransacaoCompletaResponse>) webserviceTemplate.marshalSendAndReceive(requestBody);

        CapturarTransacaoCompletaResponse oneclickResponse = jaxbResponse.getValue();


        log.info("Client reeceived result='{}'", oneclickResponse.getReturn());

        return oneclickResponse.getReturn();// vo mudar o tipo de retorno do metodo. tbm fiz isso.. rsrsrsrsrsrs


    }

}
