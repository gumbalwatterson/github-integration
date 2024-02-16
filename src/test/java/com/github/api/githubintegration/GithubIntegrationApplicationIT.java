package com.github.api.githubintegration;

import com.github.api.githubintegration.model.Repository;
import com.github.api.githubintegration.service.GithubClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GithubIntegrationApplicationIT {

    @Autowired
    GithubClient githubClient;
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void contextTest() {
        assertNotNull(githubClient);
        assertNotNull(restTemplate);
    }

    @Test
    public void shouldReturnSuccessGithubAPICall() {
        String url = "https://api.github.com/users/gumbalwatterson/repos";
        ResponseEntity<Repository[]> response = restTemplate.getForEntity(url, Repository[].class);

        assertNotNull(response);
        assertEquals(response.getStatusCode() , HttpStatus.OK);
    }

    @Test
    public void shouldReturnNotFoundGithubAPICall() {
        String url = "https://api.github.com/users/??/repos";
        RestClientResponseException exception = assertThrows(RestClientResponseException.class,
                () -> restTemplate.getForEntity(url, Repository[].class));

        assertTrue(exception.getMessage().contains("Not Found"));
        assertEquals(exception.getStatusCode() , HttpStatus.NOT_FOUND);
    }
}
