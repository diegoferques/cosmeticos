package com.cosmeticos.controller;

import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Controller
public class PaymentController {

    @Value("${superpay.url.transacao}")
    private String urlTransacao;

    @Value("${superpay.estabelecimento}")
    private String estabelecimento;

    @Value("${superpay.login}")
    private String login;

    @Value("${superpay.senha}")
    private String senha;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public enum Language {
        NONE, PORTUGUESE, ENGLISH, SPANISH
    }

    @RequestMapping(path = "/campainha/superpay/", method = RequestMethod.POST)
    public HttpEntity<CampainhaSuperpeyResponseBody> create(
            @RequestParam(name = "numeroTransacao") Long numeroTransacao,
            @RequestParam(name = "codigoEstabelecimento") Long codigoEstabelecimento,
            @RequestParam(required = false, name = "campoLivre1") String campoLivre1,
            @RequestParam(required = false, name = "campoLivre2") String campoLivre2,
            @RequestParam(required = false, name = "campoLivre3") String campoLivre3,
            @RequestParam(required = false, name = "campoLivre4") String campoLivre4,
            @RequestParam(required = false, name = "campoLivre5") String campoLivre5) {

        CampainhaSuperpeyResponseBody responseBody = new CampainhaSuperpeyResponseBody();
        Optional<Order> orderOptional = Optional.ofNullable(orderRepository.findOne(numeroTransacao));

        try {

            if(numeroTransacao == null) {

                responseBody.setDescription("O campo \"numeroTransacao\" é obrigatório.");
                log.error(responseBody.getDescription());

                return badRequest().body(responseBody);

            } else if(codigoEstabelecimento == null) {

                responseBody.setDescription("O campo \"codigoEstabelecimento\" é obrigatório.");
                log.error(responseBody.getDescription());

                return badRequest().body(responseBody);

            } else if(codigoEstabelecimento != Long.parseLong(estabelecimento)) {

                responseBody.setDescription("\"codigoEstabelecimento\" desconhecido.");
                log.error(responseBody.getDescription());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

            } else if(!orderOptional.isPresent()) {

                responseBody.setDescription("Order não encontrado:" + numeroTransacao);
                log.error(responseBody.getDescription());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);

            } else {
                //AQUI VAMOS CHAMAR O METODO QUE VAI NO SUPERPAY VERIFICAR SE HOUVE UMA ATUALIZACAO NA TRANSACAO
                //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
                Boolean consultaTransacao = this.consultaTransacao(numeroTransacao, codigoEstabelecimento);

                if(consultaTransacao) {
                    responseBody.setDescription("Campainha sinalizada e status do pagamento para Order com o ID ["+
                            numeroTransacao +"] foram atualizados com sucesso.");
                    log.error(responseBody.getDescription());
                    return ok(responseBody);

                } else {
                    String errorCode = String.valueOf(System.nanoTime());
                    responseBody.setDescription("Erro ao consultar a transação de ID ["+ numeroTransacao +"]. Error Code: ["+ errorCode +"].");
                    log.error(responseBody.getDescription());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
                }

            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            responseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    private Map<String, Integer> usuarioMap = getFormasPagamento();

    private Map<String, Integer> getFormasPagamento() {
        Map<String, Integer> formasPagamento = new HashMap<String, Integer>();
        formasPagamento.put( "Visa", 170);
        formasPagamento.put("MasterCard", 171);
        formasPagamento.put("Itau", 29);

        return formasPagamento;
    }

    public String sendRequest(Order orderCreated) throws ParseException, JsonProcessingException {

        Order order = orderRepository.findOne(orderCreated.getIdOrder());

        System.out.println(">>>> Teste RESTv2");//verificar abaixo pq address nao esta indo em order
        TransacaoRequest request = createRequest(order);

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

        Map<String, String> usuarioMap = new HashMap<>();
        usuarioMap.put("usuario", jsonHeader);

        String jsonRequest = om.writeValueAsString(request);

        System.out.println(jsonHeader);
        System.out.println(jsonRequest);

        String response = postJson(urlTransacao, usuarioMap, jsonRequest);
        System.out.println(response);
        System.out.println("<<<<");

        return response;

    }

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

        List<ItemPedido> itensDoPedido = this.getItensDoPedido(order.getProfessionalServices());
        request.setItensDoPedido(itensDoPedido);

        DadosCobranca dadosCobranca = this.getDadosCobranca(order.getIdCustomer());
        request.setDadosCobranca(dadosCobranca);

        DadosEntrega dadosEntrega = this.getDadosEntrega(order.getIdCustomer());
        request.setDadosEntrega(dadosEntrega);

        return request;
    }

    private Transacao getTransacao(Order order) {

        Transacao transacao = new Transacao();
        //TODO - AINDA NAO TRABALHAMOS COM PARCELAS, SETEI MANUALMENTE COMO 0(ZERO)
        transacao.setParcelas(1);
        //TODO - AINDA NAO TRABALHAMOS COM IDIOMAS, SETEI MANUALMENTE COMO 1(PORTUGUES)
        transacao.setIdioma(Language.PORTUGUESE.ordinal());
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

    private DadosCartao getDadosCartao(CreditCard creditCard) {
        //TODO - COMO NAO TEMOS COMO PEGAR OS DADOS DO CARTAO UTILIZADO PARA PAGAMENTO EM ORDER, SETEI MANUALMENTE
        DadosCartao dadosCartao = new DadosCartao();
        dadosCartao.setNomePortador(creditCard.getOwnerName());
        dadosCartao.setNumeroCartao(creditCard.getCardNumber());
        dadosCartao.setCodigoSeguranca(creditCard.getSecurityCode());
        dadosCartao.setDataValidade(creditCard.getExpirationDate());

        return dadosCartao;
    }

    private List<ItemPedido> getItensDoPedido(ProfessionalServices professionalServices) {

        List<ItemPedido> itensDoPedido = new ArrayList<>();
        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(professionalServices.getService().getIdService().toString());
        item.setNomeProduto(professionalServices.getService().getCategory());
        //item.setCodigoCategoria("1");
        //item.setNomeCategoria("Categoria de testes");
        //item.setQuantidadeProduto(1);
        //item.setValorUnitarioProduto(100);
        itensDoPedido.add(item);

        return itensDoPedido;
    }

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

    public CreditCard getCartaoTeste() throws ParseException {

        CreditCard creditCard = new CreditCard();
        creditCard.setOwnerName("Cliente Teste");
        creditCard.setCardNumber("0000000000000001");
        creditCard.setSecurityCode("123");

        //TODO - FOI NECESSARIO FAZER O QUE SEGUE ABAIXO NESTE CARTAO TESTE, MAS DEVERIA SER STRING, POIS EH MES/ANO
        //SimpleDateFormat dateFormat = new SimpleDateFormat("mm,yyyy");
        //Date expiration = dateFormat.parse("12,2026");
        //creditCard.setExpirationDate(expiration);
        creditCard.setExpirationDate("12/2026");

        creditCard.setVendor("Visa");

        return creditCard;
    }

    private String postJson(String url, Map<String, String> headers, String data) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        String result = "";
        System.out.println("HEADERS: " + headers);
        System.out.println("BODY: " + data);

        try {
            RequestEntity<String> entity = RequestEntity
                    .post(new URI(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("usuario", headers.get("usuario"))
                    .accept(MediaType.APPLICATION_JSON)
                    .body(data);

            ResponseEntity<String> exchange = restTemplate
                    .exchange(entity, String.class);

            result = exchange.getBody();

        } catch (Exception e) {
            log.error(e.toString());
            result = e.toString();

        }

			/*
			HttpPost post = new HttpPost(url);

			post.setEntity(new BufferedHttpEntity(
					new InputStreamEntity(new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8"))))));

			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json;charset=UTF-8");
			if (headers != null) {
				for (Entry<String, String> h : headers.entrySet()) {
					post.setHeader(h.getKey(), h.getValue());
				}
			}

			int connectionTimeout = 63_000;
			int readTimeout = 10 * connectionTimeout;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout)
					.setSocketTimeout(readTimeout).build();
			try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
				HttpResponse response = client.execute(post);
				log.info("Response status code: "
						+ (response.getStatusLine() == null ? 0 : response.getStatusLine().getStatusCode()));

				result = EntityUtils.toString(response.getEntity());
			}

		} catch (Exception e) {
			log.error(e.toString());
			result = e.toString();
		}
		*/

        return result;
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

    //TODO - VERIFICAR NO GATEWAY SUPERPAY SE HOUVE UMA ATUALIZACAO NA TRANSACAO
    //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
    private Boolean consultaTransacao(Long numeroTransacao, Long codigoEstabelecimento) {
        return true;
    }
}
