package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.Client;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class MultiMediaServiceTest extends BaseRepositoryTest {
	
	private MultimediaService multimediaService;
	
	private ClientService clientService;
	
	@Autowired
	@Qualifier("multimediaRepositoryPostgres")
	private MultimediaRepository multimediaRepository;
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Value("#{opensrp['multimedia.directory.name']}")
	String baseMultimediaDirPath;
	
	@Before
	public void setUp() {
		clientService = new ClientService(clientsRepository);
		multimediaService = new MultimediaService(multimediaRepository, clientService);
		multimediaService.baseMultimediaDirPath = baseMultimediaDirPath;
	}
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("multimedia.sql");
		scripts.add("client.sql");
		return scripts;
	}
	
	@Test
	public void testUploadFile() throws IOException {
		String content = "876nsfsdfs-sdfsfsdf";
		MultipartFile multimediaFile = new MockMultipartFile("mockFile", "test.jpg", "image/jpeg", content.getBytes());
		MultimediaDTO multimediaDTO = new MultimediaDTO("469597f0-eefe-4171-afef-f7234cbb2859", "biddemo",
		        multimediaFile.getContentType(), "", "profilepic");
		
		assertTrue(multimediaService.uploadFile(multimediaDTO, multimediaFile));
		
		//assertEquals(multimediaFile, multimediaService.findByCaseId("469597f0-eefe-4171-afef-f7234cbb2859"));
		
		File file = new File(baseMultimediaDirPath + File.separator + MultimediaService.IMAGES_DIR + File.separator
		        + "469597f0-eefe-4171-afef-f7234cbb2859.jpg");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());
		assertTrue(file.canRead());
		
		assertEquals(content, new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
		
	}
	
	@Test
	public void testSaveMultimediaFile() throws IOException {
		String baseEntityId = "469597f0-7453-fsfsf-afef-f723b2859";
		String content = "876nsfsdfs-sdfsfsdf";
		MultipartFile multimediaFile = new MockMultipartFile("mockFile", "test1.jpg", "image/jpeg", content.getBytes());
		MultimediaDTO multimediaDTO = new MultimediaDTO(baseEntityId, "biddemo", multimediaFile.getContentType(), "",
		        "profile_picture");
		
		assertEquals("success", multimediaService.saveMultimediaFile(multimediaDTO, multimediaFile));
		
		File file = new File(baseMultimediaDirPath + File.separator + MultimediaService.IMAGES_DIR + File.separator
		        + baseEntityId + ".jpg");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());
		assertTrue(file.canRead());
		
		assertEquals(content, new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
		Client client = clientService.find(baseEntityId);
		
		Multimedia savedMultimedia = multimediaService.findByCaseId(baseEntityId);
		assertEquals(multimediaDTO.getFilePath(), savedMultimedia.getFilePath());
		assertEquals(file.getAbsolutePath(), savedMultimedia.getFilePath());
		assertEquals(multimediaDTO.getContentType(), savedMultimedia.getContentType());
		assertEquals(multimediaDTO.getFileCategory(), savedMultimedia.getFileCategory());
		
		assertEquals(5, multimediaService.getMultimediaFiles("biddemo").size());
		assertEquals(6, multimediaRepository.getAll().size());
		
		assertEquals(baseEntityId + ".jpg", client.getAttribute("Patient Image"));
		Assert.assertEquals(0, Minutes.minutesBetween(client.getDateEdited(), DateTime.now()).getMinutes());
		
	}
	
}
