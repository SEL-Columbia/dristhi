package org.opensrp.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.opensrp.service.XlsFormDownloaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/xlsform")
public class XlsFormDownloaderController {
	
	
	private XlsFormDownloaderService xlsService;
	@Autowired
	public XlsFormDownloaderController(XlsFormDownloaderService xlsService) {
		this.xlsService=xlsService;
	}
	
	@RequestMapping(method = GET, value = "/index")
	public ModelAndView showPage(HttpServletRequest request) throws UnsupportedEncodingException {
	
		//String path=request.getContextPath();
		//System.out.println(" path to files"+getPath() );
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		return new ModelAndView("xlsformdownloader", model);

	}
	@RequestMapping(method=POST , value="/addfiles")
	public ModelAndView addFiles(HttpServletRequest request) throws UnsupportedEncodingException{
		Map<String, Object> model = new HashMap<String, Object>();
		String userName=request.getParameter("userName").trim();
		String formId=request.getParameter("formId").trim();
		String formName=request.getParameter("formName").trim();
		String formPk=request.getParameter("formPk").trim();
		String password=request.getParameter("password").trim();
		System.out.println("Password : "+password);
		
//		System.out.println(userName+"   "+formId);
		//String username=request.getParameter("username");
		//String path=request.getContextPath();
		//System.out.println(" path to files"+getPath() );
	String formDefinition="" ;
		boolean check=false;
		try {
		check=	xlsService.downloadFormFiles(getPath().trim()+"form", userName, password,formId,formPk, formName);
		formDefinition=xlsService.getFormDefinition();
		
		//Gson gson = new Gson();

		

		//	BufferedReader br = new BufferedReader(
		//		new FileReader(getPath().trim()+"form/"+formName+"form.json"));

			//convert the json string back to object
			//FormSubmission obj = gson.fromJson(br, FormSubmission.class);

			//System.out.println(obj);

		 

	//	model.put("success", msg);
		} catch (IOException e) {
			check=false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String msg=check==true?"Files downloaded in directory":"files not downloaded  !";
		model.put("msg", msg);
		model.put("definition", formDefinition);
		model.put("check", check);
		
		return new ModelAndView("xlsformdownloader", model);
	}
	
	public String getPath() throws UnsupportedEncodingException {

		String path = this.getClass().getClassLoader().getResource("").getPath();

		String fullPath = URLDecoder.decode(path, "UTF-8");

		//String pathArr[] = fullPath.split("/WEB-INF/classes/");

	//	System.out.println(fullPath);

		//System.out.println(pathArr[0]);

	//	fullPath = pathArr[0];

		

	//	String reponsePath = "";

// to read a file from webcontent

	//	reponsePath = new File(fullPath).getPath() + File.separatorChar + "newfile.txt";

		return fullPath;

	}

}
