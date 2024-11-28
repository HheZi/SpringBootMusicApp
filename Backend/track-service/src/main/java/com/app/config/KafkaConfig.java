package com.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
	
	@Bean
	NewTopic deleteTrackTopic(@Value("${kafka.topic.name}") String topicName) {
		return TopicBuilder.name(topicName)
				.partitions(10)
				.replicas(1)
				.build();
	}
	
}
