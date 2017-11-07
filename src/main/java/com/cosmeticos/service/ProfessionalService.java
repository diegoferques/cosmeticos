package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.PriceRuleRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private HabilityService habilityService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    public Optional<Professional> find(Long idProfessional) {
        return Optional.ofNullable(professionalRepository.findOne(idProfessional));
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

        professionalRepository.save(newProfessional);

        //AQUI SALVAMOS LATITUDE E LONGITUDE NO ADDRESS CRIADO ACIMA
        //Address address = newProfessional.getAddress();
        //addressService.updateGeocodeFromProfessional(address);
        addressService.updateGeocodeFromProfessionalCreate(newProfessional);

        configureHability(request.getProfessional(), newProfessional);

        //configureProfessionalCategory(request.getProfessional(), newProfessional);
        if(request.getProfessional().getProfessionalCategoryCollection() != null)
        {
            newProfessional.setProfessionalCategoryCollection(request.getProfessional().getProfessionalCategoryCollection());
            newProfessional.getProfessionalCategoryCollection().forEach(pc -> {
                pc.setProfessional(newProfessional);

                // Relacionamento bidirecional
                if ( pc.getPriceRuleList() != null) {
                    pc.getPriceRuleList().forEach(pr -> {
                        pr.setProfessionalCategory(pc);
                    });
                }
            });
        }

        //SALVAMOS 2 VEZES PROFESSIONAL? EH ISSO MESMO?
        return professionalRepository.save(newProfessional);
    }


    public Optional<Professional> update(ProfessionalRequestBody request) {
        Professional receivedProfessional = request.getProfessional();

        Optional<Professional> optional = Optional.ofNullable(professionalRepository.findOne(receivedProfessional.getIdProfessional()));

        if(optional.isPresent()) {

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

            if(!StringUtils.isEmpty(receivedProfessional.getAttendance())){
                persistentProfessional.setAttendance(receivedProfessional.getAttendance());
            }

            if(receivedProfessional.getUser() != null){
                User persistentUser = persistentProfessional.getUser();

                Set<Vote> requestVotes = receivedProfessional.getUser().getVoteCollection();

                for (Vote v : requestVotes) {
                    persistentUser.addVote(v);
                }
            }

            if(receivedProfessional.getEmployeesCollection() != null){
                for(Professional professionalItem : receivedProfessional.getEmployeesCollection()){
                    Professional persistentProfessionalItem = professionalRepository.findOne(professionalItem.getIdProfessional());
                    persistentProfessional.addEmployees(persistentProfessionalItem);
                }
            }

            if(receivedProfessional.getBoss() != null){
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
                    Professional persistentProfessionalItem = professionalRepository.findOne(professionalItem.getIdProfessional());
                    persistentProfessional.addEmployees(persistentProfessionalItem);
                }
            }
            */


            configureProfessionalCategory(receivedProfessional, persistentProfessional);
            
            professionalRepository.save(persistentProfessional);

            return Optional.of(persistentProfessional);
        }
        else{
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
	 Por exemplo: 
	 
	 (1)
	 Professional p = new Professional();
	 p.setNameProfessional("Joao");
	 Vai gerar: SELECT * FROM Professional WHERE nameProfessional = 'Joao'
	 
	 
	 (2)
	 Professional p = new Professional();
	 p.setNameProfessional("Joao");
	 p.setCnpj("123456");
	 Vai gerar: SELECT * FROM Professional WHERE nameProfessional = 'Joao' and cnpj = '123456'
	 
	 Fonte: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
	 */
    public List<Professional> findAllBy(Professional professionalProbe) {

        // Se esses 3 atributos forem passados vai bugar pq chegando aqui os anularemos.
        if ( professionalProbe.getUser() != null ) {
            professionalProbe.getUser().setLostPassword(null);
            professionalProbe.getUser().setCreditCardCount(null);
            professionalProbe.getUser().setEvaluation(null);
        }

        return professionalRepository.findAll(Example.of(professionalProbe));
    }

    /**
     * Importante: Se receivedProfessional.getProfessionalCategoryCollection == null nada sera alterado. Se for vazio, limparemos essa lista
     * no banco tbm. Eh importante considerar que antes repassar o que chegou no request para newProfessional, newProfessional sofre um
     * newProfessional.getProfessionalCategoryCollection().clear().
     *
     *
     * Este metodo faz diversos deletes, por isso necessita de transação requeres new, pois caso hajam erros, todos os deletes sofrem rollback.
     *
     * Se a lista de ProfessionalCategories ou priceRule vierem nulas ou vazias, nenhuma ação é tomada. Essas listas nao podem ser vazias.
     * @param receivedProfessional
     * @param persistentProfessional
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void configureProfessionalCategory(Professional receivedProfessional, Professional persistentProfessional) {

        Set<ProfessionalCategory> receivedPcCollection = receivedProfessional.getProfessionalCategoryCollection();

        if(receivedPcCollection != null && !receivedPcCollection.isEmpty())
        {
            persistentProfessional.setProfessionalCategoryCollection(receivedPcCollection);
            persistentProfessional.getProfessionalCategoryCollection().forEach(pc -> {
                pc.setProfessional(persistentProfessional);
                if(pc.getPriceRuleList() != null && !pc.getPriceRuleList().isEmpty()) {
                    // Relacionamento bidirecional
                    pc.getPriceRuleList().forEach(pr -> {
                        pr.setProfessionalCategory(pc);
                    });
                }
            });
        }
    }

    private void configureHability(Professional receivedProfessional, Professional newProfessional) {
        Collection<Hability> habilityList = receivedProfessional.getHabilityCollection();

        if(habilityList != null)
        {
            for (Hability h : receivedProfessional.getHabilityCollection()) {

                Optional<Hability> optional = Optional.ofNullable(habilityService.findByName(h.getName()));

                if(optional.isPresent()) {
                    newProfessional.getHabilityCollection().add(optional.get());
                }
                else
                {
                    Hability newHability = new Hability();
                    newHability.setName(h.getName());
                    Hability persistentHability = habilityService.create(newHability);

                    newProfessional.getHabilityCollection().add(persistentHability);
                }
            }
        }
    }

    public void deleteEmployee(Long bossId, Long employeeId) {
        Professional boss = professionalRepository.findOne(bossId);

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
}

