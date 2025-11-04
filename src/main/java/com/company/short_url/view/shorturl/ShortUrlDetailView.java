package com.company.short_url.view.shorturl;

import com.company.short_url.app.OptionCache;
import com.company.short_url.entity.ShortUrl;
import com.company.short_url.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "short-urls/:id", layout = MainView.class)
@ViewController(id = "sh_ShortUrl.detail")
@ViewDescriptor(path = "short-url-detail-view.xml")
@EditedEntityContainer("shortUrlDc")
public class ShortUrlDetailView extends StandardDetailView<ShortUrl> {

    @Autowired
    private OptionCache optionCache;

    @ViewComponent
    private Button copy;

    @Subscribe(id = "copy", subject = "clickListener")
    public void onCopyClick(final ClickEvent<JmixButton> event) {
        ShortUrl entity = getEditedEntity(); // access the currently edited entity
        String href =
                String.format(
                        "%s/s/%s",
                        optionCache.getOption().getShortenerUrl(),
                        entity.getShortUrl() // or any other property you need
                );

        UiComponentUtils.copyToClipboard(href);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        ShortUrl entity = getEditedEntity();
        boolean isNew = entity.getCreatedAt() == null;
        copy.setVisible(!isNew);
    }
}
