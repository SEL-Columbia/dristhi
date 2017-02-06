package org.opensrp.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.service.MultimediaService;
import org.opensrp.web.security.DrishtiAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
@RequestMapping("/multimedia")
public class MultimediaController {
    private static Logger logger = LoggerFactory.getLogger(MultimediaController.class.toString());
   
    
    
    @Value("#{opensrp['multimedia.directory.name']}") 
    String multiMediaDir;
    
    @Autowired
	@Qualifier("drishtiAuthenticationProvider")
	DrishtiAuthenticationProvider provider;
    
    @Autowired
    MultimediaService multimediaService;
    
   /**
    * Download a file from the multimedia directory. 
    * The method also assumes two file types mp4 and images whereby all images are stored in the images folder and videos in mp4 in the multimedia directory
    * This method is set to bypass spring security config but authenticate through the username/password passed at the headers
    * @param response
    * @param fileName
    * @param userName
    * @param password
    * @throws IOException
    */
    @RequestMapping(value="/download/{fileName:.+}", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, @PathVariable("fileName") String fileName,@RequestHeader(value="username") String userName,@RequestHeader(value="password") String password) throws Exception {
    	
		
		if(authenticate(userName,password).isAuthenticated()){
			File file =new File(multiMediaDir+File.separator+"images"+File.separator+fileName);
			if(fileName.endsWith("mp4")){
				file= new File(multiMediaDir+File.separator+"videos"+File.separator+fileName);
			}
         
        downloadFile(file, response);
      }
    }
    /**
     * This method downloads a file from the server given the client id. A search is made to the multimedia repo to see if any file exists mapped to the user whereby the filepath is recorded
     * @param response
     * @param baseEntityId
     * @param userName
     * @param password
     * @throws Exception
     */
    @RequestMapping(value="/profileimage/{baseEntityId}", method = RequestMethod.GET)
    public void downloadFileByClientId(HttpServletResponse response, @PathVariable("baseEntityId") String baseEntityId,@RequestHeader(value="username") String userName,@RequestHeader(value="password") String password) throws Exception {
    	
    	
		
		if(authenticate(userName,password).isAuthenticated()){
			
		Multimedia multiMedia = multimediaService.findByCaseId(baseEntityId);
		String filePath=multiMedia.getFilePath();
			
        File file = new File(filePath);
        downloadFile(file, response);
        
        
    }
		
	}
    
    @RequestMapping(headers = {"Accept=multipart/form-data"}, method = POST, value = "/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("anm-id") String providerId, @RequestParam("entity-id") String entityId, @RequestParam("file-category") String fileCategory, @RequestParam("file") MultipartFile file) {
    	
    	String contentType= file.getContentType();
    	
    	MultimediaDTO multimediaDTO = new MultimediaDTO(entityId, providerId, contentType, null, fileCategory);
    	
    	String status = multimediaService.saveMultimediaFile(multimediaDTO, file);
    	 
    	 return new ResponseEntity<>(new Gson().toJson(status), HttpStatus.OK);
    }
    private Authentication authenticate(String userName,String password){
    	Authentication auth = new UsernamePasswordAuthenticationToken( userName, password);
		auth = provider.authenticate(auth);
		return auth;
    }
    
    private void downloadFile(File file, HttpServletResponse response) throws Exception{
    	 
        if(!file.exists()){
            String errorMessage = "Sorry. The file you are looking for does not exist";
            logger.info(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }
         
        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
        	logger.info("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }
         
        logger.info("mimetype : "+mimeType);
         
        response.setContentType(mimeType);
         
        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser 
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
 
         
        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
         
        response.setContentLength((int)file.length());
 
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
 
        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    
    
}
