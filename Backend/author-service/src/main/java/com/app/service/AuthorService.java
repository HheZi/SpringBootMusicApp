package com.app.service;

import com.app.enums.UserRole;
import com.app.exception.AuthorNameException;
import com.app.kafka.KafkaImageProducer;
import com.app.kafka.message.ImageDeletionMessage;
import com.app.model.Author;
import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.repository.AuthorRepository;
import com.app.util.AuthorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final ImageClient imageClient;

    private final KafkaImageProducer kafkaImageProducer;

    private final String TEMP_FOLDER_NAME = "temp";

    public Mono<AuthorResponse> getAuthorById(Integer id) {
        return authorRepository
                .findById(id)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(a -> authorMapper.fromAuthorToAuthorResponse(a, true));
    }

    public Flux<AuthorResponse> getAuthorsByIds(List<Integer> ids) {
        return authorRepository.findAllById(ids)
                .map(a -> authorMapper.fromAuthorToAuthorResponse(a, false));
    }

    public Flux<AuthorResponse> getAuthorByFirstSymbols(String symbols) {
        return authorRepository
                .findByNameStartingWithIgnoreCase(symbols)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(a -> authorMapper.fromAuthorToAuthorResponse(a, false));
    }

    @Transactional
    public Mono<ResponseEntity<?>> saveAuthor(AuthorCreateOrUpdateRequest dto, UserRole userRole) {
        if (userRole != UserRole.ADMIN) {
            return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        }

        if (dto.getCover() != null) {
            File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();

            return dto.getCover().transferTo(file)
                    .then(Mono.fromCallable(() -> authorMapper.fromAuthorRequestToAuthor(dto,true)))
                    .flatMap(this::isAuthorNameUnique)
                    .flatMap(t -> imageClient.saveAuthorImage(t, file))
                    .flatMap(authorRepository::save)
                    .doFinally(t -> file.delete())
                    .map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
        }

        return Mono.just(authorMapper.fromAuthorRequestToAuthor(dto, false))
                .flatMap(this::isAuthorNameUnique)
                .flatMap(authorRepository::save)
                .map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
    }

    @Transactional
    public Mono<Void> updateAuthor(AuthorCreateOrUpdateRequest dto, Integer id, UserRole userRole) {
        if (userRole != UserRole.ADMIN) {
            return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        }

        if (dto.getCover() != null) {
            File file = new File(TEMP_FOLDER_NAME, dto.getCover().filename()).getAbsoluteFile();

            return dto.getCover().transferTo(file)
                    .then(authorRepository.findById(id))
                    .flatMap(t -> this.mapAuthorForUpdate(t, dto))
                    .flatMap(t -> imageClient.saveAuthorImage(t, file))
                    .flatMap(authorRepository::save)
                    .doFinally(t -> file.delete())
                    .then();
        }

        return authorRepository.findById(id)
                .flatMap(t -> this.mapAuthorForUpdate(t, dto))
                .flatMap(authorRepository::save)
                .then();

    }

    private Mono<Author> mapAuthorForUpdate(Author author, AuthorCreateOrUpdateRequest dto) {
        author.setDescription(dto.getDescription());
        if (author.getImageName() == null && dto.getCover() != null) {
            author.setImageName(UUID.randomUUID());
        }
        if (!author.getName().equals(dto.getName())) {
            author.setName(dto.getName());
            return this.isAuthorNameUnique(author);
        }

        return Mono.just(author);
    }

    public Mono<Void> deleteAuthorImage(Integer id, UserRole userRole) {
        if (userRole != UserRole.ADMIN) {
            return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        }

        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .filter(t -> Objects.nonNull(t.getImageName()))
                .doOnNext(t -> {
                    kafkaImageProducer.sendMessageToDeleteImage(new ImageDeletionMessage(t.getImageName()));
                    t.setImageName(null);
                })
                .flatMap(authorRepository::save)
                .then();
    }

    private Mono<Author> isAuthorNameUnique(Author author) {
        return Mono.zip(Mono.just(author), authorRepository.existsByNameIgnoreCase(author.getName()))
                .filter(t -> !t.getT2())
                .switchIfEmpty(Mono.error(() -> new AuthorNameException("Author with this name already exists")))
                .map(Tuple2::getT1);

    }
}
