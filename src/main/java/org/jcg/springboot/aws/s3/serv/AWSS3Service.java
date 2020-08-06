package org.jcg.springboot.aws.s3.serv;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectSummary;



@Service("Service")
public interface AWSS3Service {

	void uploadFile(MultipartFile multipartFile,String event);
	List<byte[]> downloadFile(String prefix);
	List<S3ObjectSummary> getBucketObjectSummaries(String bucketName);
	List<String> getBucketObjectNames(String bucketName);
}
