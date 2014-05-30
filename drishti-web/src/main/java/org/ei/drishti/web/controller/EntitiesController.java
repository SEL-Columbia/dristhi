package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.domain.EntityDetail;
import org.ei.drishti.dto.register.EntityDetailDTO;
import org.ei.drishti.repository.AllDrishtiUsers;
import org.ei.drishti.service.EntitiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class EntitiesController {
    private static Logger logger = LoggerFactory.getLogger(EntitiesController.class.toString());
    private EntitiesService service;
    private AllDrishtiUsers allDrishtiUsers;

    @Autowired
    public EntitiesController(EntitiesService service, AllDrishtiUsers allDrishtiUsers) {
        this.service = service;
        this.allDrishtiUsers = allDrishtiUsers;
    }

    @RequestMapping(method = GET, value = "/entities-as-json")
    @ResponseBody
    public ResponseEntity<List<EntityDetailDTO>> allEntities(@RequestParam("anmIdentifier") String anmIdentifier) {
        try {
            List<EntityDetail> entityDetails = service.entities(anmIdentifier);
            logger.info("JSON map of all entities");
            return new ResponseEntity<>(mapToDTO(entityDetails), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(MessageFormat.format("{0} occurred while fetching ANM Details. StackTrace: \n {1}", exception.getMessage(), ExceptionUtils.getFullStackTrace(exception)));
        }
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }

    private List<EntityDetailDTO> mapToDTO(List<EntityDetail> entityDetails) {
        List<EntityDetailDTO> entityDetailsDTO =
                with(entityDetails)
                        .convert(new Converter<EntityDetail, EntityDetailDTO>() {
                            @Override
                            public EntityDetailDTO convert(EntityDetail entry) {
                                return new EntityDetailDTO()
                                        .withECNumber(entry.ecNumber())
                                        .withThayiCardNumber(entry.thayiCardNumber())
                                        .withEntityID(entry.entityID())
                                        .withANMIdentifier(entry.anmIdentifier())
                                        .withEntityType(entry.entityType());
                            }
                        });
        return entityDetailsDTO;
    }
}
