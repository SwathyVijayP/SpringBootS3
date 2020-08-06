package org.jcg.springboot.aws.s3.serv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;


@Service("Service")
public class AWSS3ServiceImpl extends AmazonS3Client  implements AWSS3Service  {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

	@Autowired
	private AmazonS3 amazonS3;
	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	
	public AWSS3ServiceImpl(){
         super(new EnvironmentVariableCredentialsProvider());
    }

    public AWSS3ServiceImpl(AWSCredentialsProvider awsCredentialsProvider){
        super(awsCredentialsProvider);
    }


	@Override
	// @Async annotation ensures that the method is executed in a different background thread 
	// but not consume the main thread.
	@Async
	public void uploadFile(final MultipartFile multipartFile,final String event) {
		LOGGER.info("File upload in progress.");
		try {
			final File file = convertMultiPartFileToFile(multipartFile);
			uploadFileToS3Bucket(bucketName, file,event);
			LOGGER.info("File upload is completed.");
			file.delete();	// To remove the file locally created in the project folder.
		} catch (final AmazonServiceException ex) {
			LOGGER.info("File upload is failed.");
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
		}
	}

	private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(multipartFile.getOriginalFilename());
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (final IOException ex) {
			LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
		}
		return file;
	}

	private void uploadFileToS3Bucket(final String bucketName, final File file, final String event) {
		final String uniqueFileName =event+"_"+file.getName();
		LOGGER.info("Uploading file with name= " + uniqueFileName);
		final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
		amazonS3.putObject(putObjectRequest);
	}

	@Override
	// @Async annotation ensures that the method is executed in a different background thread 
	// but not consume the main thread.
	@Async
	public List<byte[]> downloadFile(final String prefix) {
		LOGGER.info("Downloading an object with given prefix");
		byte[] content = null;
		List<byte[]> contentlist=new ArrayList<byte[]>();

		List<String> names=this.getBucketObjectNames(bucketName);

		List<String> finalnames=new ArrayList();

		for (String ss : names) {

			if(ss.contains(prefix)) {

				finalnames.add(ss);

			}}

		for (String tt : finalnames) {

			S3Object s3Object = amazonS3.getObject(bucketName, tt);

			S3ObjectInputStream stream = s3Object.getObjectContent();

			try {

				content = IOUtils.toByteArray(stream);

				contentlist.add(content);

				LOGGER.info("File downloaded successfully.");
				s3Object.close();

			} catch(final IOException ex) {
				LOGGER.info("IO Error Message= " + ex.getMessage());
			}}
			
		return contentlist;
		
	}
		
	@Async
	@Override
	public List<S3ObjectSummary> getBucketObjectSummaries(String bucketName){

        List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<S3ObjectSummary>();

        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
            
            ObjectListing objectListing;

            do {

                objectListing = amazonS3.listObjects(listObjectsRequest);

                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

                    s3ObjectSummaries.add(objectSummary);

                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());

            } while (objectListing.isTruncated());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon BdS3Client, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with BdS3Client, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return s3ObjectSummaries;
    }
	
	
@Async
@Override
    public List<String> getBucketObjectNames(String bucketName){
        List<String> s3ObjectNames = new ArrayList<String>();
        List<S3ObjectSummary> s3ObjectSummaries = getBucketObjectSummaries(bucketName);

        for(S3ObjectSummary s3ObjectSummary : s3ObjectSummaries){

            s3ObjectNames.add(s3ObjectSummary.getKey());

        }
        return s3ObjectNames;
    }


}