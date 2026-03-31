package com.example.app.service;

import com.example.app.model.LiveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LiveEventService {

    private final Logger logger = LoggerFactory.getLogger(LiveEventService.class);

    //-- Eager Initialisation
    private final Map<String, LiveEvent> liveEventsData = new ConcurrentHashMap<>(1_00_000);

    //-- Add or Remove Cache Event Entries
    public void updateCache(String eventId, boolean status) {
        if (!status) {
            liveEventsData.remove(eventId);
            logger.info("CACHE UPDATED FOR EVENT ::: REMOVED ::: {}", eventId);
        } else {
            LiveEvent liveEvent = new LiveEvent();
            liveEvent.setEventId(eventId);
            liveEvent.setLive(true);
            liveEventsData.put(eventId,liveEvent);
            logger.info("CACHE UPDATED FOR EVENT ::: SCORE UPDATED ::: {}", liveEventsData.get(eventId));
        }
    }

    public LiveEvent getFromCache(String eventId) {
        return liveEventsData.get(eventId);
    }

    public List<LiveEvent> getAllEventsFromLiveCache() {
        return new ArrayList<>(liveEventsData.values());
    }

    public List<String> getAllEventIdsFromLiveCache() {
        return new ArrayList<>(liveEventsData.keySet());
    }

}