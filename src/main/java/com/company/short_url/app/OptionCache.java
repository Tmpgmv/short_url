package com.company.short_url.app;

import com.company.short_url.entity.Option;
import io.jmix.core.DataManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sh_OptionCache")
public class OptionCache {
    @Autowired
    private DataManager dataManager;

    private Option cachedOption;

    @PostConstruct
    public void init() {
        // Assuming only one row in Option table
        cachedOption = dataManager.load(Option.class).all().one();
    }

    public Option getOption() {
        return cachedOption;
    }
}
