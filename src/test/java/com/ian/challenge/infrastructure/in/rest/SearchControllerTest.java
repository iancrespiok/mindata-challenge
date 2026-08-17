package com.ian.challenge.infrastructure.in.rest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ian.challenge.Fixture;
import com.ian.challenge.domain.exception.SearchNotFoundException;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.in.GetSearchCountUseCase;
import com.ian.challenge.domain.port.in.RegisterSearchUseCase;
import com.ian.challenge.infrastructure.adapter.in.rest.SearchController;
import com.ian.challenge.infrastructure.adapter.in.rest.SearchRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@Import(SearchRestMapper.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterSearchUseCase registerSearchUseCase;

    @MockitoBean
    private GetSearchCountUseCase getSearchCountUseCase;

    @Test
    void searchReturnsCreatedWithSearchId() throws Exception {
        when(registerSearchUseCase.registerSearch(any())).thenReturn(new SearchId("abc-123"));

        String body = Fixture.DEFAULT_REQUEST_JSON;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.searchId").value("abc-123"));
    }

    @Test
    void searchRejectsBlankAndMalformedFields() throws Exception {
        String body = """
                {
                  "hotelId": "",
                  "checkIn": "2023-12-29",
                  "checkOut": "31/12/2023",
                  "ages": []
                }
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchRejectsNegativeAges() throws Exception {
        String body = """
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2023",
                  "checkOut": "31/12/2023",
                  "ages": [30, -1]
                }
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchRejectsCheckInNotBeforeCheckOut() throws Exception {
        when(registerSearchUseCase.registerSearch(any()))
                .thenThrow(new IllegalArgumentException("checkIn must be before checkOut"));

        String body = """
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2023",
                  "checkOut": "29/12/2023",
                  "ages": [30]
                }
                """;

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Check in date must be before check out date."));
    }

    @Test
    void countReturnsSearchAndCount() throws Exception {
        SearchCriteria criteria = Fixture.criteriaWithAges(List.of(3, 29, 30, 1));
        SearchRecord record = Fixture.recordWithId(new SearchId("abc-123"), criteria);
        when(getSearchCountUseCase.getCount(new SearchId("abc-123")))
                .thenReturn(new GetSearchCountUseCase.SearchCountResult(record, 100L));

        mockMvc.perform(get("/count").param("searchId", "abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("abc-123"))
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.search.hotelId").value("1234aBc"))
                .andExpect(jsonPath("$.search.ages[0]").value(3));
    }

    @Test
    void countReturnsNotFoundWhenSearchIdDoesNotExist() throws Exception {
        when(getSearchCountUseCase.getCount(any())).thenThrow(new SearchNotFoundException("missing-id"));

        mockMvc.perform(get("/count").param("searchId", "missing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void countRejectsBlankSearchId() throws Exception {
        mockMvc.perform(get("/count").param("searchId", "   "))
                .andExpect(status().isBadRequest());
    }
}
