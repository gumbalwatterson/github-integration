package com.github.api.githubintegration.exception;

import lombok.Getter;

@Getter
public class GithubNoRepositoriesException extends RuntimeException {
    private final int statusCode;

    public GithubNoRepositoriesException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
