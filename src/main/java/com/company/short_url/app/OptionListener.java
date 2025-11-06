package com.company.short_url.app;

import com.company.short_url.entity.Option;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("sh_OptionListener")
public class OptionListener {
  @Autowired private DataManager dataManager;

  @EventListener
  public void onOptionListener(EntitySavingEvent<Option> event) {
    long count = dataManager.load(Option.class).all().list().size();
    if (count > 1) {
      throw new IllegalStateException("There can be only one Option record.");
    }
  }
}
