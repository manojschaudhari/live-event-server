package com.example.app.controller;

import com.example.app.service.LiveEventService;
import com.example.app.model.LiveEvent;
import com.example.app.model.LiveServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("")
public class LiveEventController {

    private final Logger logger = LoggerFactory.getLogger(LiveEventController.class);

    @Autowired
    LiveEventService liveEventService;

    @PostMapping("/events/status")
    @Operation(description = "update event cache with live or non-live status for event identifier.")
    public LiveServerResponse<String> event(@RequestBody LiveEvent event) {
        try {
            if (event.getLive() != null && event.getEventId() != null && NumberUtils.isParsable(event.getEventId())) {
                logger.info("NEW EVENT REQUEST ::: {}", event.getEventId());
                liveEventService.updateCache(event.getEventId(), event.getLive());
                return LiveServerResponse.ok("SUCCESS");
            } else {
                throw new RuntimeException("INPUT PARAMETER ISSUE");
            }
        } catch (Exception ex) {
            logger.error("ERROR PROCESSING REQUEST ::: UPDATE CACHE ::: {}", event, ex);
            return LiveServerResponse.error("FAILED",ex.getMessage());
        }
    }

    @PostMapping("/events/live")
    @Operation(description = "List all live events.")
    public LiveServerResponse<List<LiveEvent>> events() {
        try {
            return LiveServerResponse.ok(liveEventService.getAllEventsFromLiveCache());
        } catch (Exception ex) {
            logger.error("ERROR PROCESSING REQUEST ::: LIVE EVENTS ::: ",ex);
            return LiveServerResponse.error(new ArrayList<>(),ex.getMessage());
        }
    }

    @PostMapping("/events/{eventId}")
    @Operation(description = "Get Event Details for event identifier.")
    public LiveServerResponse<List<LiveEvent>> eventFromCache(@PathVariable String eventId) {
        try {
            if (eventId != null && NumberUtils.isParsable(eventId)) {
                logger.info("GET EVENT REQUEST ::: {}", eventId);
                return LiveServerResponse.ok(Stream.of(liveEventService.getFromCache(eventId)).filter(Objects::nonNull).collect(Collectors.toList()));
            } else {
                throw new RuntimeException("UNABLE TO PARSE");
            }
        } catch(Exception ex) {
            logger.error("ERROR PROCESSING REQUEST ::: GET EVENT ::: {}",eventId,ex);
            return LiveServerResponse.error(new ArrayList<>(),ex.getMessage());
        }

    }


}

