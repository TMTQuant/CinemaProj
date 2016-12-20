package com.vorontsov.Design;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vorontsov.Base.*;
import com.vorontsov.MyUI;

import java.util.List;

public class MainForm extends VerticalLayout{
    private MyUI myUI;
    private FilmService service = FilmService.getInstance();
    private Grid grid = new Grid();
    private TextField filterText = new TextField();
    private Button clearFilterButton = new Button(FontAwesome.TIMES);
    private Button addNewFilmButton = new Button("Add new film");
    private EditingForm editingForm;
    private CssLayout filtering = new CssLayout();

    public MainForm(MyUI myUI) {
        this.myUI = myUI;
        editingForm = new EditingForm(myUI, this);
        grid.setColumns("id", "title", "duration", "ageRestrictions", "genreID", "rating");
        grid.setSizeFull();

        updateList();

        filterText.addTextChangeListener(e -> {
            grid.setContainerDataSource(new BeanItemContainer<>(Film.class, service.findAll(e.getText())));
                });
        filterText.setInputPrompt("filter by name...");


        addNewFilmButton.addClickListener(e -> {
            grid.select(null);
            editingForm.setFilm(new Film());
        });

        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filtering.addComponents(filterText, clearFilterButton);

        HorizontalLayout toolBar = new HorizontalLayout(filtering, addNewFilmButton);
        toolBar.setSpacing(true);

        HorizontalLayout main = new HorizontalLayout();
        main.addComponents(grid, editingForm);
        addComponents(toolBar, main);
        main.setSpacing(true);
        main.setSizeFull();
        main.setExpandRatio(grid, 1);

        clearFilterButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                filterText.clear();
                updateList();
            }
        });

        setMargin(true);
        setSpacing(true);

        editingForm.setVisible(false);

        grid.addSelectionListener(event -> {
            if(event.getSelected().isEmpty()) {
                editingForm.setVisible(false);
            } else {
                Film film = (Film)event.getSelected().iterator().next();
                editingForm.setFilm(film);
            }
        });
    }

    public void updateList() {
        List<Film> films = service.findAll();
        grid.setContainerDataSource(new BeanItemContainer<>(Film.class, films));
    }
}
