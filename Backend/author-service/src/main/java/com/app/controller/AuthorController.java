package com.app.controller;


import com.app.payload.request.AuthorCreateOrUpdateRequest;
import com.app.payload.response.AuthorResponse;
import com.app.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/api/authors/")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{id}")
    public Mono<AuthorResponse> getAuthor(@PathVariable("id") Integer id) {
        return authorService.getAuthorById(id);
    }

    @GetMapping
    public Flux<AuthorResponse> getAuthorsByIds(@RequestParam("ids") List<Integer> ids) {
        return authorService.getAuthorsByIds(ids);
    }

    @GetMapping("/symbol/{symbol}")
    public Flux<AuthorResponse> getAuthorsBySymbols(@PathVariable("symbol") String symbol) {
        return authorService.getAuthorByFirstSymbols(URLDecoder.decode(symbol, Charset.defaultCharset()));
    }

    @GetMapping("owner/{id}")
    public Mono<Boolean> canModify(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "userId",
                    required = false,
                    defaultValue = "0") Integer userId
    ) {
        return authorService.canUserModify(id, userId);
    }

    @PostMapping
    public Mono<ResponseEntity<?>> createAuthor(
            @Valid @ModelAttribute AuthorCreateOrUpdateRequest dto,
            @RequestHeader("userId") Integer userId
    ) {
        return authorService.saveAuthor(dto, userId);
    }

    @PutMapping("{id}")
    public Mono<Void> updateAuthor(
            @Valid @ModelAttribute AuthorCreateOrUpdateRequest dto,
            @PathVariable("id") Integer id,
            @RequestHeader("userId") Integer userId
    ) {
        return authorService.updateAuthor(dto, id, userId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteAuthorImage(
            @PathVariable("id") Integer id,
            @RequestHeader("userId") Integer userId
    ) {
        return authorService.deleteAuthorImage(id, userId);
    }
}
