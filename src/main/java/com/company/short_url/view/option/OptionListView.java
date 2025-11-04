package com.company.short_url.view.option;

import com.company.short_url.entity.Option;
import com.company.short_url.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;

@Route(value = "options", layout = MainView.class)
@ViewController(id = "sh_Option.list")
@ViewDescriptor(path = "option-list-view.xml")
@LookupComponent("optionsDataGrid")
@DialogMode(width = "64em")
public class OptionListView extends StandardListView<Option> {
    @ViewComponent
    private CollectionContainer<Option> optionsDc;

    @ViewComponent
    private Button createButton;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        // Hide or disable "Create" button if at least one Option exists
        boolean optionExists = !optionsDc.getItems().isEmpty();
        createButton.setVisible(!optionExists); // or setEnabled(!optionExists);
    }
}
