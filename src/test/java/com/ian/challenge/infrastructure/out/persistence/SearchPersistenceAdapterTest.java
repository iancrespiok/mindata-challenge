package com.ian.challenge.infrastructure.out.persistence;


import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.Fixture;
import com.ian.challenge.infrastructure.adapter.out.persistence.SearchPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(SearchPersistenceAdapter.class)
class SearchPersistenceAdapterTest {

    @Autowired
    private SearchPersistenceAdapter adapter;

    @Test
    void savesAndFindsBySearchId() {
        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        adapter.save(record);
        Optional<SearchRecord> found = adapter.findById(record.searchId());

        assertAll("persisted search is as been saved",
                () -> assertTrue(found.isPresent()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), found.orElseThrow().criteria().ages())
        );
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(adapter.findById(SearchId.generate()).isEmpty());
    }

    @Test
    void countsOnlyExactOrderMatches() {
        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchCriteria reordered = Fixture.criteriaWithAges(List.of(1, 3, 29, 30));

        adapter.save(Fixture.recordWithCriteria(criteria));
        adapter.save(Fixture.recordWithCriteria(criteria));
        adapter.save(Fixture.recordWithCriteria(reordered));

        assertAll("count is only with EXACTLY order matches",
                () -> assertEquals(2L, adapter.countByCriteria(criteria)),
                () -> assertEquals(1L, adapter.countByCriteria(reordered))
        );
    }

    @Test
    void saveIsUpsertBySearchId() {
        SearchId id = SearchId.generate();
        SearchCriteria criteria = Fixture.shortWindowCriteria(List.of(5));
        adapter.save(Fixture.recordWithId(id, criteria));
        adapter.save(Fixture.recordWithId(id, criteria));

        assertEquals(1L, adapter.countByCriteria(criteria));
    }

    @Test
    void twoSearchesWithIdenticalCriteriaGetDifferentUniqueIds() {
        SearchCriteria criteria = Fixture.shortWindowCriteria(List.of(5));
        SearchRecord first = Fixture.recordWithCriteria(criteria);
        SearchRecord second = Fixture.recordWithCriteria(criteria);

        adapter.save(first);
        adapter.save(second);

        assertAll("each search has an unique id, although criteria are equals",
                () -> assertTrue(adapter.findById(first.searchId()).isPresent()),
                () -> assertTrue(adapter.findById(second.searchId()).isPresent()),
                () -> assertEquals(2L, adapter.countByCriteria(criteria))
        );
    }

    @Test
    void hotelIdWithSqlMetacharactersIsTreatedAsLiteralData() {
        String maliciousHotelId = "abc'; DROP TABLE hotel_search; --";
        SearchCriteria criteria = Fixture.criteriaWithHotelId(maliciousHotelId);
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        adapter.save(record);

        assertAll("value persists as string, without executing SQL",
                () -> assertEquals(1L, adapter.countByCriteria(criteria)),
                () -> assertEquals(maliciousHotelId,
                        adapter.findById(record.searchId()).orElseThrow().criteria().hotelId())
        );
    }
}
