package com.example.app.controller;

import com.example.app.model.MockEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock-api")
public class MockController {

    @PostMapping("/eventScore")
    public MockEvent event(@RequestBody String eventId) {
        return new MockEvent(eventId);
    }

}