package com.app.controller;

import com.app.exception.FileValidationException;
import com.app.kafka.KafkaImageProducer;
import com.app.model.Author;
import com.app.payload.response.AuthorResponse;
import com.app.service.ImageClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@AutoConfigureWebTestClient
class AuthorControllerTest {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private ImageClient client;

    @MockBean
    private KafkaImageProducer kafkaImageProducer;

    @Test
    void test_get_author() {
        String nameOfAuthor = "First";

        testClient.get()
                .uri("/api/authors/" + 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo(nameOfAuthor);
    }

    @Test
    void test_get_author_but_not_found() {
        testClient.get()
                .uri("/api/authors/" + 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void test_get_authors() {
        testClient.get()
                .uri(t -> t.path("/api/authors/").queryParam("ids", 1, 2).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorResponse.class)
                .hasSize(2);
    }

    @Test
    void test_get_authors_but_without_ids_params() {
        testClient.get()
                .uri(t -> t.path("/api/authors/").build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void test_find_author_by_start_symbol() {
        String nameOfAuthor = "sec";

        testClient.get()
                .uri("/api/authors/symbol/" + nameOfAuthor)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(2)
                .jsonPath("$[0].name").isEqualTo("Second");

    }

    @Test
    void test_find_author_by_start_symbol_but_not_found() {
        testClient.get()
                .uri("/api/authors/symbol/" + "advwevwfdscsd")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void test_create_author_without_cover() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Nirvana");
        builder.part("description", "test desc");

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();

    }

    @Test
    void test_create_author_with_bad_payload() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "");
        builder.part("description", "test desc");

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void test_create_author_but_with_not_unique_name() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Second");
        builder.part("description", "test desc");

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void test_create_author() {
        Author author = new Author();
        author.setName("Test");
        author.setDescription("Some desc");

        when(client.saveAuthorImage(any(), any())).thenReturn(Mono.just(author));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage.jpeg"));

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated();

    }

    @Test
    public void test_create_author_when_not_admin() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage.jpeg"));

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "USER")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void test_create_author_with_wrong_file() {
        Author author = new Author();
        author.setName("Test");
        author.setDescription("Some desc");

        when(client.saveAuthorImage(any(), any())).thenReturn(Mono.error(() -> new FileValidationException("Wrong format of file. Can be only JPEG and PNG")));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("name", "test with cover");
        builder.part("description", "test desc with cover");
        builder.part("cover", new ClassPathResource("testImage"));

        testClient.post()
                .uri(t -> t.path("/api/authors/").build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.[0]").value(is("Wrong format of file. Can be only JPEG and PNG"));

    }

    @Test
    void test_update_author_without_cover() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Fifth");
        builder.part("description", "desc");

        testClient.put()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void test_update_author() {
        Author author = new Author();
        author.setName("Author test");
        author.setDescription("Some desc");

        when(client.saveAuthorImage(any(), any())).thenReturn(Mono.just(author));

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Fifth");
        builder.part("description", "desc");
        builder.part("cover", new ClassPathResource("testImage.jpeg"));

        testClient.put()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void test_update_author_with_not_unique_name() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "Second");
        builder.part("description", "desc");

        testClient.put()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void test_update_author_with_bad_payload() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "");
        builder.part("description", "desc");

        testClient.put()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "ADMIN")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void test_update_author_when_not_admin() {

        var builder = new MultipartBodyBuilder();

        builder.part("name", "test");
        builder.part("description", "desc");

        testClient.put()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "USER")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void test_delete_cover() {
        testClient.delete()
                .uri(t -> t.path("/api/authors/" + 3).build())
                .header("User-Role", "ADMIN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void test_delete_cover_but_not_found() {
        testClient.delete()
                .uri(t -> t.path("/api/authors/" + 100).build())
                .header("User-Role", "ADMIN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void test_delete_cover_when_not_admin() {
        testClient.delete()
                .uri(t -> t.path("/api/authors/" + 2).build())
                .header("User-Role", "USER")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }
}
