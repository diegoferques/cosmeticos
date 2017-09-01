package com.cosmeticos.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class PaymentService {

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
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private Map<String, Integer> usuarioMap = getFormasPagamento();

    //POR AQUI QUE CHEGA ORDER COM STATUS PARA SOLICITAR A COBRANCA/RESERVA NA SUPERPAY
    public Optional<RetornoTransacao> sendRequest(Order orderCreated) throws ParseException, JsonProcessingException {

        Order order = orderRepository.findOne(orderCreated.getIdOrder());

        TransacaoRequest request = createRequest(order);

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

        Map<String, String> usuarioMap = new HashMap<>();
        usuarioMap.put("usuario", jsonHeader);

        String jsonRequest = om.writeValueAsString(request);

        System.out.println(jsonHeader);
        System.out.println(jsonRequest);

        //String response = postJson(urlTransacao, usuarioMap, jsonRequest);
        Optional<RetornoTransacao> retornoTransacao = postJson(urlTransacao, usuarioMap, jsonRequest);

        return retornoTransacao;

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
    public Optional<RetornoTransacao> postJson(String url, Map<String, String> headers, String data) {
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

        } catch (Exception e) {
            log.error(e.toString());
            retornoTransacao = null;
        }

        return Optional.ofNullable(retornoTransacao);
    }

    //TODO - CRIAR AS ROTINAS PARA SALVAR NO BANCO O QUE FOR NECESSARIO DO RETORNO DO PAGAMENTO NO GATEWAY DE PAGAMENTO

    //TODO - TEMOS QUE DEFINIR LOGO SE VAMOS USAR ORDER ID PARA CRIAR OS PEDIDOS NA SUPERPAY OU SE VAMOS USAR PAYMENT ID
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/118
    public ResponseEntity<RetornoTransacao> capturaTransacaoSuperpay(Long numeroTransacao) throws JsonProcessingException, URISyntaxException {

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
    }

    public ResponseEntity<RetornoTransacao> doConsultaTransacaoRequest(RestTemplate restTemplate,
                                                                       RequestEntity<RetornoTransacao> entity) {
        return restTemplate.exchange(entity, RetornoTransacao.class);
    }

    public ResponseEntity<RetornoTransacao> doCapturaTransacaoRequest(RestTemplate restTemplate, RequestEntity<RetornoTransacao> entity) {
        return restTemplate.exchange(entity, RetornoTransacao.class);
    }

    //TODO - VERIFICAR NO GATEWAY SUPERPAY SE HOUVE UMA ATUALIZACAO NA TRANSACAO
    //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/129
    public Optional<RetornoTransacao> consultaTransacao(Long numeroTransacao) throws JsonProcessingException {

        RestTemplate restTemplate = restTemplateBuilder.build();

        String urlConsultaTransacao = urlTransacao + "/" + estabelecimento + "/" + numeroTransacao;

        RetornoTransacao retornoTransacao;

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

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

    //TODO - COMO AINDA NAO TEMOS STATUS DE PAGAMENTO DE ORDER, SERA NECESSARIO IMPLEMENTAR ESTE METODO POSTERIORMENTE
    public Boolean updatePaymentStatus(RetornoTransacao retornoTransacao) {

        return true;
    }


    //MONTAMOS A REQUISICAO PARA ENVIAR PARA A SUPERPAY
    private TransacaoRequest createRequest(Order order) throws ParseException {

        //TODO - PRECISAMOS DE ALGO PARECIDO COMO O QUE SEGUE ABAIXO PARA PEGAR OS DADOS DA FORMA DE PAGAMENTO DE ORDER
        //CreditCard creditCard = order.getPayment().getCreditCard();
        CreditCard creditCard = this.getCartaoTeste();

        TransacaoRequest request = new TransacaoRequest();

        request.setCodigoEstabelecimento(estabelecimento);

        request.setCodigoFormaPagamento(usuarioMap.get(creditCard.getVendor()));

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

    private Map<String, Integer> getFormasPagamento() {
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
    public CreditCard getCartaoTeste() throws ParseException {

        CreditCard creditCard = new CreditCard();
        creditCard.setOwnerName("Cliente Teste");
        creditCard.setCardNumber("0000000000000001");
        creditCard.setSecurityCode("123");

        //SimpleDateFormat dateFormat = new SimpleDateFormat("mm,yyyy");
        //Date expiration = dateFormat.parse("12,2026");
        //creditCard.setExpirationDate(expiration);
        //new Date(2026,12,01);
        //new LocalDate(2026,12, 01).get();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        creditCard.setExpirationDate(sdf.parse("01/12/2026"));

        creditCard.setVendor("Visa");

        return creditCard;
    }


    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //TODO - ACHO QUE PRECISA DE MAIS VALIDACOES, BEM COMO QUANDO DER ERRO DE CONSULTA OU CAPTURA POR 404, 500 E ETC.
    public Boolean validatePaymentStatusAndSendCapture(Order order) throws JsonProcessingException, URISyntaxException, OrderValidationException {

        //Payment payment = paymentRepository.findOne(order.getPayment().getId());

        Boolean validateAndCapture = false;

        if (order.getStatus() == Order.Status.READY2CHARGE) {

            Optional<RetornoTransacao> retornoConsultaTransacao = consultaTransacao(order.getIdOrder());

            if (retornoConsultaTransacao.isPresent()) {

                //SE O STATUS ESTIVER NA SUPERPAY COMO PAGO E NAO CAPTURADO, ENTAO ENVIAMOS O PEDIDO DE CAPTURA
                if (retornoConsultaTransacao.get().getStatusTransacao() == 2) {
                    ResponseEntity<RetornoTransacao> exchange = this.capturaTransacao(order.getIdOrder());
                    validateAndCapture = true;
                } else {
                    log.error("Status do pagamento na Superpay não permite captura");
                }
            }

        } else {
            throw new OrderValidationException(OrderValidationException.Type.FORBIDEN_PAYMENT, "Status da Order não permite efetuar captura do pagamento");
        }

        return validateAndCapture;

    }

    //MUDEI DE PUBLIC PARA PRIVATE, AGORA TEMOS QUE CHAMAR O VALIDATE ANTES DE ENVIAR PARA A SUPERPAY
    //DEVERA SER VISTO SOMENTE PELA CLASSE
    //AQUI CAPTURAMOS A TRANSACAO NA SUPERPAY E, CASO RETORNE HTTP STATUS 200(OK), ATUALIZAMOS O STATUS DE PAYMENT DA ORDER
    private ResponseEntity<RetornoTransacao> capturaTransacao(Long numeroTransacao) throws JsonProcessingException, URISyntaxException {

        ResponseEntity<RetornoTransacao> exchange = capturaTransacaoSuperpay(numeroTransacao);

        if (exchange.getStatusCode() == HttpStatus.OK) {

            Boolean result = updatePaymentStatus(exchange.getBody());

            if (result == false) {
                log.error("Erro ao atualizar o status de Order ID {} após capturar na Superpay", numeroTransacao);
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
}
