package com.company.short_url.view.shorturl;

import com.company.short_url.entity.ShortUrl;
import com.company.short_url.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "short-urls", layout = MainView.class)
@ViewController(id = "sh_ShortUrl.list")
@ViewDescriptor(path = "short-url-list-view.xml")
@LookupComponent("shortUrlsDataGrid")
@DialogMode(width = "64em")
public class ShortUrlListView extends StandardListView<ShortUrl> {
}