package kafka;

import com.app.kafka.message.ImageDeletionMessage;
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
class KafkaImageConsumerTest {

	@Value("${image.dir}")
	private String testImagePath;
	
	@Autowired
	private KafkaTemplate<String, ImageDeletionMessage> kafkaTemplate;
	
	@BeforeAll
	@SneakyThrows
	void configFile() {
			File file = ResourceUtils.getFile("classpath:testImage");
			
			Files.copy(file.toPath(), Paths.get(testImagePath, "testImage"), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	void consume_deletion_of_track() {
		kafkaTemplate.send("trackDeletionTopic", new ImageDeletionMessage("testImage"))
		.thenRun(() -> assertThat(Paths.get(testImagePath, "testImage")).doesNotExist());
	}
	
	
	@AfterAll
	@SneakyThrows
	void deleteFile() {
		Files.deleteIfExists(Paths.get(testImagePath, "testImage"));
	}

}
