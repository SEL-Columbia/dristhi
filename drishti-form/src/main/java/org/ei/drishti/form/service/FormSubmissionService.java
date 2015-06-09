package org.ei.drishti.form.service;

import ch.lambdaj.function.convert.Converter;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	@Autowired
	public FormSubmissionService(AllFormSubmissions allFormSubmissions) {
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

	public List<FormSubmission> getNewSubmissionsForANM(String anmIdentifier,
			Long version, Integer batchSize) {
		return allFormSubmissions.findByANMIDAndServerVersion(anmIdentifier,
				version, batchSize);
	}

	/**
	 * @author Suneel.S
	 * 
	 * @param entityid
	 * @param entityEcId
	 * @param anmid
	 * @param formName
	 * 
	 * 
	 *            Description: Test method to save isConsultation = true for
	 *            ANC, PNC and Child visits
	 */

	public void requestConsultationTest(String visitentityid, String entityidEC,
			String anmid, String visittype) {
		PreparedStatement pst = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {

			logger.debug("connection creation failed");
			e.printStackTrace();
		}
		Connection con = null;
		try {
			con = DriverManager.getConnection(
					"jdbc:postgresql://http://192.168.90.151:5432/drishti",
					"postgres", "password");

			String phcName = "select name from report.dim_phc where id=(select phc from report.dim_anm where anmidentifier = '"+anmid+"')";
			PreparedStatement phcNameQuery = con.prepareStatement(phcName);
			ResultSet resultSet = phcNameQuery.executeQuery();
			while (resultSet.next()) {
				String phc_name = resultSet.getString("name");
			
				String stm = "insert into report.poc_table VALUES(?,?,?,?,?,?)";
				String defaultLevel = "level1";
				pst = con.prepareStatement(stm);
				pst.setInt(1, 5);
				pst.setString(2, visitentityid);
				pst.setString(3, entityidEC);
				pst.setString(4, anmid);
				pst.setString(5, defaultLevel);
				pst.setInt(6,2);
				pst.setString(7," ");
				pst.setString(8," ");
				pst.setString(9, visittype);
				pst.setString(10,phc_name);
				
				
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			logger.info("Record not inserted");
			e.printStackTrace();

		}
		if (con != null) {
			logger.info("You made it, take control your database now!");
		} else {
			logger.debug("Failed to make connection!");
		}

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
