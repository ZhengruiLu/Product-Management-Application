package com.csye6225.productmanager.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import org.springframework.beans.factory.annotation.Value;

public class S3Service {
    @Value("${amazonProperties.iamrolearn}")
    private static String iamRoleArn;

    public static AWSCredentialsProvider buildCredentialsProvider() {
        // Build credentials using IAM role
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(InstanceProfileCredentialsProvider.getInstance())
                .build();
        AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
                .withRoleArn(iamRoleArn)
                .withRoleSessionName("MySession");
        STSAssumeRoleSessionCredentialsProvider credentialsProvider =
                new STSAssumeRoleSessionCredentialsProvider.Builder(iamRoleArn, "MySession")
                        .withStsClient(stsClient)
                        .build();
        return credentialsProvider;
    }


}