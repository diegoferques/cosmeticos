package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.PriceRuleRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.smtp.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;

import static java.time.LocalDateTime.now;

/**
 * Created by matto on 27/05/2017.
 */
@Slf4j
@Service
public class ProfessionalService {

    @Autowired
    private MailSenderService emailService;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HabilityService habilityService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Autowired
    private BalanceItemService balanceItemService;

    @Value("${exception.unresolved.destination.email}")
    private String corpEmail;

    @Value("${balance.rescue.request.mail.subject}")
    private String rescueRequestMailSubject;

    @Value("${balance.rescue.request.mail.body}")
    private String rescueRequestMailBody;

    public Optional<Professional> find(Long idProfessional) {
        return (professionalRepository.findById(idProfessional));
    }

    public Professional create(ProfessionalRequestBody request) {

        Professional newProfessional = new Professional();

        newProfessional.setBirthDate(request.getProfessional().getBirthDate());
        newProfessional.setCellPhone(request.getProfessional().getCellPhone());
        newProfessional.setCnpj(request.getProfessional().getCnpj());
        newProfessional.setGenre(request.getProfessional().getGenre());
        newProfessional.setNameProfessional(request.getProfessional().getNameProfessional());
        newProfessional.setStatus(Professional.Status.ACTIVE);
        newProfessional.setDateRegister(Calendar.getInstance().getTime());
        newProfessional.setUser(request.getProfessional().getUser());
        newProfessional.getUser().setProfessional(newProfessional);
        newProfessional.setAddress(request.getProfessional().getAddress());
        newProfessional.setAttendance(request.getProfessional().getAttendance());

        //professionalRepository.save(newProfessional);

        //AQUI SALVAMOS LATITUDE E LONGITUDE NO ADDRESS CRIADO ACIMA
        //Address address = newProfessional.getAddress();
        //addressService.updateGeocodeFromProfessional(address);
        addressService.updateGeocodeFromProfessionalCreate(newProfessional);

        configureHability(request.getProfessional(), newProfessional);

        //configureProfessionalCategory(request.getProfessional(), newProfessional);
        if (request.getProfessional().getProfessionalCategoryCollection() != null) {
            newProfessional.setProfessionalCategoryCollection(request.getProfessional().getProfessionalCategoryCollection());
            newProfessional.getProfessionalCategoryCollection().forEach(pc -> {
                pc.setProfessional(newProfessional);

                // Relacionamento bidirecional
                if (pc.getPriceRuleList() != null) {
                    pc.getPriceRuleList().forEach(pr -> {
                        pr.setProfessionalCategory(pc);
                    });
                }
            });
        }

        newProfessional.getUser().setStatus(User.Status.ACTIVE);

        //SALVAMOS 2 VEZES PROFESSIONAL? EH ISSO MESMO?
        // GArry: removi o primeiro save
        return professionalRepository.save(newProfessional);
    }


