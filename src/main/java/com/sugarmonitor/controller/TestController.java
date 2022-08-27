package com.sugarmonitor.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class TestController {

    @GetMapping()
    public ResponseEntity<String> pebble() {
        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://sugarinpashtet2.herokuapp.com/";

        ResponseEntity<String> response
                = restTemplate.getForEntity(uri + "/pebble", String.class);
        return response;
    }

}
