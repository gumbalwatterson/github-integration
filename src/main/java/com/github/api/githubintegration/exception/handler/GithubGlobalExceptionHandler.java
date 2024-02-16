package com.github.api.githubintegration.exception.handler;

import com.github.api.githubintegration.exception.GithubNoRepositoriesException;
import com.github.api.githubintegration.exception.model.GithubError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GithubGlobalExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<GithubError> handleRestCallException(RestClientResponseException ex) {
        log.error("Exception while performing call to Github API: {}", ex.getMessage(), ex);
        if (ex.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(new GithubError(ex.getStatusCode().value(), "User does not exists"));
        }
        return ResponseEntity.status(ex.getStatusCode())
                .headers(ex.getResponseHeaders()).body(new GithubError(ex.getStatusCode().value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GithubError> handleGlobalException(Exception ex) {
        log.error("Exception while processing request: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(new GithubError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "API internal problems"));
    }

    @ExceptionHandler(GithubNoRepositoriesException.class)
    public ResponseEntity<GithubError> handleNoRepositoryFound(Exception ex) {
        log.error("Exception while processing request: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GithubError(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GithubError> handleNoResourceFound(Exception ex) {
        log.error("Exception while processing request: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GithubError(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}
