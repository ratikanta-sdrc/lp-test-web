package org.sdrc.lactation.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.lactation.service.DataDumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Naseem Akhtar (naseem@sdrc.co.in) on 5th March 2018 20:23
 * 
 * This service will be used to handle the api that we are using to dump the DB data in excel file
 * and make it downloadable.
 *
 */

@RestController
public class DataDumpController {
	
	@Autowired
	private DataDumpService dataDumpService;
	
	@CrossOrigin
	@RequestMapping(value = "/downloadFile", method=RequestMethod.GET)
	public void downLoad(HttpServletResponse response) throws IOException {
		String fileName = dataDumpService.exportDataToExcel();
		
		try(InputStream inputStream = new FileInputStream(fileName)) {
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); //for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			File file = new File(fileName);
			if(file.delete())
				System.out.println("file delete succcess");
			else
				System.out.println("file delete failed");
		}
	}


}