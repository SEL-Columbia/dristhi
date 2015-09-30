package org.ei.drishti.form.service;

import ch.lambdaj.function.convert.Converter;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.domain.Poc_table;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.ei.drishti.form.repository.AllPoc_tableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static java.util.Collections.sort;

@Service
public class FormSubmissionService {
	private static Logger logger = LoggerFactory
			.getLogger(FormSubmissionService.class.toString());
	private AllFormSubmissions allFormSubmissions;
	//private AllPoc_tableRepository allPoc_tableRepository;

	@Autowired
	public FormSubmissionService(AllFormSubmissions allFormSubmissions
			) {
		this.allFormSubmissions = allFormSubmissions;
		
	}

	public List<FormSubmissionDTO> fetch(long formFetchToken) {
		return with(allFormSubmissions.findByServerVersion(formFetchToken))
				.convert(new Converter<FormSubmission, FormSubmissionDTO>() {
					@Override
					public FormSubmissionDTO convert(FormSubmission submission) {
						return FormSubmissionConverter.from(submission);
					}
				});
	}

//	public List<FormSubmission> getNewSubmissionsForANM(String anmIdentifier,
//			Long version, Integer batchSize) {
//		return allFormSubmissions.findByANMIDAndServerVersion(anmIdentifier,
//				version, batchSize);
//	}
	
	public List<FormSubmission> getNewSubmissionsForANM(String village,
			Long version, Integer batchSize) {
		logger.info("******getnew submissios******");
		return allFormSubmissions.findByVillageAndServerVersion(village,
				version, batchSize);
	}

	/**
	 * @author Suneel.S
	 * 
	 * @param entityidEC
	 * @param visittype
	 * @param anmid
	 * @param visitentityid
	 * 
	 * 
	 *            Description: Test method to save isConsultation = true for
	 *            ANC, PNC and Child visits
	 */

	public void requestConsultationTest(String visittype, String visitentityid,
			String entityidEC, String anmid) {
//		PreparedStatement pst = null;
//		String subcenter = null;
//		String parenthosp = null;
//		
//
//		try {
//			Class.forName("org.postgresql.Driver");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		Connection con = null;
//		try {
//			String url = "jdbc:postgresql://localhost:5432/drishti";
//			con = DriverManager.getConnection(url, "postgres", "password");
//
//			java.util.Date date = new java.util.Date();
//			Timestamp timestamp = new Timestamp(date.getTime());
//
//			String hospitalname="select parent_hospital from report.health_centers where hospital_name=(select subcenter from report.user_masters where user_id='"+anmid+"') and hospital_type='Subcenter'";
//			PreparedStatement hosNamestmt = con.prepareStatement(hospitalname);
//			
//			ResultSet hosresultSet = hosNamestmt.executeQuery();
//			
//			while (hosresultSet.next()) {
//				parenthosp = hosresultSet.getString("parent_hospital");
//				logger.info("*******parent_hospitalphc is *****"+parenthosp);
//			}
//				
//		
//			String stm = "insert into report.poc_table(visitentityid,entityidec,anmid,level,clientversion,serverversion,visittype,phc,timestamp) VALUES(?,?,?,?,?,?,?,?,?)";
//			String defaultLevel = "1";
//			pst = con.prepareStatement(stm);
//			pst.setString(1, visitentityid);
//			pst.setString(2, entityidEC);
//			pst.setString(3, anmid);
//			pst.setString(4, defaultLevel);
//			pst.setString(5, " ");
//			pst.setString(6, " ");
//			pst.setString(7, visittype);
//			pst.setString(8, parenthosp);
//			pst.setTimestamp(9, timestamp);
//			//pst.executeUpdate();
//		} catch (SQLException e) {
//			logger.info("Record not inserted");
//			e.printStackTrace();
//		}
//		if (con != null) {
//			logger.info("You made it, take control your database now!");
//		} else {
//			logger.debug("Failed to make connection!");
//		}
	}

	public List<FormSubmission> getAllSubmissions(Long version,
			Integer batchSize) {
		return allFormSubmissions.allFormSubmissions(version, batchSize);
	}

	public void submit(List<FormSubmissionDTO> formSubmissionsDTO) {
		List<FormSubmission> formSubmissions = with(formSubmissionsDTO)
				.convert(new Converter<FormSubmissionDTO, FormSubmission>() {
					@Override
					public FormSubmission convert(FormSubmissionDTO submission) {
						return FormSubmissionConverter
								.toFormSubmission(submission);
					}
				});

		sort(formSubmissions, timeStampComparator());
		for (FormSubmission submission : formSubmissions) {
			if (allFormSubmissions.exists(submission.instanceId())) {
				logger.warn(format(
						"Received form submission that already exists. Skipping. Submission: {0}",
						submission));
				continue;
			}
			logger.info(format(
					"Saving form {0} with instance Id: {1} and for entity Id: {2}",
					submission.formName(), submission.instanceId(),
					submission.entityId()));
			submission.setServerVersion(DateUtil.millis());
			allFormSubmissions.add(submission);
		}
	}

	private Comparator<FormSubmission> timeStampComparator() {
		return new Comparator<FormSubmission>() {
			public int compare(FormSubmission firstSubmission,
					FormSubmission secondSubmission) {
				long firstTimestamp = firstSubmission.clientVersion();
				long secondTimestamp = secondSubmission.clientVersion();
				return firstTimestamp == secondTimestamp ? 0
						: firstTimestamp < secondTimestamp ? -1 : 1;
			}
		};
	}
}
