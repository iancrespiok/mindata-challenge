package com.ian.challenge.infrastructure.adapter.in.rest;

import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.port.in.GetSearchCountUseCase;
import com.ian.challenge.domain.port.in.RegisterSearchUseCase;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchCountResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Tag(name = "Hotel search register (mindata challenge)", description = "Register and count searches of hotel availability")
public class SearchController {
    private final RegisterSearchUseCase registerSearchUseCase;
    private final GetSearchCountUseCase getSearchCountUseCase;
    private final SearchRestMapper mapper;

    public SearchController(RegisterSearchUseCase registerSearchUseCase, GetSearchCountUseCase getSearchCountUseCase, SearchRestMapper mapper) {
        this.registerSearchUseCase = registerSearchUseCase;
        this.getSearchCountUseCase = getSearchCountUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Register searches of hotel availability",
            description = "Validate payload, assigns and returns a unique id and publish it event asynchronously on Kafka for it posterior persistence.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Search has been registered",
                    content = @Content(schema = @Schema(implementation = SearchResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload ",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResponseDTO> search(@Valid @RequestBody SearchRequestDTO searchRequestDTO) {
        SearchId searchId = registerSearchUseCase.registerSearch(mapper.toDomain(searchRequestDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(searchId));
    }

    @Operation(summary = "Consult count of identical searches",
            description = "Returns the original search, the id and the count of registered searches with the exactly same criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search finded",
                    content = @Content(schema = @Schema(implementation = SearchCountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "It does not exist a search with this id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchCountResponseDTO> count(@RequestParam @NotBlank(message = "searchId is mandatory") String searchId) {
        GetSearchCountUseCase.SearchCountResult result = getSearchCountUseCase.getCount(new SearchId(searchId));
        return ResponseEntity.ok(mapper.toCountResponse(result.search(), result.count()));
    }
}
