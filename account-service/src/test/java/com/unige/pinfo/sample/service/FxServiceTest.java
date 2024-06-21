package com.unige.pinfo.sample.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.service.FxService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class FxServiceTest {

    @Inject
    FxService fxService;

    @Test
    void testGetRate() {
        Assertions.assertEquals(1.0d, fxService.getRate("USD", "USD"));
        Assertions.assertEquals(0.9d, fxService.getRate("USD", "CHF"));
        Assertions.assertEquals(1.1d, fxService.getRate("CHF", "USD"));
        Assertions.assertEquals(1.0d, fxService.getRate("CHF", "CHF"));
        Assertions.assertEquals(1.0d, fxService.getRate("CHF", "XHR"));
    }
}
