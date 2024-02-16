package com.github.api.githubintegration.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubError {
    int status;
    String message;
}
