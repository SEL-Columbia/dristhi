package org.opensrp.web.rest;  
  
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;


@RunWith(SpringJUnit4ClassRunner.class)    
@ContextConfiguration(locations={"classpath:spring/applicationContext-opensrp-web.xml"})   
public class RestClientTest  
{  
    
  private static final String BASE_URL = "http://localhost:8080/opensrp-web/client";  
    
  private Logger log = LoggerFactory.getLogger(RestClientTest.class);  
    
  private RestTemplate restTemplate;  

  private ClientResource cr;

  @Test  
  public void saveAndGet() throws Exception{  
    Client cl = restTemplate.getForObject(BASE_URL, Client.class,new Object[]{});  
    assertNotNull("no people",cl);  
  }  
  
  @Before
  public void before() throws Exception {
      ClientService dealerService = mock(ClientService.class);
      cr = new ClientResource(dealerService);
      when(dealerService.find("ei0")).thenReturn(new Client("4444"));
  }

  @Test
  public void testGetAllDealers() throws Exception {
      MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    //  mockRequest.setContentType(MediaType.APPLICATION_JSON.toString());
      mockRequest.setMethod("GET");
      mockRequest.setRequestURI("/rest/client/ei0");
      mockRequest.setAttribute(HandlerMapping.class.getName() + ".introspectTypeLevelMapping", true);

      AnnotationMethodHandlerAdapter handlerAdapter = new AnnotationMethodHandlerAdapter();
      HttpMessageConverter[] messageConverters = {new MappingJacksonHttpMessageConverter()};
      handlerAdapter.setMessageConverters(messageConverters);

      MockHttpServletResponse mockResponse = new MockHttpServletResponse();
      handlerAdapter.handle(mockRequest, mockResponse, cr);

      String actual = mockResponse.getContentAsString();
      String expected="[{\"id\":\"01\",\"name\":\"dealer-01\"}, {\"id\":\"02\",\"name\":\"dealer-02\"}]";
      assertEquals(expected, actual);
  }


}  