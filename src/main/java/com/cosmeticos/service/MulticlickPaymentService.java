package com.cosmeticos.service;

import java.net.URI;
import java.util.Optional;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.repository.PaymentRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.payment.superpay.client.rest.model.Usuario;
import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.controller.PaymentController;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class MulticlickPaymentService implements Charger {

    @Value("${superpay.url.transacao}")
    private String urlTransacao;

    @Value("${superpay.estabelecimento}")
    private String estabelecimento;

    @Value("${superpay.login}")
    private String login;

    @Value("${superpay.senha}")
    private String senha;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private Map<String, Integer> formasPagamentoMap = getFormasPagamentoMap();

    //POR AQUI QUE CHEGA ORDER COM STATUS PARA SOLICITAR A COBRANCA/RESERVA NA SUPERPAY
    private ChargeResponse<Object> sendRequest(Order orderCreated) {

        try {
            Order order = orderRepository.findOne(orderCreated.getIdOrder());

            TransacaoRequest request = createRequest(order);

            ObjectMapper om = new ObjectMapper();
            String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

            Map<String, String> usuarioMap = new HashMap<>();
            usuarioMap.put("usuario", jsonHeader);

            String jsonRequest = om.writeValueAsString(request);

            System.out.println(jsonHeader);
            System.out.println(jsonRequest);

            //String response = postJson(urlTransacao, formasPagamentoMap, jsonRequest);
            ChargeResponse<RetornoTransacao> retornoTransacao = postJson(urlTransacao, usuarioMap, jsonRequest);

            return new ChargeResponse<>(retornoTransacao);
        } catch (JsonProcessingException e) {
            throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Falha convertendo json", e);
        }

    }

    //AQUI ENVIAMOS A REQUISICAO PARA A SUPERPAY
    //DEVERIA SER PRIVADO, MAS COMO PRECISAMOS MOCKAR NO TESTE, TIVE QUE DEIXAR PUBLICO

    /**
     * ATENÇÃO: NÃO USAR ESTE MÉTODO, SÓ ESTÁ PÚBLICO PARA MOCKAR EM MockingPaymentControllerTests!
     *
     * @param url
     * @param headers
     * @param data
     * @return Optional<RetornoTransacao>
     */
    private ChargeResponse<RetornoTransacao> postJson(String url, Map<String, String> headers, String data) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        RetornoTransacao retornoTransacao;

        System.out.println("HEADERS: " + headers);
        System.out.println("BODY: " + data);

        try {
            RequestEntity<String> entity = RequestEntity
                    .post(new URI(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("usuario", headers.get("usuario"))
                    .accept(MediaType.APPLICATION_JSON)
                    .body(data);

            ResponseEntity<RetornoTransacao> exchange = restTemplate
                    .exchange(entity, RetornoTransacao.class);

            retornoTransacao = exchange.getBody();

            return new ChargeResponse<>(retornoTransacao);
        } catch (URISyntaxException e) {
           throw  new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Url invalida ao chamar superpay", e);
        }
    }

    //TODO - CRIAR AS ROTINAS PARA SALVAR NO BANCO O QUE FOR NECESSARIO DO RETORNO DO PAGAMENTO NO GATEWAY DE PAGAMENTO

    //TODO - TEMOS QUE DEFINIR LOGO SE VAMOS USAR ORDER ID PARA CRIAR OS PEDIDOS NA SUPERPAY OU SE VAMOS USAR PAYMENT ID
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/118
    private ResponseEntity<RetornoTransacao> capturaTransacaoSuperpay(Long numeroTransacao)  {

        try {
            RestTemplate restTemplate = restTemplateBuilder.build();

            String urlCapturaTransacao = urlTransacao + "/" + estabelecimento + "/" + numeroTransacao + "/capturar";

            ObjectMapper om = new ObjectMapper();
            String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

            RequestEntity<RetornoTransacao> entity = RequestEntity
                    .post(new URI(urlCapturaTransacao))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("usuario", jsonHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(null);

            ResponseEntity<RetornoTransacao> exchange = doCapturaTransacaoRequest(restTemplate, entity);

            if (exchange.getStatusCode() == HttpStatus.CONFLICT) {
                log.warn("Conflitou");
            }

            return exchange;
        } catch (JsonProcessingException | URISyntaxException e) {
            throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Falha preparando a captura de pagamento", e);
        }
    }

    public ResponseEntity<RetornoTransacao> doCapturaTransacaoRequest(RestTemplate restTemplate, RequestEntity<RetornoTransacao> entity) {
        return restTemplate.exchange(entity, RetornoTransacao.class);
    }

    //TODO - VERIFICAR NO GATEWAY SUPERPAY SE HOUVE UMA ATUALIZACAO NA TRANSACAO
    //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/129
    private Optional<RetornoTransacao> consultaTransacao(Long numeroTransacao)  {

        RestTemplate restTemplate = restTemplateBuilder.build();

        String urlConsultaTransacao = urlTransacao + "/" + estabelecimento + "/" + numeroTransacao;

        RetornoTransacao retornoTransacao;

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = null;
        try {
            jsonHeader = om.writeValueAsString(new Usuario(login, senha));
        } catch (JsonProcessingException e) {
            throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Falha convertendo json", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("usuario", jsonHeader);

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<RetornoTransacao> exchange = doConsultaTransacao(restTemplate, urlConsultaTransacao, entity);

        if (exchange.getStatusCode() == HttpStatus.OK) {
            retornoTransacao = exchange.getBody();

        } else {
            retornoTransacao = null;

        }

        if (exchange.getStatusCode() == HttpStatus.CONFLICT) {
            log.warn("Conflitou");
        }

        return Optional.ofNullable(retornoTransacao);
    }

    public ResponseEntity<RetornoTransacao> doConsultaTransacao(RestTemplate restTemplate, String urlConsultaTransacao, HttpEntity entity) {
        return restTemplate.exchange(
                urlConsultaTransacao,
                HttpMethod.GET,
                entity,
                RetornoTransacao.class
                //        param
        );
    }

    //MONTAMOS A REQUISICAO PARA ENVIAR PARA A SUPERPAY
    private TransacaoRequest createRequest(Order order)  {

        //TODO - PRECISAMOS DE ALGO PARECIDO COMO O QUE SEGUE ABAIXO PARA PEGAR OS DADOS DA FORMA DE PAGAMENTO DE ORDER
        //CreditCard creditCard = order.getPayment().getCreditCard();
        CreditCard creditCard = this.getPaymentCreditCard(order);

        TransacaoRequest request = new TransacaoRequest();

        request.setCodigoEstabelecimento(estabelecimento);

        request.setCodigoFormaPagamento(formasPagamentoMap.get(creditCard.getVendor()));

        Transacao transacao = this.getTransacao(order);
        request.setTransacao(transacao);

        DadosCartao dadosCartao = this.getDadosCartao(creditCard);
        request.setDadosCartao(dadosCartao);

        List<ItemPedido> itensDoPedido = this.getItensDoPedido(order.getProfessionalCategory());
        request.setItensDoPedido(itensDoPedido);

        //TIVE QUE ADICIONAR ISSO AQUI PARA CONSEGUIR PEGAR E PASSAR OS DADOS DE USER, POIS O DE ORDER ESTA COMO NULL
        Customer customer = customerRepository.findOne(order.getIdCustomer().getIdCustomer());

        DadosCobranca dadosCobranca = this.getDadosCobranca(customer);
        request.setDadosCobranca(dadosCobranca);

        DadosEntrega dadosEntrega = this.getDadosEntrega(customer);
        request.setDadosEntrega(dadosEntrega);

        return request;
    }

    private Map<String, Integer> getFormasPagamentoMap() {
        Map<String, Integer> formasPagamento = new HashMap<String, Integer>();
        formasPagamento.put("Visa", 170);
        formasPagamento.put("MasterCard", 171);
        formasPagamento.put("Itau", 29);

        return formasPagamento;
    }

    //MONTAMOS A TRANSACAO PARA UTILIZAR NA REQUISICAO
    private Transacao getTransacao(Order order) {

        Transacao transacao = new Transacao();
        //TODO - AINDA NAO TRABALHAMOS COM PARCELAS, SETEI MANUALMENTE COMO 0(ZERO)
        transacao.setParcelas(1);
        //TODO - AINDA NAO TRABALHAMOS COM IDIOMAS, SETEI MANUALMENTE COMO 1(PORTUGUES)
        transacao.setIdioma(PaymentController.Language.PORTUGUESE.ordinal());
        //TODO - URL CAMPAINHA E URL RETORNO
        transacao.setUrlCampainha("http://campainha.com.br");
        transacao.setUrlResultado("http://retorno.com.br");
        //TODO - AINDA NAO TRABALHAMOS COM DESCONTOS, SETEI MANUALMENTE COMO 0(ZERO)
        transacao.setValorDesconto(0);
        //TODO - COMO PEGAREMOS O VALOR TOTAL DE ORDER?
        transacao.setValor(100);
        transacao.setNumeroTransacao(order.getIdOrder());

        return transacao;
    }

    //MONTAMOS OS DADOS DO CARTAO PARA UTILIZAR NA REQUISICAO
    private DadosCartao getDadosCartao(CreditCard creditCard) {
        //TODO - COMO NAO TEMOS COMO PEGAR OS DADOS DO CARTAO UTILIZADO PARA PAGAMENTO EM ORDER, SETEI MANUALMENTE
        DadosCartao dadosCartao = new DadosCartao();
        dadosCartao.setNomePortador(creditCard.getOwnerName());
        dadosCartao.setNumeroCartao(creditCard.getCardNumber());
        dadosCartao.setCodigoSeguranca(creditCard.getSecurityCode());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/yyyy");
        dadosCartao.setDataValidade(dateFormatter.format(creditCard.getExpirationDate()));

        return dadosCartao;
    }

    //PEGAMOS OS ITENS DO PEDIDO PARA UTILIZAR NA REQUISICAO
    private List<ItemPedido> getItensDoPedido(ProfessionalCategory professionalServices) {

        List<ItemPedido> itensDoPedido = new ArrayList<>();
        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(professionalServices.getCategory().getIdCategory().toString());
        item.setNomeProduto(professionalServices.getCategory().getName());
        //item.setCodigoCategoria("1");
        //item.setNomeCategoria("Categoria de testes");
        //item.setQuantidadeProduto(1);
        //item.setValorUnitarioProduto(100);
        itensDoPedido.add(item);

        return itensDoPedido;
    }

    //PEGAMOS OS DADOS DA COBRANCA PARA UTILIZAR NA REQUISICAO
    private DadosCobranca getDadosCobranca(Customer customer) {

        DadosCobranca dadosCobranca = new DadosCobranca();

        dadosCobranca.setCodigoCliente(Integer.valueOf(customer.getIdCustomer().intValue()));

        //TODO - NAO TEMOS DEFINICAO DE PESSOA FISICA OU JURIDICA, SETEI MANUALMENTE COMO FISICA SEMPRE
        dadosCobranca.setTipoCliente(1);
        dadosCobranca.setNome(customer.getNameCustomer());
        dadosCobranca.setEmail(customer.getUser().getEmail());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dadosCobranca.setDataNascimento(dateFormatter.format(customer.getBirthDate()).toString());
        dadosCobranca.setSexo((String.valueOf(customer.getGenre())));
        dadosCobranca.setDocumento(customer.getCpf());
        dadosCobranca.setDocumento2(null);

        dadosCobranca.setEndereco(this.getEnderecoCobranca(customer.getAddress()));

        //TODO - COMO CUSTOMER NAO TEM DDI E DDD SEPARADOS DO NUMERO E NAO E OBRIGATORIO NO SUPERPAY, COMENTEI!
            /*
            List<Telefone> telefone = new ArrayList<>();
                Telefone tel = new Telefone();
                    tel.setTipoTelefone(1);
                    tel.setDdi("55");
                    tel.setDdd("11");
                    tel.setTelefone(customer.getCellPhone());
                telefone.add(tel);
            dadosCobranca.setTelefone(telefone);
            */
        return dadosCobranca;
    }

    //PEGAMOS OS DADOS DA ENTREGA PARA UTILIZAR NA REQUISICAO
    private DadosEntrega getDadosEntrega(Customer customer) {

        DadosEntrega dadosEntrega = new DadosEntrega();

        //TODO - COMO NAO TRABALHAMOS COM DADOS DE ENTREGA E COBRANCA, ESTOU ENVIANDO O MESMO SETADO PARA COBRANCA
        dadosEntrega.setNome(customer.getNameCustomer());
        dadosEntrega.setEmail(customer.getUser().getEmail());
        dadosEntrega.setDataNascimento(customer.getBirthDate().toString());
        dadosEntrega.setSexo((String.valueOf(customer.getGenre())));
        dadosEntrega.setDocumento(customer.getCpf());
        dadosEntrega.setDocumento2(null);

        //TODO - COMO NAO TRABALHAMOS COM ENDERECO DE ENTREGA E COBRANCA, ESTOU ENVIANDO O MESMO ENDERECO DE COBRANCA
        dadosEntrega.setEndereco(this.getEnderecoCobranca(customer.getAddress()));

        //TODO - COMO ADDRESS NAO TEM DDI E DDD SEPARADOS E NAO E OBRIGATORIO NO SUPERPAY, COMENTEI!
        //dadosEntrega.setTelefone(telefone);

        return dadosEntrega;
    }

    //PEGAMOS O ENDERECO DA COBRANCA PARA UTILIZAR NA REQUISICAO
    private Endereco getEnderecoCobranca(Address orderAddress) {
        //TODO - NAO SEI POR QUAL MOTIVO, MAS OS DADOS DO ENDERECO NAO ESTAO VINDO NEM ACIMA E NEM ABAIXO
        Address address = addressRepository.findOne(orderAddress.getIdAddress());
        Endereco enderecoCobranca = new Endereco();

        enderecoCobranca.setLogradouro(address.getAddress());
        //TODO - COMO ADDRESS NAO POSSUI NUMERO E NAO E OBRIGATORIO NO SUPERPAY, COMENTEI PARA NAO SER ENVIADO
        //enderecoCobranca.setNumero("224");
        //TODO - COMO ADDRESS NAO POSSUI COMPLEMENTO E NAO E OBRIGATORIO NO SUPERPAY, COMENTEI PARA NAO SER ENVIADO
        //enderecoCobranca.setComplemento(null);
        enderecoCobranca.setCep(address.getCep());
        enderecoCobranca.setBairro(address.getNeighborhood());
        enderecoCobranca.setCidade(address.getCity());
        enderecoCobranca.setEstado(address.getState());
        //TODO - COMO COUNTRY EM ADDRESS TEM MAIS DE 2 CARACTERES E NAO E OBRIGATORIO NO SUPERPAY, COMENTEI!
        //enderecoCobranca.setPais(address.getCountry());

        return enderecoCobranca;
    }

    //GERAMOS UM CARTAO DE TESTE DA SUPERPAY PARA USAR NOS TESTES
    public CreditCard getPaymentCreditCard(Order order)  {

        Customer persistentCustomer = customerRepository.findOne(order.getIdCustomer().getIdCustomer());

        Optional<CreditCard> optionalCc = persistentCustomer
                .getUser()
                .getCreditCardCollection()
                .stream()
                .findFirst();

        if (optionalCc.isPresent()){
            return optionalCc.get();
        }else{
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_TYPE, "Cartão não cadastrado");
        }
    }


    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //TODO - ACHO QUE PRECISA DE MAIS VALIDACOES, BEM COMO QUANDO DER ERRO DE CONSULTA OU CAPTURA POR 404, 500 E ETC.
    public Boolean validatePaymentStatusAndSendCapture(Payment payment)  {

        Order order = payment.getOrder();

        Boolean validateAndCapture = false;

        if (order.getStatus() == Order.Status.READY2CHARGE) {

            Optional<RetornoTransacao> retornoConsultaTransacao = consultaTransacao(payment.getId());

            if (retornoConsultaTransacao.isPresent()) {

                //SE O STATUS ESTIVER NA SUPERPAY COMO PAGO E NAO CAPTURADO, ENTAO ENVIAMOS O PEDIDO DE CAPTURA
                if (retornoConsultaTransacao.get().getStatusTransacao() == 2) {
                    ResponseEntity<RetornoTransacao> exchange = this.capturaTransacao(order.getIdOrder());

                    RetornoTransacao retornoTransacao = exchange.getBody();

                    payment.setStatus(Payment.Status.fromSuperpayStatus(retornoTransacao.getStatusTransacao()));

                    updatePaymentStatus(payment);

                    validateAndCapture = true;
                } else {
                    log.error("Status do pagamento na Superpay não permite captura");
                }
            }

        } else {
            throw new OrderValidationException(ResponseCode.FORBIDEN_PAYMENT, "Status da Order não permite efetuar captura do pagamento");
        }

        return validateAndCapture;

    }

    //MUDEI DE PUBLIC PARA PRIVATE, AGORA TEMOS QUE CHAMAR O VALIDATE ANTES DE ENVIAR PARA A SUPERPAY
    //DEVERA SER VISTO SOMENTE PELA CLASSE
    //AQUI CAPTURAMOS A TRANSACAO NA SUPERPAY E, CASO RETORNE HTTP STATUS 200(OK), ATUALIZAMOS O STATUS DE PAYMENT DA ORDER
    private ResponseEntity<RetornoTransacao> capturaTransacao(Long numeroTransacao)  {

        ResponseEntity<RetornoTransacao> exchange = capturaTransacaoSuperpay(numeroTransacao);

        if (exchange.getStatusCode() == HttpStatus.OK) {

            RetornoTransacao retornoTransacao = exchange.getBody();

            Payment payment = paymentRepository.findOne(numeroTransacao);
            payment.setStatus(Payment.Status.fromSuperpayStatus(retornoTransacao.getStatusTransacao()));

            Boolean result = updatePaymentStatus(payment);

            if (result == false) {
                throw new OrderValidationException(ResponseCode.INTERNAL_ERROR,
                        "Erro ao atualizar o status de Payment ID {"+numeroTransacao+"} após capturar na Superpay"
                );
            }
        }

        return exchange;
    }

    private CampainhaSuperpeyResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        CampainhaSuperpeyResponseBody responseBody = new CampainhaSuperpeyResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }

    @Override
    public ChargeResponse addCard(ChargeRequest chargeRequest) {
        log.debug("Nao implementado.");
        return null;
    }

    @Override
    public ChargeResponse<Object> reserve(ChargeRequest<Payment> chargeRequest) {

        Payment payment = chargeRequest.getBody();

        ChargeResponse<Object> response = this.sendRequest(payment.getOrder());

        RetornoTransacao retornoTransacao = (RetornoTransacao) response.getBody();

        Integer superpayStatusStransacao = retornoTransacao.getStatusTransacao();

        Payment.Status paymentStatus = Payment.Status.fromSuperpayStatus(superpayStatusStransacao);

        payment.setStatus(paymentStatus);

        MDC.put("paymentStatus", paymentStatus.toString());

        // Atualiza o status, sendo sucesso ou nao.
        Boolean updateStatusPagamento = updatePaymentStatus(payment);

        if (!updateStatusPagamento) {
            log.warn("Erro ao salvar o status do pagamento");
        }

        //if (paymentStatus.isSuccess()) {
        //
        //    //TODO - URGENTE
        //    //ENVIAMOS OS DADOS DO PAGAMENTO EFETUADO NA SUPERPAY PARA SALVAR O STATUS DO PAGAMENTO
        //    //OBS.: COMO ESSE METODO AINDA NAO FOI IMPLEMENTADO, ELE ESTA RETORNANDO BOOLEAN
        //    Boolean updateStatusPagamento =updatePaymentStatus(payment);
//
        //    if (!updateStatusPagamento) {
        //        //TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO DER UM ERRO AO ATUALIZAR O STATUS DO PAGAMENTO
        //        // DIEGO, nao devemos lancar ero aqui pq o pagamento ja foi feito. Devemos prosseguir com a trasacao, logar um warn e trabalhar na correçao
        //        log.warn("Erro ao salvar o status do pagamento");
        //       // throw new RuntimeException("Erro salvar o status do pagamento");
        //    }
        //}

        if (paymentStatus.isSuccess()) {
            response.setResponseCode(ResponseCode.SUCCESS);
        }
        else
        {
            response.setResponseCode(paymentStatus.getResponseCode());
        }
        return response;
    }

    @Override
    public ChargeResponse<Object> capture(ChargeRequest<Payment> chargeRequest) {
        return new ChargeResponse(validatePaymentStatusAndSendCapture(chargeRequest.getBody()));
    }

    @Override
    public ChargeResponse<Object> getStatus(ChargeRequest<Payment> chargeRequest) {


        Optional<Payment> payment = Optional.ofNullable(chargeRequest.getBody());
        if(payment.isPresent())
            return new ChargeResponse(consultaTransacao(payment.get().getId()));
        else
            throw new OrderValidationException(ResponseCode.INTERNAL_ERROR, "Nao conseguimos encontrar o id da transacao");
    }

    @Override
    public Boolean updatePaymentStatus(Payment payment) {
        return true;
    }

}
