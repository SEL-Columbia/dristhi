package org.opensrp.web.rest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnEntityFacadeRESTTest {
 
  @Before
  public void setUp() {
    RuntimeDelegate.setInstance(runtimeDelegate);
  }
 
  @Test
  public void testCreate() throws Exception {
    AnEntity anEntity = mock(AnEntity.class);
    UriBuilder uriBuilder = mock(UriBuilder.class);
    URI uri = URI.create("http://restin.local/rest/anentity/");
    ResponseBuilder responseBuilder = mock(ResponseBuilder.class);
    Response response = mock(Response.class);
 
    when((uriInfo).getRequestUriBuilder()).thenReturn(uriBuilder);
    when((uriBuilder).path(isA(String.class))).thenReturn(uriBuilder);
    when((uriBuilder).build()).thenReturn(uri);
    when((runtimeDelegate).createResponseBuilder()).thenReturn(responseBuilder);
    when((runtimeDelegate).createUriBuilder()).thenReturn(uriBuilder);
    when((responseBuilder).status(Response.Status.CREATED)).thenReturn(responseBuilder);
    when(Response.status(Response.Status.CREATED)).thenReturn(responseBuilder);
    when((responseBuilder).location(uri)).thenReturn(responseBuilder);
    when((responseBuilder).entity(any(AnEntity.class))).thenReturn(responseBuilder);
    when((responseBuilder).contentLocation(uri)).thenReturn(responseBuilder);
    when((responseBuilder).build()).thenReturn(response);
 
    Response result = instance.create(anEntity);
 
    verify(em).persist(anEntity);
    verify(em).flush();
 
    assertEquals(result, response);
  }
 
  @Mock
  private EntityManager em;
 
  @Mock
  private UriInfo uriInfo;
 
  @Mock
  private RuntimeDelegate runtimeDelegate;
 
  @InjectMocks
  private AnEntityFacadeREST instance;
}