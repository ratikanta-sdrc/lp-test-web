package org.sdrc.lactation.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.lactation.domain.LogBreastFeedingPostDischarge;
import org.sdrc.lactation.domain.LogBreastFeedingSupportivePractice;
import org.sdrc.lactation.domain.LogExpressionBreastFeed;
import org.sdrc.lactation.domain.LogFeed;
import org.sdrc.lactation.domain.Patient;
import org.sdrc.lactation.domain.TypeDetails;
import org.sdrc.lactation.repository.LogBreastFeedingPostDischargeRepository;
import org.sdrc.lactation.repository.LogBreastFeedingSupportivePracticeRepository;
import org.sdrc.lactation.repository.LogExpressionBreastFeedRepository;
import org.sdrc.lactation.repository.LogFeedRepository;
import org.sdrc.lactation.repository.PatientRepository;
import org.sdrc.lactation.repository.TypeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Naseem Akhtar (naseem@sdrc.co.in) on 5th April 2018 17:00
 * 
 * This service class wil handle all the requests related to data dump.
 *
 */

@Service
public class DataDumpServiceImpl implements DataDumpService {

	private static final Logger log = LogManager.getLogger(DataDumpServiceImpl.class);
	
	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private LogExpressionBreastFeedRepository logExpressionBreastFeedRepository;

	@Autowired
	private LogBreastFeedingPostDischargeRepository logBreastFeedingPostDischargeRepository;

	@Autowired
	private LogFeedRepository logFeedRepository;

	@Autowired
	private LogBreastFeedingSupportivePracticeRepository logBreastFeedingSupportivePracticeRepository;
	
	@Autowired
	private TypeDetailsRepository typeDetailsRepository;

	private SimpleDateFormat sdfDateInteger = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
	
