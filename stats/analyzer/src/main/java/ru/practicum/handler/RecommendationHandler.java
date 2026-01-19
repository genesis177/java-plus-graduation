package ru.practicum.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;
import ru.practicum.model.ActionType;
import ru.practicum.model.EventSim;
import ru.practicum.model.UserAction;
import ru.practicum.repository.SimRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationHandler {

    final UserActionRepository userActionRepository;
    final SimRepository similarityRepository;

    @Value("${user-action.view}")
    Double viewAction;

    @Value("${user-action.register}")
    Double registerAction;

    @Value("${user-action.like}")
    Double likeAction;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int limit = request.getMaxResults();

        Set<Long> recentlyViewedEventIds = fetchRecentlyViewedEvents(userId, limit);
        if (recentlyViewedEventIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> candidateEventIds = findCandidateRecommendations(userId, recentlyViewedEventIds, limit);
        if (candidateEventIds.isEmpty()) {
            return Collections.emptyList();
        }

        return generateRecommendations(candidateEventIds, userId, limit);
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<EventSim> similaritiesA = fetchSimilaritiesByEventA(eventId, maxResults);
        List<EventSim> similaritiesB = fetchSimilaritiesByEventB(eventId, maxResults);

        List<RecommendedEventProto> recommendations = new ArrayList<>();
        addFilteredRecommendations(recommendations, similaritiesA, true, userId);
        addFilteredRecommendations(recommendations, similaritiesB, false, userId);

        return limitAndSortRecommendations(recommendations, maxResults);
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());
        Map<Long, Double> eventScores = calculateInteractionsScores(eventIds);
        return buildRecommendationsFromScores(eventScores);
    }

    private Set<Long> fetchRecentlyViewedEvents(Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return userActionRepository.findAllByUserId(userId, pageRequest).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<EventSim> fetchSimilaritiesByEventA(Long eventId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventA(eventId, pageRequest);
    }

    private List<EventSim> fetchSimilaritiesByEventB(Long eventId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventB(eventId, pageRequest);
    }

    private Set<Long> findCandidateRecommendations(Long userId, Set<Long> viewedEventIds, int limit) {
        List<EventSim> similaritiesA = similarityRepository.findAllByEventAIn(viewedEventIds, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score")));
        List<EventSim> similaritiesB = similarityRepository.findAllByEventBIn(viewedEventIds, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score")));

        Set<Long> candidateIds = new LinkedHashSet<>();
        addNewEventsFromSimilarities(similaritiesA, true, userId, candidateIds);
        addNewEventsFromSimilarities(similaritiesB, false, userId, candidateIds);

        return candidateIds;
    }

    private void addNewEventsFromSimilarities(List<EventSim> similarities,
                                              boolean isEventB,
                                              Long userId,
                                              Set<Long> result) {
        for (EventSim es : similarities) {
            Long candidateId = isEventB ? es.getEventB() : es.getEventA();
            if (candidateId != null && !userActionRepository.existsByEventIdAndUserId(candidateId, userId)) {
                result.add(candidateId);
            }
        }
    }

    private List<RecommendedEventProto> generateRecommendations(Set<Long> candidateEventIds,
                                                                Long userId,
                                                                int limit) {
        Map<Long, Double> scores = new HashMap<>();
        for (Long eventId : candidateEventIds) {
            scores.put(eventId, calculateRecommendationScore(eventId, userId, limit));
        }
        return buildTopRecommendations(scores, limit);
    }

    private Double calculateRecommendationScore(Long eventId, Long userId, int neighborsLimit) {
        List<EventSim> similaritiesA = fetchSimilaritiesByEventA(eventId, neighborsLimit);
        List<EventSim> similaritiesB = fetchSimilaritiesByEventB(eventId, neighborsLimit);

        Map<Long, Double> viewedSimilarityScores = collectViewedSimilarities(similaritiesA, true, userId);
        viewedSimilarityScores.putAll(collectViewedSimilarities(similaritiesB, false, userId));

        if (viewedSimilarityScores.isEmpty()) return 0.0;

        Map<Long, Double> userWeights = userActionRepository.findAllByEventIdInAndUserId(viewedSimilarityScores.keySet(), userId)
                .stream().collect(Collectors.toMap(UserAction::getEventId, ua -> toWeight(ua.getActionType())));

        return calculateWeightedScore(viewedSimilarityScores, userWeights);
    }

    private Map<Long, Double> collectViewedSimilarities(List<EventSim> similarities,
                                                        boolean isEventB,
                                                        Long userId) {
        Map<Long, Double> result = new HashMap<>();
        for (EventSim es : similarities) {
            Long relatedEventId = isEventB ? es.getEventB() : es.getEventA();
            if (relatedEventId != null && userActionRepository.existsByEventIdAndUserId(relatedEventId, userId)) {
                result.put(relatedEventId, es.getScore());
            }
        }
        return result;
    }

    private double calculateWeightedScore(Map<Long, Double> similarityScores,
                                          Map<Long, Double> userWeights) {
        double sumWeighted = 0.0;
        double sumSim = 0.0;
        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Double weight = userWeights.get(entry.getKey());
            if (weight != null) {
                sumWeighted += weight * entry.getValue();
                sumSim += entry.getValue();
            }
        }
        return sumSim > 0 ? sumWeighted / sumSim : 0.0;
    }

    private List<RecommendedEventProto> buildTopRecommendations(Map<Long, Double> scores, int limit) {
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> buildRecommendation(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void addFilteredRecommendations(List<RecommendedEventProto> recommendations,
                                            List<EventSim> similarities,
                                            boolean isEventB,
                                            Long userId) {
        for (EventSim es : similarities) {
            Long candidateId = isEventB ? es.getEventB() : es.getEventA();
            if (candidateId != null && !userActionRepository.existsByEventIdAndUserId(candidateId, userId)) {
                recommendations.add(buildRecommendation(candidateId, es.getScore()));
            }
        }
    }

    private List<RecommendedEventProto> limitAndSortRecommendations(List<RecommendedEventProto> recommendations, int maxResults) {
        recommendations.sort(Comparator.comparing(RecommendedEventProto::getScore).reversed());
        return recommendations.size() > maxResults ? recommendations.subList(0, maxResults) : recommendations;
    }

    private Map<Long, Double> calculateInteractionsScores(Set<Long> eventIds) {
        Map<Long, Double> scores = new HashMap<>();
        userActionRepository.findAllByEventIdIn(eventIds)
                .forEach(action -> scores.merge(action.getEventId(), toWeight(action.getActionType()), Double::sum));
        return scores;
    }

    private List<RecommendedEventProto> buildRecommendationsFromScores(Map<Long, Double> scores) {
        return scores.entrySet().stream()
                .map(entry -> buildRecommendation(entry.getKey(), entry.getValue()))
                .toList();
    }

    private RecommendedEventProto buildRecommendation(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }

    private Double toWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> viewAction;
            case REGISTER -> registerAction;
            case LIKE -> likeAction;
        };
    }
}