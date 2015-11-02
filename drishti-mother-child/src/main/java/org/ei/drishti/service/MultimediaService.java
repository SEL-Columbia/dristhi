/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ei.drishti.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import org.ei.drishti.domain.Multimedia;
import org.ei.drishti.dto.form.MultimediaDTO;
import org.ei.drishti.repository.MultimediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author administrator
 */
@Service
public class MultimediaService {

    private final MultimediaRepository multimediaRepository;
    private String multimediaDirPath;
    private String path;

    private static Logger logger = LoggerFactory
            .getLogger(MultimediaService.class.toString());

    @Autowired
    public MultimediaService(MultimediaRepository multimediaRepository, @Value("#{drishti['multimedia.directory.name']}") String baseMultimediaDirPath) {
        this.multimediaRepository = multimediaRepository;
    }

    public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {
        logger.info("Save Multimedia file");

        path = multimediaDTO.providerId();
        boolean uploadStatus = uploadFile(multimediaDTO, file);

        if (uploadStatus) {
            try {
                logger.info("try to Save Multimedia file");
                Multimedia multimediaFile = new Multimedia()
                        .withCaseId(multimediaDTO.caseId())
                        .withProviderId(multimediaDTO.providerId())
                        .withContentType(multimediaDTO.contentType())
                        .withFilePath(path)
                        .withFileCategory(multimediaDTO.fileCategory());

                multimediaRepository.add(multimediaFile);

                return "success"+":"+path;

            } catch (Exception e) {
                e.getMessage();
            }
        }

        return "fail";

    }

    public boolean uploadFile(MultimediaDTO multimediaDTO,
            MultipartFile multimediaFile) {
        logger.info("Upload file in location");
       String baseMultimediaDirPath = "/var/lib/tomcat7/webapps/drishti-web-0.1-SNAPSHOT";
       // String baseMultimediaDirPath = "/var/www/html/Downloads/test";
        if (!multimediaFile.isEmpty()) {
            try {

                multimediaDirPath = baseMultimediaDirPath
                        + File.separator + multimediaDTO.providerId()
                        + File.separator;
                 //multimediaDirPath = "/var/www/html/hs/";

                switch (multimediaDTO.contentType()) {

                    case "audio/x-wav":
                        
                        multimediaDirPath +=
                                "audios" ;
                        File file=new File(multimediaDirPath);
                        if(!file.exists())
                            file.mkdirs();
                        multimediaDirPath += File.separator + multimediaDTO.caseId() + ".wav";
                        path += File.separator + "audios" + File.separator + multimediaDTO.caseId() + ".wav";
                        break;

                    case "image/jpeg":
                        multimediaDirPath += "images" + File.separator
                                + multimediaDTO.caseId() + ".jpg";
                        path += File.separator + "images" + File.separator + multimediaDTO.caseId() + ".jpeg";
                        break;

                    case "image/gif":
                        multimediaDirPath += "images" + File.separator
                                + multimediaDTO.caseId() + ".gif";
                        path += File.separator + "images" + File.separator + multimediaDTO.caseId() + ".gif";
                        break;

                    case "image/png":
                        multimediaDirPath += "images" + File.separator + multimediaDTO.caseId() + ".png";
                        path += File.separator + "images" + File.separator + multimediaDTO.caseId() + ".png";
                        break;

                    default:
                        multimediaDirPath += "images" + File.separator
                                + multimediaDTO.caseId() + ".jpg";
                        path += File.separator + "images" + File.separator + multimediaDTO.caseId() + ".jpg";
                        break;

                }
                logger.info("uploded");
                logger.info("content-type: " + multimediaDTO.contentType());
                logger.info("directory path: " + multimediaDirPath);
                
                if(multimediaDTO.contentType().equalsIgnoreCase("audio/x-wav")){
                    saveAudio(multimediaFile, multimediaDirPath);
                }
                else{
                    File multimediaDir = new File(multimediaDirPath);
                    multimediaDir.mkdirs();
                logger.info("test directory created: " + multimediaDir);

                logger.info("File created");
                multimediaFile.transferTo(multimediaDir);
                logger.info("file data transfer done");
                }
//                byte[] bytes = multimediaFile.getBytes();
//
//                BufferedOutputStream stream = new BufferedOutputStream(
//                        new FileOutputStream(new File(multimediaDirPath)));
//                logger.info("directory created");
//                stream.write(bytes);
//                stream.close();

                return true;

            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<Multimedia> getMultimediaFiles(String providerId) {
        return multimediaRepository.all(providerId);
    }
    
    private void saveAudio(MultipartFile multimediaFile, String multimediaDirPath){
        try{
            
        
        byte[] bytes = multimediaFile.getBytes();

                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File(multimediaDirPath)));
                logger.info("directory created");
                stream.write(bytes);
                stream.close();
                
        }
        catch(Exception e){
            System.out.println(e);
        }
    } 
}
