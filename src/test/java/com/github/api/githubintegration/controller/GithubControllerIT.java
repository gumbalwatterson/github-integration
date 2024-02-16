package com.github.api.githubintegration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.api.githubintegration.dto.BranchDataDto;
import com.github.api.githubintegration.dto.RepositoryDataDto;
import com.github.api.githubintegration.exception.GithubNoRepositoriesException;
import com.github.api.githubintegration.exception.model.GithubError;
import com.github.api.githubintegration.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GithubControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GithubService githubService;
    @Autowired
    private ObjectMapper mapper;
    private static final String URL = "/api/repos/nonfork/user/testUser";

    @Test
    public void shouldReturnSuccessResponse() throws Exception {
        when(githubService.getNonForkRepositoriesByUserName(anyString())).thenReturn(getTestDtoData());
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(getTestDtoData())));
    }

    @Test
    public void shouldReturnResponseForNoRepositories() throws Exception {
        when(githubService.getNonForkRepositoriesByUserName(anyString()))
                .thenThrow(new GithubNoRepositoriesException("No repositories were found", HttpStatus.NOT_FOUND.value()));
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(
                        getTestErrorData(HttpStatus.NOT_FOUND.value(), "No repositories were found"))));
    }

    @Test
    public void shouldReturnErrorResponseForGitApiNonExistingUser() throws Exception {
        when(githubService.getNonForkRepositoriesByUserName(anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(getTestErrorData(HttpStatus.NOT_FOUND.value(),
                                "User does not exists"))));
    }

    @Test
    public void shouldReturnErrorResponseForGitApiInternalError() throws Exception {
        when(githubService.getNonForkRepositoriesByUserName(anyString())).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(getTestErrorData(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "500 INTERNAL_SERVER_ERROR"))));
    }

    @Test
    public void shouldReturnErrorResponseForInternalError() throws Exception {
        when(githubService.getNonForkRepositoriesByUserName(anyString())).thenThrow(new NullPointerException());
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(getTestErrorData(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "API internal problems"))));
    }

    @Test
    public void shouldReturnErrorResponseForNoResources() throws Exception {
        given(githubService.getNonForkRepositoriesByUserName(anyString())).willAnswer(invocation ->
        {
            throw new NoResourceFoundException(HttpMethod.GET, "/api/repos/nonfork/user/??");
        });
        MockHttpServletResponse mockHttpServletResponse =
                mockMvc.perform(MockMvcRequestBuilders.get(URL)).andReturn().getResponse();

        assertTrue(mockHttpServletResponse.getContentAsString().contains("/api/repos/nonfork/user/??"));
        assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.NOT_FOUND.value());
    }

    private GithubError getTestErrorData(int statusCode, String message) {
        return new GithubError(statusCode, message);
    }

    private List<RepositoryDataDto> getTestDtoData() {
        return singletonList(new RepositoryDataDto("testOwner", "testRepository",
                singletonList(new BranchDataDto("testBranchName", "testSha"))));
    }
}
