package org.opensrp.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.opensrp.web.security.DrishtiAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/multimedia")
public class MultimediaController {
    private static Logger logger = LoggerFactory.getLogger(MultimediaController.class.toString());
   
    
    
    @Value("#{opensrp['multimedia.directory.name']}") 
    String multiMediaDir;
    
    @Autowired
	@Qualifier("drishtiAuthenticationProvider")
	DrishtiAuthenticationProvider provider;
    
   /**
    * Download a file from the multimedia directory. This method is set to bypass spring security config but authenticate through the username/password passed at the headers
    * @param response
    * @param fileName
    * @param userName
    * @param password
    * @throws IOException
    */
    @RequestMapping(value="/download/{fileName:.+}", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, @PathVariable("fileName") String fileName,@RequestHeader(value="username") String userName,@RequestHeader(value="password") String password) throws IOException {
    	
    	Authentication auth = new UsernamePasswordAuthenticationToken( userName, password);
		auth = provider.authenticate(auth);
		
		if(auth.isAuthenticated()){
			
        File file = new File(multiMediaDir+File.separator+fileName);
         
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
    
}