	private SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat sdfDateTimeWithSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	@Transactional(readOnly = true)
	public String exportDataInExcel(HttpServletRequest request, HttpServletResponse response) {
		
		String filePath;

		if(validateUserForExportApi(request, response)) {
			
			filePath = "/opt/lactation/data_dump/dataDump_" + sdfDateInteger.format(new Date()) + ".xlsx";
			
			try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filePath);) {
				
				Map<Integer, TypeDetails> typeDetailsMap = new HashMap<>();
				typeDetailsRepository.findAll().forEach(typeDetails->typeDetailsMap.put(typeDetails.getId(), typeDetails));
				
				List<Patient> patients = patientRepository.findAll();
				List<LogExpressionBreastFeed> bfExpressions = logExpressionBreastFeedRepository.findAll();
				List<LogBreastFeedingSupportivePractice> bfsps = logBreastFeedingSupportivePracticeRepository.findAll();
				List<LogFeed> feeds = logFeedRepository.findAll();
				List<LogBreastFeedingPostDischarge> bfpds = logBreastFeedingPostDischargeRepository.findAll();
	
				XSSFSheet patientSheet = workbook.createSheet("Patients");
				XSSFSheet bfExpressionsSheet = workbook.createSheet("Bf Expressions");
				XSSFSheet bfspSheet = workbook.createSheet("BFSP");
				XSSFSheet logFeedSheet = workbook.createSheet("Log Feed");
				XSSFSheet bfpdSheet = workbook.createSheet("BFPD");
	
				// for styling the first row of every sheet
				XSSFFont font = workbook.createFont();
				font.setBold(true);
				font.setItalic(false);
	
				CellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setFont(font);
	
				int slNo = 1;
				int rowNum = 0;
				Row headingRow = patientSheet.createRow(rowNum++);
				int headingCol = 0;
	
				headingRow.setRowStyle(cellStyle);
				// setting heading of patient sheet
				headingRow.createCell(headingCol).setCellValue("Sl no.");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Baby Code");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Baby code hospital");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Baby of");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Baby weight");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Baby admitted to");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Admission date for outdoor patients");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Discharge date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Nicu admission reason");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Time till first expression");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("UUID");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Delivery date and time");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Gestational age in weeks");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Mother's age");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Delivert method");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Inpatient / Outpatient");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Mother's prenatal intent");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Parents knowledge on hm and lactation");
				headingCol = 0;
	
				// Iterating patient records and writing in the excel sheet
				for (Patient patient : patients) {
					Row row = patientSheet.createRow(rowNum++);
					int colNum = 0;
	
					row.createCell(colNum).setCellValue(slNo++);
					colNum++;
					row.createCell(colNum).setCellValue(patient.getBabyCode());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getBabyCodeHospital() == null ? "" : patient.getBabyCodeHospital());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getBabyOf() == null ? "" : patient.getBabyOf());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getBabyWeight() == null ? "" : patient.getBabyWeight().toString());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getBabyAdmittedTo() == null ? "" : patient.getBabyAdmittedTo().getName());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getAdmissionDateForOutdoorPatients() ==  null ? "" : sdfDateOnly.format(patient.getAdmissionDateForOutdoorPatients()));
					colNum++;
					row.createCell(colNum).setCellValue(patient.getCreatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getDischargeDate() == null ? "" : sdfDateOnly.format(patient.getDischargeDate()));
					colNum++;
					row.createCell(colNum).setCellValue(patient.getNicuAdmissionReason().length() == 0 ? "" : arrayToString(patient.getNicuAdmissionReason(), typeDetailsMap));
					colNum++;
					row.createCell(colNum).setCellValue(patient.getTimeTillFirstExpression() == null ? "" : patient.getTimeTillFirstExpression());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getUpdatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getUuidNumber() == null ? "" : patient.getUuidNumber());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(patient.getCreatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(patient.getDeliveryDateAndTime()));
					colNum++;
					row.createCell(colNum).setCellValue(patient.getGestationalAgeInWeek() == null ? "" : patient.getGestationalAgeInWeek().toString());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getMothersAge() == null ? "" : patient.getMothersAge().toString());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(patient.getUpdatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(patient.getDeliveryMethod() == null ? "" : patient.getDeliveryMethod().getName());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getInpatientOrOutPatient() == null ? "" : patient.getInpatientOrOutPatient().getName());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getMothersPrenatalIntent() == null ? "" : patient.getMothersPrenatalIntent().getName());
					colNum++;
					row.createCell(colNum).setCellValue(patient.getParentsKnowledgeOnHmAndLactation() == null ? "" : patient.getParentsKnowledgeOnHmAndLactation().getName());
				}
	
				rowNum = 0;
				slNo = 1;
				headingRow = bfExpressionsSheet.createRow(rowNum++);
	
				// setting heading of breastfeed expression sheet
				headingRow.createCell(headingCol).setCellValue("Sl no.");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Unique form id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("UUID");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Date and time of expression");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Milk expressed from left and right breast");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Location where expression occured");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Method of expression");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Patient id");
				headingCol = 0;
	
				// iterating through breastfeed expressions
				for (LogExpressionBreastFeed bfExpression : bfExpressions) {
					Row row = bfExpressionsSheet.createRow(rowNum++);
					int colNum = 0;
	
					row.createCell(colNum).setCellValue(slNo++);
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getCreatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getUniqueFormId());
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getUpdatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getUuidNumber() == null ? "" : bfExpression.getUuidNumber());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfExpression.getCreatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfExpression.getDateAndTimeOfExpression()));
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getMilkExpressedFromLeftAndRightBreast() == null ? "" : bfExpression.getMilkExpressedFromLeftAndRightBreast().toString());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfExpression.getUpdatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getExpressionOccuredLocation() == null ? "" : bfExpression.getExpressionOccuredLocation().getName());
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getMethodOfExpression() == null ? "" : bfExpression.getMethodOfExpression().getName());
					colNum++;
					row.createCell(colNum).setCellValue(bfExpression.getPatientId().getBabyCode());
				}
	
				rowNum = 0;
				slNo = 1;
				headingRow = bfspSheet.createRow(rowNum++);
	
				// setting heading of bfsp sheet
				headingRow.createCell(headingCol).setCellValue("Sl no.");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Unique form id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("UUID");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Bfsp duration");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Date and time of bfsp");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Bfsp performed");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Patient id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Personn who performed BFSP");
				headingCol = 0;
	
				// iterating through bfsp entries and writing them in excel
				for (LogBreastFeedingSupportivePractice bfsp : bfsps) {
					Row row = bfspSheet.createRow(rowNum++);
					int colNum = 0;
	
					row.createCell(colNum).setCellValue(slNo++);
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getCreatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getUniqueFormId());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getUpdatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getUuidNumber() == null ? "" : bfsp.getUuidNumber());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getBfspDuration() == null ? "" : bfsp.getBfspDuration().toString());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfsp.getCreatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfsp.getDateAndTimeOfBFSP()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfsp.getUpdatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getBfspPerformed() == null ? "" : bfsp.getBfspPerformed().getName());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getPatientId().getBabyCode());
					colNum++;
					row.createCell(colNum).setCellValue(bfsp.getPersonWhoPerformedBFSP() == null ? "" : bfsp.getPersonWhoPerformedBFSP().getName());
				}
	
				rowNum = 0;
				slNo = 1;
				headingRow = logFeedSheet.createRow(rowNum++);
	
				// setting heading of feed sheet
				headingRow.createCell(headingCol).setCellValue("Sl no.");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Unique form id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("UUID");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Animal milk volume");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Date and time of feed");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("DHM volume");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Formula volume");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("OMM volume");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Other volume");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Weight of baby");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Feed method");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Location of feeding");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Patient id");
				headingCol = 0;
	
				// iterating through feed entries and writing them in excel
				for (LogFeed feed : feeds) {
					Row row = logFeedSheet.createRow(rowNum++);
					int colNum = 0;
	
					row.createCell(colNum).setCellValue(slNo++);
					colNum++;
					row.createCell(colNum).setCellValue(feed.getCreatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getUniqueFormId());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getUpdatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getUuidNumber() == null ? "" : feed.getUuidNumber());
					colNum++;
					row.createCell(colNum)
							.setCellValue(feed.getAnimalMilkVolume() == null ? "" : feed.getAnimalMilkVolume().toString());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(feed.getCreatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(feed.getDateAndTimeOfFeed()));
					colNum++;
					row.createCell(colNum).setCellValue(feed.getDhmVolume() == null ? "" : feed.getDhmVolume().toString());
					colNum++;
					row.createCell(colNum)
							.setCellValue(feed.getFormulaVolume() == null ? "" : feed.getFormulaVolume().toString());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getOmmVolume() == null ? "" : feed.getOmmVolume().toString());
					colNum++;
					row.createCell(colNum)
							.setCellValue(feed.getOtherVolume() == null ? "" : feed.getOtherVolume().toString());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(feed.getUpdatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(feed.getWeightOfBaby() == null ? "" : feed.getWeightOfBaby().toString());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getFeedMethod() == null ? "" : feed.getFeedMethod().getName());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getLocationOfFeeding() == null ? "" : feed.getLocationOfFeeding().getName());
					colNum++;
					row.createCell(colNum).setCellValue(feed.getPatientId().getBabyCode());
				}
	
				rowNum = 0;
				slNo = 1;
				headingRow = bfpdSheet.createRow(rowNum++);
	
				// setting heading of bfpd sheet
				headingRow.createCell(headingCol).setCellValue("Sl no.");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Unique form id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated by");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("UUID");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Created date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Date of breastfeeding");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Updated date");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Breastfeeding status");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Patient id");
				headingCol++;
				headingRow.createCell(headingCol).setCellValue("Time of breastfeeding");
				headingCol = 0;
	
				// iterating through bfpd entries and writing them in excel
				for (LogBreastFeedingPostDischarge bfpd : bfpds) {
					Row row = bfpdSheet.createRow(rowNum++);
					int colNum = 0;
	
					row.createCell(colNum).setCellValue(slNo++);
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getCreatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getUniqueFormId());
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getUpdatedBy());
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getUuidNumber() == null ? "" : bfpd.getUuidNumber());
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfpd.getCreatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateOnly.format(bfpd.getDateOfBreastFeeding()));
					colNum++;
					row.createCell(colNum).setCellValue(sdfDateTimeWithSeconds.format(bfpd.getUpdatedDate()));
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getBreastFeedingStatus() == null ? "" : bfpd.getBreastFeedingStatus().getName());
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getPatientId().getBabyCode());
					colNum++;
					row.createCell(colNum).setCellValue(bfpd.getTimeOfBreastFeeding().getName());
				}
	
				workbook.write(fileOut);
			} catch (Exception e) {
				log.error("Error - DataDumpServiceImpl - exportDataInExcel - " + e.getMessage());
			}
		}else{
			filePath = null;
		}

		return filePath;
	}
	
	/***
	 *@author Naseem Akhtar (naseem@sdrc.co.in) on 7th April 2018 1641.
	 *
	 * This method will accpet the request for data dump in json format.
	 * After validating the request the data is being extracted and then returned.
	 * 
	 * @return {@link JSONObject}
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public JSONObject exportDataInJson(HttpServletRequest request, HttpServletResponse response) {
		
		//JSONObject which will be returned at last.
		JSONObject data = new JSONObject();
		
		//validating the user
		if(validateUserForExportApi(request, response)){
			try{
				//map to keep the type details table data.
				Map<Integer, TypeDetails> typeDetailsMap = new HashMap<>();
				typeDetailsRepository.findAll().forEach(typeDetails->typeDetailsMap.put(typeDetails.getId(), typeDetails));
				
				//querying multiple tables as per requirement.
				List<Patient> patients = patientRepository.findAll();
				List<LogExpressionBreastFeed> bfExpressions = logExpressionBreastFeedRepository.findAll();
				List<LogBreastFeedingSupportivePractice> bfsps = logBreastFeedingSupportivePracticeRepository.findAll();
				List<LogFeed> feeds = logFeedRepository.findAll();
				List<LogBreastFeedingPostDischarge> bfpds = logBreastFeedingPostDischargeRepository.findAll();
				
				//declaring some of the common JSON keys as final.
				final String babyCode = "babyCode";
				final String createdBy = "createdBy";
				final String updatedBy = "updatedBy";
				final String createdDate = "createdDate";
				final String updatedDate = "updatedDate";
				final String uniqueFormId = "uniqueFormId";
				
				//creating a JSONArray to store the patient entries received through the query call
				JSONArray patientList = new JSONArray();
				patients.forEach(d -> {
					JSONObject patient = new JSONObject();
					patient.put(babyCode, d.getBabyCode());
					patient.put("babyCodeHospital", d.getBabyCodeHospital() == null ? null : d.getBabyCodeHospital());
					patient.put("babyOf", d.getBabyOf() == null ? null : d.getBabyOf());
					patient.put("babyWeight", d.getBabyWeight() == null ? null : d.getBabyWeight().toString());
					patient.put("babyAdmittedTo", d.getBabyAdmittedTo() == null ? null : d.getBabyAdmittedTo().getName());
					patient.put("admissionDateForOutdoorPatients", d.getAdmissionDateForOutdoorPatients() ==  null ? null : sdfDateOnly.format(d.getAdmissionDateForOutdoorPatients()));
					patient.put(createdBy, d.getCreatedBy());
					patient.put("dischargeDate", d.getDischargeDate() == null ? null : sdfDateOnly.format(d.getDischargeDate()));
					patient.put("nicuAdmissionReason", d.getNicuAdmissionReason() == null ? null : arrayToString(d.getNicuAdmissionReason(), typeDetailsMap));
					patient.put("timeTillFirstExpression", d.getTimeTillFirstExpression() == null ? null : d.getTimeTillFirstExpression());
					patient.put(updatedBy, d.getUpdatedBy());
					patient.put("uuid", d.getUuidNumber() == null ? null : d.getUuidNumber());
					patient.put(createdDate, sdfDateTimeWithSeconds.format(d.getCreatedDate()));
					patient.put("deliveryDateAndTime", sdfDateTimeWithSeconds.format(d.getDeliveryDateAndTime()));
					patient.put("gestationAgeInWeek", d.getGestationalAgeInWeek() == null ? null : d.getGestationalAgeInWeek().toString());
					patient.put("mothersAge", d.getMothersAge() == null ? null : d.getMothersAge().toString());
					patient.put(updatedDate, sdfDateTimeWithSeconds.format(d.getUpdatedDate()));
					patient.put("deliveryMethod", d.getDeliveryMethod() == null ? null : d.getDeliveryMethod().getName());
					patient.put("inpatientOrOutPatient", d.getInpatientOrOutPatient() == null ? null : d.getInpatientOrOutPatient().getName());
					patient.put("mothersPrenatalIntent", d.getMothersPrenatalIntent() == null ? null : d.getMothersPrenatalIntent().getName());
					patient.put("parentsKnowledgeOnHmAndLactation", d.getParentsKnowledgeOnHmAndLactation() == null ? null : d.getParentsKnowledgeOnHmAndLactation().getName());
					
					patientList.add(patient);
				});
				
				//iterating through log expression breastfeed entries and storing them in bfExpressionList
				JSONArray bfExpressionList = new JSONArray();
				bfExpressions.forEach(d -> {
					JSONObject bfExp = new JSONObject();
					bfExp.put(createdBy, d.getCreatedBy());
					bfExp.put(uniqueFormId, d.getUniqueFormId());
					bfExp.put(updatedBy, d.getUpdatedBy());
					bfExp.put("uuid", d.getUuidNumber() == null ? null : d.getUuidNumber());
					bfExp.put(createdDate, sdfDateTimeWithSeconds.format(d.getCreatedDate()));
					bfExp.put("dateAndTimeOfExpression", sdfDateTimeWithSeconds.format(d.getDateAndTimeOfExpression()));
					bfExp.put("milkExpressedFromLeftAndRightBreast", d.getMilkExpressedFromLeftAndRightBreast() == null ? null : d.getMilkExpressedFromLeftAndRightBreast().toString());
					bfExp.put(updatedDate, sdfDateTimeWithSeconds.format(d.getUpdatedDate()));
					bfExp.put("locationWhereExpressionOccured", d.getExpressionOccuredLocation() == null ? null : d.getExpressionOccuredLocation().getName());
					bfExp.put("methodOfExpression", d.getMethodOfExpression() == null ? null : d.getMethodOfExpression().getName());
					bfExp.put(babyCode, d.getPatientId().getBabyCode());
					
					bfExpressionList.add(bfExp);
				});
				
				//iterating through breast feeding supportive practice entries and storing them in bfspList
				JSONArray bfspList = new JSONArray();
				bfsps.forEach(d -> {
					JSONObject bfsp = new JSONObject();
					bfsp.put(createdBy, d.getCreatedBy());
					bfsp.put(uniqueFormId, d.getUniqueFormId());
					bfsp.put(updatedBy, d.getUpdatedBy());
					bfsp.put("uuid", d.getUuidNumber() == null ? null : d.getUuidNumber());
					bfsp.put("bfspDuration", d.getBfspDuration() == null ? null : d.getBfspDuration().toString());
					bfsp.put(createdDate, sdfDateTimeWithSeconds.format(d.getCreatedDate()));
					bfsp.put("dateAndTimeOfBfsp", sdfDateTimeWithSeconds.format(d.getDateAndTimeOfBFSP()));
					bfsp.put(updatedDate, sdfDateTimeWithSeconds.format(d.getUpdatedDate()));
					bfsp.put("bfspPerformed", d.getBfspPerformed() == null ? null : d.getBfspPerformed().getName());
					bfsp.put(babyCode, d.getPatientId().getBabyCode());
					bfsp.put("personWhoPerformedBfsp", d.getPersonWhoPerformedBFSP() == null ? null : d.getPersonWhoPerformedBFSP().getName());
					
					bfspList.add(bfsp);
				});
				
				//iterating through feed entries and storing them in feedList
				JSONArray feedList = new JSONArray();
				feeds.forEach(d -> {
					JSONObject feed = new JSONObject();
					feed.put(createdBy, d.getCreatedBy());
					feed.put(uniqueFormId, d.getUniqueFormId());
					feed.put(updatedBy, d.getUpdatedBy());
					feed.put("uuid", d.getUuidNumber() == null ? null : d.getUuidNumber());
					feed.put("animalMilkVolume", d.getAnimalMilkVolume() == null ? null : d.getAnimalMilkVolume().toString());
					feed.put(createdDate, sdfDateTimeWithSeconds.format(d.getCreatedDate()));
					feed.put("dateAndTimeOfFeed", sdfDateTimeWithSeconds.format(d.getDateAndTimeOfFeed()));
					feed.put("dhmVolume", d.getDhmVolume() == null ? null : d.getDhmVolume().toString());
					feed.put("formulaVolume", d.getFormulaVolume() == null ? null : d.getFormulaVolume().toString());
					feed.put("ommVolume", d.getOmmVolume() == null ? null : d.getOmmVolume().toString());
					feed.put("otherVolume", d.getOtherVolume() == null ? null : d.getOtherVolume().toString());
					feed.put(updatedDate, sdfDateTimeWithSeconds.format(d.getUpdatedDate()));
					feed.put("babyWeight", d.getWeightOfBaby() == null ? null : d.getWeightOfBaby().toString());
					feed.put("methodOfFeed", d.getFeedMethod() == null ? null : d.getFeedMethod().getName());
					feed.put("loactionOfFeeding", d.getLocationOfFeeding() == null ? null : d.getLocationOfFeeding().getName());
					feed.put(babyCode, d.getPatientId().getBabyCode());
					
					feedList.add(feed);
				});
				
				//iterating through breast feeding post discharge entries and storing them in bfpdList
				JSONArray bfpdList = new JSONArray();
				bfpds.forEach(d -> {
					JSONObject bfpd = new JSONObject();
					bfpd.put(createdBy, d.getCreatedBy());
					bfpd.put(uniqueFormId, d.getUniqueFormId());
					bfpd.put(updatedBy, d.getUpdatedBy());
					bfpd.put("uuid", d.getUuidNumber() == null ? null : d.getUuidNumber());
					bfpd.put(createdDate, sdfDateTimeWithSeconds.format(d.getCreatedDate()));
					bfpd.put("dateOfBreastFeeding", sdfDateOnly.format(d.getDateOfBreastFeeding()));
					bfpd.put(updatedDate, sdfDateTimeWithSeconds.format(d.getUpdatedDate()));
					bfpd.put("breastFeedingStatus", d.getBreastFeedingStatus() == null ? null : d.getBreastFeedingStatus().getName());
					bfpd.put(babyCode, d.getPatientId().getBabyCode());
					bfpd.put("timeOfBreastFeeding", d.getTimeOfBreastFeeding().getName());
					
					bfpdList.add(bfpd);
				});
				
				//finally assigning all the list to data object.
				data.put("patients", patientList);
				data.put("breastFeedExpressions", bfExpressionList);
				data.put("bfsps", bfspList);
				data.put("feeds", feedList);
				data.put("bfpds", bfpdList);
				
			}catch (Exception e) {
				log.error("Error - DataDumpServiceImpl - exportDataInJson - " + e.getMessage());
			}
		}else{
			//if user is not valid then send null
			data = null;
		}
		
		return data;
	}
	
	/**
	 * @author Naseem Akhtar (naseem@sdrc.co.in) on 7th April 2018 16:31
	 * 
	 * This method will be used to validate the user who has sent request for data dump (excel/json).
	 * 
	 * @param request - will be retrieving the encrypted username and password from the header.
	 * @return {@link Boolean}
	 */
	private Boolean validateUserForExportApi(HttpServletRequest request, HttpServletResponse response) {
		
		//initializing this variable to send the status of the user which has sent request.
		Boolean validUser = false;
		Map<String, String> map = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		
		//iterating through header names
		while(headerNames.hasMoreElements()){
			String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
		}
		
		
		if(map.get("username") != null && map.get("password") != null){
			//decoding the username and password
			String username = new String(Base64.getDecoder().decode(map.get("username")));
			String password = new String(Base64.getDecoder().decode(map.get("password")));
			
			//DB call to be made here
			if(username.equals("lactation@medella.co.in") && password.equals("la@123#!"))
				validUser = true;
			else
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}else{
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
		}
		
		return validUser;
		
	}
	
	
	/**
	 * @author Naseem Akhtar (naseem@sdrc.co.in) on 6th April 2018 13:08
	 * 
	 * This method will receive NICU admission reason id in string and a map with admission reasons 
	 * NICU Admission reason id will be extracted from the string and passed to the type detail map to extract
	 * the actual reason and append it on a string.
	 * 
	 * @param admissionReason
	 * @param typeDetailsMap
	 * @return {@link String}
	 */
	private String arrayToString(String admissionReason, Map<Integer, TypeDetails> typeDetailsMap){
		String[] nicuAdmissionReasons = admissionReason.split(",");
		StringBuilder reasonNameList = new StringBuilder();
		for(String reason : nicuAdmissionReasons){
			reasonNameList.append(typeDetailsMap.get(Integer.parseInt(reason)).getName() + ",");
		}
		return reasonNameList.substring(0, reasonNameList.length() - 1);
	}

	

}
