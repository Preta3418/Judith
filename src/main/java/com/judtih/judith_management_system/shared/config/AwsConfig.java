package com.judtih.judith_management_system.shared.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsConfig {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;


    /**
     * 이해한걸 간한하게 정리해보자
     * SnsClient 는 간단하게 말하면, 우리가 api 연결을 위해 쓰일 중간 다리. (RestClient 랑 동일한 방식)
     * AwsBasicCredential 은 엑세스/시크릿 엑세스 키를 들 수 있는 컨테이너
     * builder 로 SnsClient 에 엑세스키와 지역을 넣고 만들어주면 RestClient 랑 거의 동일하게 사용 가능

     * StaticCredentialsProvide 는 중간 레이어인데, 이 중간 레이어가 꽤 많음
     * Static , Default, EnvironmentalVariable, Profile, Instance ....
     * 여기서 알아야 할건 Static 은 properties 를 사용하기 때문에 (사실상 하드코드 되었기 때문에) 쓰는거고
     * 나중에 환경변수로 쓰거나 EC2 인스턴스 사용, 또는 ~/.aws/credentials 폴더를 활용하면 Default 사용하면 된다.
     * 이 경우 AwsBasicCredentials 를 만들 필요도 없음, 자동으로 찾아서 만들어줌.
     */
    @Bean
    public SnsClient snsClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

    }

}
