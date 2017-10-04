package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.Hability;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
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
    private HabilityService habilityService;

    @Autowired
    private AddressService addressService;

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
        configureProfessionalServices(request.getProfessional(), newProfessional);

        //SALVAMOS 2 VEZES PROFESSIONAL? EH ISSO MESMO?
        return professionalRepository.save(newProfessional);
    }


    public Optional<Professional> update(ProfessionalRequestBody request) {
        Professional cr = request.getProfessional();

        Optional<Professional> optional = Optional.ofNullable(professionalRepository.findOne(cr.getIdProfessional()));

        if(optional.isPresent()) {

            Professional professional = optional.get();

            if (!StringUtils.isEmpty(cr.getBirthDate())) {
                professional.setBirthDate(cr.getBirthDate());
            }

            if (!StringUtils.isEmpty(cr.getCellPhone())) {
                professional.setCellPhone(cr.getCellPhone());
            }

            if (!StringUtils.isEmpty(cr.getCnpj())) {
                professional.setCnpj(cr.getCnpj());
            }

            if (!StringUtils.isEmpty(cr.getGenre())) {
                professional.setGenre(cr.getGenre());
            }

            if (!StringUtils.isEmpty(cr.getNameProfessional())) {
                professional.setNameProfessional(cr.getNameProfessional());
            }

            if (!StringUtils.isEmpty(cr.getStatus())) {
                professional.setStatus(cr.getStatus());
            }

            if(!StringUtils.isEmpty(cr.getAttendance())){
                professional.setAttendance(cr.getAttendance());
            }

            //AQUI SALVAMOS LATITUDE E LONGITUDE NO ADDRESS CRIADO ACIMA
            if (cr.getAddress() != null) {
                addressService.updateGeocodeFromProfessionalUpdate(cr);
                //addressService.updateGeocodeFromProfessional(professional);
            }

            configureProfessionalServices(cr, professional);
            
            professionalRepository.save(professional);

            return Optional.of(professional);
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
        return professionalRepository.findAll(Example.of(professionalProbe));
    }

    private void configureProfessionalServices(Professional receivedProfessional, Professional newProfessional) {
        Set<ProfessionalCategory> receivedProfessionalServices =
                receivedProfessional.getProfessionalCategoryCollection();

        if (receivedProfessionalServices != null) {
			receivedProfessionalServices.stream().forEach(ps -> {
				ps.setProfessional(newProfessional);

				newProfessional.getProfessionalCategoryCollection().add(ps);
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

}

