package com.github.api.githubintegration.service;


import com.github.api.githubintegration.model.Branch;
import com.github.api.githubintegration.model.Repository;
import org.springframework.http.ResponseEntity;

public interface GithubClient {
    ResponseEntity<Branch[]> performReqForBranches(String branchesUrl);

    ResponseEntity<Repository[]> performReqForRepositories(String userName);
}
