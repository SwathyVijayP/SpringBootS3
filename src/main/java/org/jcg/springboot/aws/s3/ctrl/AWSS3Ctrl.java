package org.jcg.springboot.aws.s3.ctrl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


import org.jcg.springboot.aws.s3.serv.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value= "/s3")
public class AWSS3Ctrl {

	@Autowired
	private AWSS3Service service;

	@PostMapping(value= "/upload")
	public ResponseEntity<String> uploadFile(@RequestPart(value= "file") final MultipartFile multipartFile,@RequestPart(value= "event") final String event) {
		service.uploadFile(multipartFile,event);
		final String response = "[" + multipartFile.getOriginalFilename() + "] uploaded successfully.";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

@GetMapping(value= "/download")
	public ResponseEntity<List<String>> downloadFile(@RequestParam(value= "fileName") final String keyName) {
		final List<byte[]> data = service.downloadFile(keyName);

		System.out.println(data);
		List<String> output=new ArrayList<String>();
		for (byte[] bs : data) {
			String base64String=Base64.getEncoder().encodeToString(bs);
			output.add(base64String);
		}
		
		return new ResponseEntity<>(output,HttpStatus.OK);
	}
}
