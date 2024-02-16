package com.github.api.githubintegration.service.impl;

import com.github.api.githubintegration.dto.BranchDataDto;
import com.github.api.githubintegration.dto.RepositoryDataDto;
import com.github.api.githubintegration.exception.GithubNoRepositoriesException;
import com.github.api.githubintegration.model.Branch;
import com.github.api.githubintegration.model.Repository;
import com.github.api.githubintegration.service.GithubClient;
import com.github.api.githubintegration.service.GithubService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final GithubClient githubClient;

    public List<RepositoryDataDto> getNonForkRepositoriesByUserName(String username) {
        ResponseEntity<Repository[]> response = githubClient.performReqForRepositories(username);
        List<Repository> repositories = requireRepos(Arrays.asList(response.getBody())).stream().filter(a -> !a.isFork()).toList();
        log.info("Number of non fork repositories: {}, for user: {}", repositories.size(), username);

        return requireRepos(repositories).stream()
                .map(this::createRepositoryDataDto)
                .collect(Collectors.toList());
    }

    private List<Repository> requireRepos(List<Repository> body) {
        if (body.isEmpty()) {
            throw new GithubNoRepositoriesException("No repositories were found", HttpStatus.NOT_FOUND.value());
        }
        return body;
    }

    private RepositoryDataDto createRepositoryDataDto(Repository repo) {
        return new RepositoryDataDto(extractOwnerName(repo.getFullName()), repo.getName(), extractBranchInfo(repo));
    }

    private String extractOwnerName(String fullName) {
        return fullName.split("/")[0];
    }

    private List<BranchDataDto> extractBranchInfo(Repository repository) {
        ResponseEntity<Branch[]> response = githubClient.performReqForBranches(repository.getBranchesUrl());
        log.info("Retrieved branches: {}, for repository: {}", response.getBody().length, repository.getName());
        return Arrays.stream(response.getBody())
                .map(b -> new BranchDataDto(b.getName(), b.getCommit().getSha())).collect(Collectors.toList());
    }
}
