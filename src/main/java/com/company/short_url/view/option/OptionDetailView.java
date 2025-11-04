package com.company.short_url.view.option;

import com.company.short_url.entity.Option;
import com.company.short_url.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "options/:id", layout = MainView.class)
@ViewController(id = "sh_Option.detail")
@ViewDescriptor(path = "option-detail-view.xml")
@EditedEntityContainer("optionDc")
public class OptionDetailView extends StandardDetailView<Option> {
}
