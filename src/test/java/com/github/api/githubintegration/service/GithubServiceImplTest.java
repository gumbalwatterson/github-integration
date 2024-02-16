package com.github.api.githubintegration.service;

import com.github.api.githubintegration.dto.RepositoryDataDto;
import com.github.api.githubintegration.exception.GithubNoRepositoriesException;
import com.github.api.githubintegration.model.Branch;
import com.github.api.githubintegration.model.Commit;
import com.github.api.githubintegration.model.Repository;
import com.github.api.githubintegration.service.impl.GithubServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GithubServiceImplTest {

    @Mock
    private GithubClient githubClient;
    @InjectMocks
    private GithubServiceImpl githubService;

    @Test
    public void shouldThrowExceptionIfOnlyForks() {
        when(githubClient.performReqForRepositories(anyString())).thenReturn(new ResponseEntity<>(getArrayOfRepository(true), HttpStatus.OK));
        GithubNoRepositoriesException exception = assertThrows(GithubNoRepositoriesException.class,
                () -> githubService.getNonForkRepositoriesByUserName(anyString()));

        assertTrue(exception.getMessage().contains("No repositories were found"));
        assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND.value());
        verify(githubClient, times(1)).performReqForRepositories(anyString());
    }

    @Test
    public void shouldReturnRepositoryAndBranchList() {
        when(githubClient.performReqForRepositories(anyString())).thenReturn(new ResponseEntity<>(getArrayOfRepository(false), HttpStatus.OK));
        when(githubClient.performReqForBranches(anyString())).thenReturn(new ResponseEntity<>(getArrayOfBranch(), HttpStatus.OK));
        List<RepositoryDataDto> repositoryDataDtos = githubService.getNonForkRepositoriesByUserName("testUser");

        assertNotNull(repositoryDataDtos);
        assertEquals("testNameRepository", repositoryDataDtos.getFirst().getRepositoryName());
        assertEquals("testBranchName", repositoryDataDtos.getFirst().getBranches().getFirst().getBranchName());
        verify(githubClient, times(1)).performReqForRepositories(anyString());
        verify(githubClient, times(1)).performReqForBranches(anyString());
    }

    @Test
    public void shouldThrowExceptionIfNoRepository() {
        when(githubClient.performReqForRepositories(anyString())).thenReturn(new ResponseEntity<>(new Repository[]{}, HttpStatus.OK));
        GithubNoRepositoriesException exception = assertThrows(GithubNoRepositoriesException.class,
                () -> githubService.getNonForkRepositoriesByUserName(anyString()));

        assertTrue(exception.getMessage().contains("No repositories were found"));
        assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND.value());
        verify(githubClient, times(1)).performReqForRepositories(anyString());
    }

    @Test
    public void shouldReturnEmptyListForBranches() {
        when(githubClient.performReqForRepositories(anyString())).thenReturn(new ResponseEntity<>(getArrayOfRepository(false), HttpStatus.OK));
        when(githubClient.performReqForBranches(anyString())).thenReturn(new ResponseEntity<>(new Branch[]{}, HttpStatus.OK));

        List<RepositoryDataDto> repositoryDataDtos = githubService.getNonForkRepositoriesByUserName("testUser");
        assertNotNull(repositoryDataDtos);
        assertTrue(repositoryDataDtos.getFirst().getBranches().isEmpty());
        assertEquals("testNameRepository", repositoryDataDtos.getFirst().getRepositoryName());
        verify(githubClient, times(1)).performReqForRepositories(anyString());
        verify(githubClient, times(1)).performReqForBranches(anyString());
    }

    private Branch[] getArrayOfBranch() {
        return new Branch[]{new Branch("testBranchName", new Commit("testSha"))};
    }

    private Repository[] getArrayOfRepository(boolean fork) {
        return new Repository[]{new Repository(fork, "testUrl", "testNameRepository",
                "fullName", "branchesUrlTest")};
    }
}
