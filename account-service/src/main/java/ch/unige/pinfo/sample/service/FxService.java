package ch.unige.pinfo.sample.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FxService {

    public Double getRate(String originalCurrency, String accountCurrency) {
        if (originalCurrency.equals(accountCurrency))            
            return 1.0d;
        else if (originalCurrency.equals("USD") && accountCurrency.equals("CHF")) {
            return 0.9d;
        } else if (originalCurrency.equals("CHF") && accountCurrency.equals("USD")) {
            return 1.1d;
        } else {
            return 1.0d;
        }
    }


}
