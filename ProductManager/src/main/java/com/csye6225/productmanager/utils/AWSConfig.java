package com.csye6225.productmanager.utils;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSConfig {

    // create an aws-s3 client
    public static AmazonS3 awss3Client() {
//        S3Client s3 = S3Client.builder()
//                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
//                .build();
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_1)
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
    }


}
