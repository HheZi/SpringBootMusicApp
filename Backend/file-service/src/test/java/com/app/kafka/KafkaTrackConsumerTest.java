package com.app.kafka;

import com.app.kafka.message.TrackDeletionMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KafkaAutoConfiguration.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestInstance(Lifecycle.PER_CLASS)
class KafkaTrackConsumerTest {

	@Value("${audio.dir}")
	private String testAudioPath;
	
	@Autowired
	private KafkaTemplate<String, TrackDeletionMessage> kafkaTemplate;

	private final String NAME_OF_TEST_FILE = "file";
	
	@BeforeAll
	@SneakyThrows
	void configFile() {
			File file = ResourceUtils.getFile("classpath:" + NAME_OF_TEST_FILE);
			
			Files.copy(file.toPath(), Paths.get(testAudioPath, NAME_OF_TEST_FILE), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	void consume_deletion_of_track() {
		kafkaTemplate.send("trackDeletionTopic", new TrackDeletionMessage(1L,NAME_OF_TEST_FILE))
		.thenRun(() -> assertThat(Paths.get(testAudioPath, NAME_OF_TEST_FILE)).doesNotExist());
	}
	
	
	@AfterAll
	@SneakyThrows
	void deleteFile() {
		Files.deleteIfExists(Paths.get(testAudioPath, NAME_OF_TEST_FILE));
	}
}
