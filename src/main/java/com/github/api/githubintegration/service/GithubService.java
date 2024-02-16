package com.github.api.githubintegration.service;

import com.github.api.githubintegration.dto.RepositoryDataDto;

import java.util.List;

public interface GithubService {
    List<RepositoryDataDto> getNonForkRepositoriesByUserName(String username);
}
