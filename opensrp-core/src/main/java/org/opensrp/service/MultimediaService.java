package org.opensrp.service;

import java.io.File;
import java.util.List;

import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MultimediaService {
	private static Logger logger = LoggerFactory.getLogger(MultimediaService.class
			.toString());

	private final MultimediaRepository multimediaRepository;
	private String multimediaDirPath;

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository, @Value("#{opensrp['multimedia.directory.name']}") String multimediaDirName) {
		this.multimediaRepository = multimediaRepository;
		this.multimediaDirPath = multimediaDirName;
	}

	public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {
		
		boolean uploadStatus = uploadFile(multimediaDTO, file);
         
		String[] multimediaDirPathSplit =  multimediaDirPath.split("/", 3);
		String multimediaDirPathDB = File.separator + multimediaDirPathSplit[2];
		
		if (uploadStatus) {
			try {
				logger.info("Image path : " + multimediaDirPath);
				
				Multimedia multimediaFile = new Multimedia()
						.withCaseId(multimediaDTO.caseId())
						.withProviderId(multimediaDTO.providerId())
						.withContentType(multimediaDTO.contentType())
						.withFilePath(multimediaDirPathDB)
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
		 
		if (!multimediaFile.isEmpty()) {
			try {

				 multimediaDirPath += File.separator + multimediaDTO.providerId()+ File.separator;

				switch (multimediaDTO.contentType()) {
				
				case "application/octet-stream":
					String videoDirPath = multimediaDirPath += "videos";
					makeMultimediaDir(videoDirPath);
					multimediaDirPath += File.separator
							+ multimediaDTO.caseId() + ".mp4";
					break;

				case "image/jpeg":
					String jpgImgDirPath = multimediaDirPath += "images";
					makeMultimediaDir(jpgImgDirPath);
					multimediaDirPath += File.separator
							+ multimediaDTO.caseId() + ".jpg";
					break;

				case "image/gif":
					String gifImgDirPath = multimediaDirPath += "images";
					makeMultimediaDir(gifImgDirPath);
					multimediaDirPath += File.separator
							+ multimediaDTO.caseId() + ".gif";
					break;

				case "image/png":
					String pngImgDirPath = multimediaDirPath += "images";
					makeMultimediaDir(pngImgDirPath);
					multimediaDirPath += File.separator
							+ multimediaDTO.caseId() + ".png";
					break;

				default:
					String defaultDirPath = multimediaDirPath += "images";
					makeMultimediaDir(defaultDirPath);
					multimediaDirPath += File.separator
							+ multimediaDTO.caseId() + ".jpg";
					break;

				}

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
    private void makeMultimediaDir(String dirPath)
    {
    	File file = new File(dirPath);
		 if(!file.exists())
			 file.mkdirs();
			 
    }
	public List<Multimedia> getMultimediaFiles(String providerId) {
		return multimediaRepository.all(providerId);
	}
}
