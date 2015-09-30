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
        
        private static Logger logger = LoggerFactory
			.getLogger(MultimediaService.class.toString());

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository, @Value("#{drishti['multimedia.directory.name']}") String baseMultimediaDirPath) {
		this.multimediaRepository = multimediaRepository;
	}

	public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {
		logger.info("Save Multimedia file");
		boolean uploadStatus = uploadFile(multimediaDTO, file);

		if (uploadStatus) {
			try {
                                logger.info("try to Save Multimedia file");
				Multimedia multimediaFile = new Multimedia()
						.withCaseId(multimediaDTO.caseId())
						.withProviderId(multimediaDTO.providerId())
						.withContentType(multimediaDTO.contentType())
						.withFilePath(multimediaDirPath)
						.withFileCategory(multimediaDTO.fileCategory());

				multimediaRepository.add(multimediaFile);

				return "success";

			} catch (Exception e) {
				e.getMessage();
			}
		}

		return "fail";

	}

	public boolean uploadFile(MultimediaDTO multimediaDTO,
			MultipartFile multimediaFile) {
            logger.info("Upload file in location");
		String baseMultimediaDirPath = "../assets/multimedia";
		
		// String baseMultimediaDirPath = System.getProperty("user.home");

		if (!multimediaFile.isEmpty()) {
			try {

				 multimediaDirPath = baseMultimediaDirPath
						+ File.separator + multimediaDTO.providerId()
						+ File.separator;

				switch (multimediaDTO.contentType()) {
				
				case "application/octet-stream":
					multimediaDirPath += "videos" + File.separator
							+ multimediaDTO.caseId() + ".mp4";
					break;

				case "image/jpeg":
					multimediaDirPath += "images" + File.separator
							+ multimediaDTO.caseId() + ".jpg";
					break;

				case "image/gif":
					multimediaDirPath += "images" + File.separator
							+ multimediaDTO.caseId() + ".gif";
					break;

				case "image/png":
					multimediaDirPath += "images" + File.separator
							+ multimediaDTO.caseId() + ".png";
					break;

				default:
					multimediaDirPath += "images" + File.separator
							+ multimediaDTO.caseId() + ".jpg";
					break;

				}
                                logger.info("uploded");    
				File multimediaDir = new File(multimediaDirPath);

				 multimediaFile.transferTo(multimediaDir);

			/*
			 byte[] bytes = multimediaFile.getBytes();
			 	
			 BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(multimediaDirPath));
				stream.write(bytes);
				stream.close();*/
				 
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
}
