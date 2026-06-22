package com.judtih.judith_management_system.global.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Configures AWS SDK clients (SNS for SMS, S3 for file storage) using static credentials from application properties.
 * SNS uses Tokyo (ap-northeast-1) while S3 uses Seoul (ap-northeast-2) — note the separate region properties.
 */
@Configuration
public class AwsConfig {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.SnsRegion}")
    private String SnsRegion; // capital S and R — must match the property key exactly in all profiles

    @Value("${aws.defaultRegion}")
    private String defaultRegion;


    /** SNS client for direct-to-phone SMS delivery; uses StaticCredentialsProvider (properties-based credentials). */
    @Bean
    public SnsClient snsClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return SnsClient.builder()
                .region(Region.of(SnsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

    }

    /** S3 client for file uploads; uses Seoul (ap-northeast-2) — different region from the SNS client. */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return S3Client.builder()
                .region(Region.of(defaultRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }



}
