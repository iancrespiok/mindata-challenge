package com.ian.challenge.infrastructure.adapter.out.persistence;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class SearchPersistenceAdapter implements SearchRepository {
    private final SearchJpaRepository jpaRepository;

    public SearchPersistenceAdapter(SearchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(SearchRecord searchRecord) {
        SearchCriteria criteria = searchRecord.criteria();
        SearchEntity entity = new SearchEntity(
                searchRecord.searchId().value(),
                criteria.hotelId(),
                criteria.checkIn(),
                criteria.checkOut(),
                criteria.agesKey(),
                searchRecord.registeredAt()
        );
        jpaRepository.save(entity);
    }

    @Override
    public Optional<SearchRecord> findById(SearchId searchId) {
        return jpaRepository.findById(searchId.value()).map(this::toDomain);
    }

    @Override
    public long countByCriteria(SearchCriteria criteria) {
        return jpaRepository.countByCriteria(
                criteria.hotelId(), criteria.checkIn(), criteria.checkOut(), criteria.agesKey());
    }

    private SearchRecord toDomain(SearchEntity entity) {
        List<Integer> ages = Stream.of(entity.getAgesKey().split(","))
                .map(Integer::parseInt)
                .toList();
        SearchCriteria criteria = new SearchCriteria(entity.getHotelId(), entity.getCheckIn(),
                entity.getCheckOut(), ages);
        return new SearchRecord(new SearchId(entity.getSearchId()), criteria, entity.getRegisteredAt());
    }
}
