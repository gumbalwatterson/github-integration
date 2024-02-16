package com.github.api.githubintegration.service.impl;

import com.github.api.githubintegration.model.Branch;
import com.github.api.githubintegration.model.Repository;
import com.github.api.githubintegration.service.GithubClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
public class GithubClientImpl implements GithubClient {

    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<Branch[]> performReqForBranches(String branchesUrl) {
        String url = createBranchesLink(branchesUrl);
        return restTemplate.getForEntity(url, Branch[].class);
    }

    @Override
    public ResponseEntity<Repository[]> performReqForRepositories(String username) {
        String url = createRepositoriesLink(username);
        return restTemplate.getForEntity(url, Repository[].class);
    }

    private String createBranchesLink(String branchesUrl) {
        return branchesUrl.contains("{") ? branchesUrl.split("\\{")[0] : branchesUrl;
    }

    private String createRepositoriesLink(String username) {
        return "https://api.github.com/users/" + username + "/repos";
    }

}
