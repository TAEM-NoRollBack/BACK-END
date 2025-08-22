package com.marketplace.market_place.api.main.controller;

import com.marketplace.market_place.api.main.dto.MainBootstrapResponse;
import com.marketplace.market_place.api.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/bootstrap")
    public MainBootstrapResponse getBootstrap(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return mainService.bootstrap(lat, lon);
    }
}