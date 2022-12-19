package com.finalproject.user.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class SNSConfig {
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.access-key}")
    private String awsAccessKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    @Value("${aws.sns-endpoint}")
    private String snsEndpoint;

    @Value("${aws.sns-topic.name}")
    private String topicName;

    @Bean
    @Primary
    public AmazonSNSClient amazonSNSClient(){
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey,awsSecretKey);
        return (AmazonSNSClient) AmazonSNSClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate() {
        NotificationMessagingTemplate notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSNSClient());
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setSerializedPayloadClass(String.class);
        mappingJackson2MessageConverter.getObjectMapper().registerModule(new JavaTimeModule());
        notificationMessagingTemplate.setMessageConverter(mappingJackson2MessageConverter);
        notificationMessagingTemplate.setDefaultDestinationName(topicName);
        return notificationMessagingTemplate;
    }

}
