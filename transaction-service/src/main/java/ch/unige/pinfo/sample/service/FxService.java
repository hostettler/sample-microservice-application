package ch.unige.pinfo.sample.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FxService {

    public Double getRate(String originalCurrency, String accountCurrency) {
        return 1.0d;
    }


}