    public Optional<Professional> update(Professional receivedProfessional) {

        Optional<Professional> optional = (professionalRepository.findById(receivedProfessional.getIdProfessional()));

        if (optional.isPresent()) {

            Professional persistentProfessional = optional.get();

            if (!StringUtils.isEmpty(receivedProfessional.getBirthDate())) {
                persistentProfessional.setBirthDate(receivedProfessional.getBirthDate());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getCellPhone())) {
                persistentProfessional.setCellPhone(receivedProfessional.getCellPhone());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getCnpj())) {
                persistentProfessional.setCnpj(receivedProfessional.getCnpj());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getGenre())) {
                persistentProfessional.setGenre(receivedProfessional.getGenre());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getNameProfessional())) {
                persistentProfessional.setNameProfessional(receivedProfessional.getNameProfessional());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getStatus())) {
                persistentProfessional.setStatus(receivedProfessional.getStatus());
            }

            if (!StringUtils.isEmpty(receivedProfessional.getAttendance())) {
                persistentProfessional.setAttendance(receivedProfessional.getAttendance());
            }

            if (receivedProfessional.getUser() != null) {
                User persistentUser = persistentProfessional.getUser();

                Set<Vote> requestVotes = receivedProfessional.getUser().getVoteCollection();

                for (Vote v : requestVotes) {
                    persistentUser.addVote(v);
                }

                // Como user pode chegar sem o id, convenientemente configuramos o id.
                User receivedUser = receivedProfessional.getUser();
                receivedUser.setIdLogin(persistentUser.getIdLogin());
                userService.update(receivedUser);
            }

            if (receivedProfessional.getEmployeesCollection() != null) {
                for (Professional professionalItem : receivedProfessional.getEmployeesCollection()) {
                    Professional persistentProfessionalItem = professionalRepository.findById(professionalItem.getIdProfessional()).get();
                    persistentProfessional.addEmployees(persistentProfessionalItem);
                }
            }

            if (receivedProfessional.getBoss() != null) {
                persistentProfessional.setBoss(receivedProfessional.getBoss());
            }

            //AQUI SALVAMOS LATITUDE E LONGITUDE NO ADDRESS CRIADO ACIMA
            if (receivedProfessional.getAddress() != null) {
                addressService.updateGeocodeFromProfessionalUpdate(receivedProfessional);
                //addressService.updateGeocodeFromProfessional(professional);
            }
            /*
            if(cr.getEmployeesCollection() != null){
                for(Professional professionalItem : cr.getEmployeesCollection()){
                    Professional persistentProfessionalItem = professionalRepository.findById(professionalItem.getIdProfessional());
                    persistentProfessional.addEmployees(persistentProfessionalItem);
                }
            }
            */

            configureProfessionalCategory(receivedProfessional, persistentProfessional);

            professionalRepository.save(persistentProfessional);

            return Optional.of(persistentProfessional);
        } else {
            return optional;
        }
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }


    public List<Professional> findAll() {
        return professionalRepository.findTop10ByOrderByDateRegisterDesc();
    }

    /**
     * Recebe uma instancia nova transiente de Professional e inclui os atributos preenchidos em uma clausula WHERE.
     * Por exemplo:
     * <p>
     * (1)
     * Professional p = new Professional();
     * p.setNameProfessional("Joao");
     * Vai gerar: SELECT * FROM Professional WHERE nameProfessional = 'Joao'
     * <p>
     * <p>
     * (2)
     * Professional p = new Professional();
     * p.setNameProfessional("Joao");
     * p.setCnpj("123456");
     * Vai gerar: SELECT * FROM Professional WHERE nameProfessional = 'Joao' and cnpj = '123456'
     * <p>
     * Fonte: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
     */
    public List<Professional> findAllBy(Professional professionalProbe) {

        // Se esses 3 atributos forem passados vai bugar pq chegando aqui os anularemos.
        if (professionalProbe.getUser() != null) {
            professionalProbe.getUser().setLostPassword(null);
            professionalProbe.getUser().setEvaluation(null);
        }

        return professionalRepository.findAll(Example.of(professionalProbe));
    }

    /**
     * Importante: Se receivedProfessional.getProfessionalCategoryCollection == null nada sera alterado. Se for vazio, limparemos essa lista
     * no banco tbm. Eh importante considerar que antes repassar o que chegou no request para newProfessional, newProfessional sofre um
     * newProfessional.getProfessionalCategoryCollection().clear().
     * <p>
     * <p>
     * Este metodo faz diversos deletes, por isso necessita de transação requeres new, pois caso hajam erros, todos os deletes sofrem rollback.
     * <p>
     * Se a lista de ProfessionalCategories ou priceRule vierem nulas ou vazias, nenhuma ação é tomada. Essas listas nao podem ser vazias.
     *
     * @param receivedProfessional
     * @param persistentProfessional
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void configureProfessionalCategory(Professional receivedProfessional, Professional persistentProfessional) {

        if (receivedProfessional.getProfessionalCategoryCollection().isEmpty()) {
            log.debug("Ignorando atualizacao de categorias do Professional[{}] pois a lista de categorias nao " +
                    "pode estar vazia.", receivedProfessional.getIdProfessional());
        } else {

            Set<ProfessionalCategory> transientProfessionalCategories = new HashSet<>();
            Set<ProfessionalCategory> receivedIdentifiedProfessionalCategories = new HashSet<>();

            // Coletando ProfessionalCategories que ainda nao existem em persistentProfessional
            for (ProfessionalCategory professionalCategoryRequest :
                    receivedProfessional.getProfessionalCategoryCollection()) {
                if (professionalCategoryRequest.getProfessionalCategoryId() == null) {
                    transientProfessionalCategories.add(professionalCategoryRequest);
                } else {
                    receivedIdentifiedProfessionalCategories.add(professionalCategoryRequest);
                }
            }

            removeProfessionalCategoriesNotPresentInRequest(receivedProfessional, persistentProfessional);

            //Se professionalCategoryCollection estiver vazio, sera um INSERT NOVO, entao nao temos problema
            if (!persistentProfessional.getProfessionalCategoryCollection().isEmpty()) {

                for (ProfessionalCategory receivedIdentifiedPC : receivedIdentifiedProfessionalCategories) {
                    Optional<ProfessionalCategory> foundPersistentPC = persistentProfessional.getProfessionalCategoryCollection()
                            .stream().filter(persistent -> persistent.equals(receivedIdentifiedPC))
                            .findFirst();

                    // Agora Conferimos se as price rules do que ja esta no banco bate com o que foi recebido.
                    adjustPriceRules(receivedIdentifiedPC, foundPersistentPC.get());
                }

                //Agora limpamos a lista de professionalCategoryCollection e setamos a nova somente com os INSERTS
                for (ProfessionalCategory pc : transientProfessionalCategories) {

                    // Fazendo o bidirecional com a lista de PriceRules
                    for (PriceRule pr : pc.getPriceRuleList()) {
                        pr.setProfessionalCategory(pc);
                    }

                    persistentProfessional.addProfessionalCategory(pc);
                    professionalCategoryRepository.save(pc);
                }
            } else {
                // Tratando caso onde profissional NAO possui categorias.
                for (ProfessionalCategory pc : receivedProfessional.getProfessionalCategoryCollection()) {

                    // Fazendo o bidirecional com a lista de PriceRules
                    for (PriceRule pr : pc.getPriceRuleList()) {
                        pr.setProfessionalCategory(pc);
                    }

                    persistentProfessional.addProfessionalCategory(pc);
                }
            }
        }
    }

    void removeProfessionalCategoriesNotPresentInRequest(Professional receivedProfessional, Professional persistentProfessional) {
        ArrayList<ProfessionalCategory> professionalCategoriesToErase = new ArrayList<>();

        //Agora verificamos alguma category existe no persistente mas nao existe no request e deletaremos
        for (ProfessionalCategory professionalCategoryPersistent :
                persistentProfessional.getProfessionalCategoryCollection()) {

            Set<ProfessionalCategory> receivedProfessionalCategories = receivedProfessional.getProfessionalCategoryCollection();

            final Long persistentIdCategory = professionalCategoryPersistent.getCategory().getIdCategory();

            Optional<ProfessionalCategory> foundInRequest = receivedProfessionalCategories.stream()
                    .filter(received -> received.getCategory().getIdCategory() == persistentIdCategory)
                    .findFirst();

            if (!foundInRequest.isPresent()) {
                professionalCategoriesToErase.add(professionalCategoryPersistent);
            }
        }

        // Removendo fora do FOR pra evitar ConcurrentModificationException
        for (ProfessionalCategory pcToRemove :
                professionalCategoriesToErase) {

            pcToRemove.setStatus(ProfessionalCategory.Status.DELETED);
            professionalCategoryRepository.save(
                    pcToRemove
            );

            persistentProfessional.getProfessionalCategoryCollection()
                    .remove(pcToRemove);
        }
    }

    private void adjustPriceRules(ProfessionalCategory receivedPC, ProfessionalCategory persistentPC) {

        //Se professionalCategoryCollection estiver vazio, sera um INSERT NOVO, entao nao temos problema
        if (!persistentPC.getPriceRuleList().isEmpty()) {

            Set<PriceRule> transientPriceRules = new HashSet<>();
            Set<PriceRule> receivedIdentifiedPriceRules = new HashSet<>();
            Set<PriceRule> persistentPRToEraseList = new HashSet<>();

            // Coletando ProfessionalCategories que ainda nao existem em persistentProfessional
            for (PriceRule priceRuleRequest : receivedPC.getPriceRuleList()) {
                if (priceRuleRequest.getId() == null) {
                    transientPriceRules.add(priceRuleRequest);
                } else {
                    receivedIdentifiedPriceRules.add(priceRuleRequest);
                }
            }

            for (PriceRule persistentPR : persistentPC.getPriceRuleList()) {
                Optional<PriceRule> found = receivedIdentifiedPriceRules.stream()
                        .filter(rpr -> rpr.equals(persistentPR))
                        .findFirst();

                if (!found.isPresent()) {
                    persistentPRToEraseList.add(persistentPR);
                }
            }

            persistentPRToEraseList.forEach(persistentPRToErase -> {
                        persistentPC.getPriceRuleList().removeIf(
                                persistent -> persistentPRToErase.equals(persistent)
                        );
                    }
            );

            professionalCategoryRepository.save(persistentPC);

            //Agora limpamos a lista de professionalCategoryCollection e setamos a nova somente com os INSERTS
            for (PriceRule pr : transientPriceRules) {

                persistentPC.addPriceRule(pr);
                priceRuleRepository.save(pr);
            }
        } else {
            // Tratando caso onde profissional NAO possui categorias.
            for (PriceRule pr : receivedPC.getPriceRuleList()) {
                persistentPC.addPriceRule(pr);
            }
        }
    }

    private void configureHability(Professional receivedProfessional, Professional newProfessional) {
        Collection<Hability> habilityList = receivedProfessional.getHabilityCollection();

        if (habilityList != null) {
            for (Hability h : receivedProfessional.getHabilityCollection()) {

                Optional<Hability> optional = Optional.ofNullable(habilityService.findByName(h.getName()));

                if (optional.isPresent()) {
                    newProfessional.getHabilityCollection().add(optional.get());
                } else {
                    Hability newHability = new Hability();
                    newHability.setName(h.getName());
                    Hability persistentHability = habilityService.create(newHability);

                    newProfessional.getHabilityCollection().add(persistentHability);
                }
            }
        }
    }

    public void deleteEmployee(Long bossId, Long employeeId) {
        Professional boss = professionalRepository.findById(bossId).get();

        Professional employee = boss.getEmployeesCollection()
                .stream()
                .filter(emp -> emp.getIdProfessional().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Profissional " + employeeId + " nao esta associado ao Profissional " + bossId
                ));

        boss.getEmployeesCollection().remove(employee);
        employee.setBoss(null);

        professionalRepository.save(boss);
        professionalRepository.save(employee);
    }

    public Professional addCategory(Professional persistentProfessional, Category category) {

        ProfessionalCategory professionalCategory = new ProfessionalCategory(persistentProfessional, category);

        persistentProfessional.addProfessionalCategory(professionalCategory);

        professionalRepository.save(persistentProfessional);

        return persistentProfessional;

    }

    public void createRescueRequest(Long idProfessional) {

        Professional professional = professionalRepository.findById(idProfessional).get();

        if (professional.getBankAccount() != null) {
            String email = professional.getUser().getEmail();

            Long balance = balanceItemService.withdrawal(email);

            MDC.put("withdrawalRequested", String.valueOf(balance));
            MDC.put("professionalEmail", email);

            BankAccount bankAccount = professional.getBankAccount();

            String mailBody = String.format(
                    rescueRequestMailBody,
                    formatPrice(balance),
                    email,
                    professional.getIdProfessional(),
                    bankAccount.getFinancialInstitute(),
                    bankAccount.getAgency(),
                    bankAccount.getAccountNumber(),
                    bankAccount.getType().toString(),
                    bankAccount.getOwnerName(),
                    bankAccount.getOwnerCPF()
            );

            emailService.sendEmail(corpEmail, rescueRequestMailSubject, mailBody);
        } else {
            throw new IllegalStateException("Ainda nao ha conta bancaria cadastrada para realizar o resgate.");
        }
    }

    private String formatPrice(Long value) {
        return String.format("R$ %.2f", ((float) value) / 100);
    }

    public void createBankAccount(Long idProfessional, BankAccount request) {

        Professional persistentProfessional = professionalRepository.findById(idProfessional).get();

        if (persistentProfessional == null) {
            throw new IllegalArgumentException("Profissional com id: " + idProfessional + " nao encontrao.");
        }

        persistentProfessional.setBankAccount(request);
        request.setProfessional(persistentProfessional);

        professionalRepository.save(persistentProfessional);

        MDC.put("bankAccountId", String.valueOf(persistentProfessional.getBankAccount().getId()));
    }

    public List<Professional> findTop10ByOrderByUserEvaluationDesc(String receivedLatitude, String receivedLongitude, String searchRadius, Optional<String> orderByField, Integer limit) {

        List<Professional> entitylist =  professionalRepository.findTop10ByOrderByUserEvaluationDesc();

        Double distanciaLimite = Double.parseDouble(searchRadius);

        List<Professional> resultList = new ArrayList<>(10);

        for (Professional p : entitylist) {

            Address professionalAddress = p.getAddress();

            if (professionalAddress != null) {

                Optional<Double> distanceFromOpt = professionalAddress.getDistanceFrom(
                        Double.parseDouble(receivedLatitude),
                        Double.parseDouble(receivedLongitude)
                );

                if (distanceFromOpt.isPresent()) {
                    Double distance = distanceFromOpt.get();

                    if (distance <= distanciaLimite) {
                        p.setDistance(distance.longValue());
                        resultList.add(p);
                    }
                }
            }
        }

        return resultList;
    }
}

