package com.github.api.githubintegration.service;

import com.github.api.githubintegration.model.Branch;
import com.github.api.githubintegration.model.Commit;
import com.github.api.githubintegration.model.Repository;
import com.github.api.githubintegration.service.impl.GithubClientImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GithubClientImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private GithubClientImpl githubClient;
    private static final String URL = "testUrl";

    @Test
    public void shouldReturnResponseForBranch() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity(getArrayOfBranch(), HttpStatus.OK));
        ResponseEntity<Branch[]> response = githubClient.performReqForBranches(URL);

        assertNotNull(response);
        assertEquals(response.getBody()[0].getName(), "testBranchName");
        assertEquals(response.getBody()[0].getCommit().getSha(), "testSha");
        verify(restTemplate, times(1)).getForEntity(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionForBranch() {
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        RestClientResponseException exception = assertThrows(RestClientResponseException.class, () -> githubClient.performReqForBranches(URL));

        assertEquals(exception.getStatusCode().value(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        verify(restTemplate, times(1)).getForEntity(anyString(), any());
    }

    @Test
    public void shouldReturnResponseForRepository() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity(getArrayOfRepository(), HttpStatus.OK));
        ResponseEntity<Repository[]> response = githubClient.performReqForRepositories(URL);

        assertNotNull(response);
        assertEquals(response.getBody()[0].getName(), "testNameRepository");
        verify(restTemplate, times(1)).getForEntity(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionForRepository() {
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        RestClientResponseException exception = assertThrows(RestClientResponseException.class, () -> githubClient.performReqForRepositories(URL));

        assertEquals(exception.getStatusCode().value(), HttpStatus.NOT_FOUND.value());
        verify(restTemplate, times(1)).getForEntity(anyString(), any());
    }

    private Branch[] getArrayOfBranch() {
        return new Branch[]{new Branch("testBranchName", new Commit("testSha"))};
    }

    private Repository[] getArrayOfRepository() {
        return new Repository[]{new Repository(false, URL, "testNameRepository",
                "fullName", "branchesUrlTest")};
    }
}
