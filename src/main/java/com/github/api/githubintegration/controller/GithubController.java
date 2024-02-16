package com.github.api.githubintegration.controller;

import com.github.api.githubintegration.dto.RepositoryDataDto;
import com.github.api.githubintegration.service.GithubService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping(value = "/repos/nonfork/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RepositoryDataDto>> getNonForkUserRepositories(@PathVariable String username) {
        List<RepositoryDataDto> repositoryDataDtoList = githubService.getNonForkRepositoriesByUserName(username);
        return ResponseEntity.ok(repositoryDataDtoList);
    }

}
