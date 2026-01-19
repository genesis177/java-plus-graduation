package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EventSim;

import java.util.List;
import java.util.Set;

@Repository
public interface SimRepository extends JpaRepository<EventSim, Long> {
    List<EventSim> findAllByEventA(Long eventId, PageRequest pageRequest);

    List<EventSim> findAllByEventB(Long eventId, PageRequest pageRequest);

    List<EventSim> findAllByEventAIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSim> findAllByEventBIn(Set<Long> eventIds, PageRequest pageRequest);
}