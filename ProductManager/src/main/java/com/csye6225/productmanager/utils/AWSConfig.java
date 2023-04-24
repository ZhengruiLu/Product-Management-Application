package com.csye6225.productmanager.utils;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AWSConfig {

    // create an aws-s3 client
    public static AmazonS3 awss3Client() {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_1).withCredentials(new InstanceProfileCredentialsProvider(false)).build();
    }
}
