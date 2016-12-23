package com.vorontsov.Design;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vorontsov.Base.*;
import com.vorontsov.MyUI;
import com.vorontsov.Services.InternetService;

import java.util.List;

public class MainForm extends VerticalLayout{
    private MyUI myUI;
    private FilmService service = FilmService.getInstance();
    private Grid grid = new Grid();
    private TextField filterText = new TextField();
    private TextField URLLink = new TextField();
    private Button clearFilter = new Button(FontAwesome.TIMES);
    private Button newFilm = new Button("Add new film");
    private Button addFromURL = new Button("Add film from kinopoisk.ru");
    private EditingForm editing;
    private CssLayout filtering = new CssLayout();
    private ComboBox recordsPages = new ComboBox();
    private ComboBox recordsOnPage = new ComboBox();
    private static int RECORDS_PER_PAGE = 5;

    Label infoRecordsLabel = new Label("записей на странице. Перейти к странице :");

    public MainForm(MyUI myUI) {
        this.myUI = myUI;

        filterText.setInputPrompt("filter by name...");
        filterText.addTextChangeListener(e -> grid.setContainerDataSource(new BeanItemContainer<>(Film.class, service.findAll(e.getText()))));
        clearFilter.addClickListener(e-> {
            filterText.clear();
            updateList();
        });
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filtering.addComponents(filterText, clearFilter);

        HorizontalLayout toolBar = new HorizontalLayout(filtering, newFilm);
        toolBar.setSpacing(true);
        newFilm.addClickListener(e -> {
            grid.select(null);
            editing.setFilm(new Film());
        });

        HorizontalLayout URLAddition = new HorizontalLayout(URLLink, addFromURL);
        URLAddition.setSpacing(true);
        URLLink.setInputPrompt("URL to kinopoisk film");
        addFromURL.setStyleName("primary");
        addFromURL.addClickListener(e -> {
            try {
                InternetService.saveFilmFromURL(URLLink.getValue());
            }
            catch (Exception ex) {
                Notification.show("Ошибка", "По данному URL не обнаружено фильма!", Notification.Type.ERROR_MESSAGE);
                return;
            }
            service.getDataFromDB();
            updateList();
            updateRecordsComboBox();
            URLLink.setValue("");
        });

        grid.setSizeFull();
        grid.setColumns("title", "genreName", "rating", "duration", "ageRestrictions");
        grid.addSelectionListener(event -> {
            if(event.getSelected().isEmpty()) {
                editing.setVisible(false);
            } else {
                Film film = (Film)event.getSelected().iterator().next();
                editing.setFilm(film);
            }
        });

        editing = new EditingForm(myUI, this);
        editing.setVisible(false);

        HorizontalLayout recordsLayout = new HorizontalLayout(recordsOnPage, infoRecordsLabel, recordsPages);
        recordsLayout.setSpacing(true);
        recordsLayout.setComponentAlignment(infoRecordsLabel, Alignment.MIDDLE_CENTER);
        recordsOnPage.addItems("5", "10", "25", "50", "100");
        recordsOnPage.setTextInputAllowed(false);
        recordsOnPage.setNullSelectionAllowed(false);
        recordsOnPage.setValue(recordsOnPage.getItemIds().iterator().next());
        updateRecordsComboBox();
        recordsPages.setTextInputAllowed(false);
        recordsPages.setNullSelectionAllowed(false);
        recordsPages.addValueChangeListener(e -> updateList());
        recordsOnPage.addValueChangeListener(e -> {
            RECORDS_PER_PAGE = Integer.parseInt(recordsOnPage.getValue().toString());
            updateList();
            updateRecordsComboBox();
        });

        HorizontalLayout main = new HorizontalLayout();
        main.addComponents(grid, editing);
        main.setSpacing(true);
        main.setSizeFull();
        main.setExpandRatio(grid, 1);

        updateList();

        setMargin(true);
        setSpacing(true);
        addComponents(toolBar, URLAddition, main, recordsLayout);
    }

    /*
     * Adding pages count to recordsComboBox
     * If there are zero in service.count() adds only one page
     * If there are anything in service.count() add as much pages as need.
     */
    public void updateRecordsComboBox() {
        recordsPages.removeAllItems();
        int c = ((int)service.count() / RECORDS_PER_PAGE) + ((service.count() % RECORDS_PER_PAGE) != 0 ? 1 : 0);
        if(c < 1)
            c = 1;
        for(int i = 1; i <= c; i++) {
            recordsPages.addItem(i);
        }
        recordsPages.setValue(recordsPages.getItemIds().iterator().next());
    }


    /*
     * Updates grid to show films.
     */
    public void updateList() {
        List<Film> films;
        if(recordsPages.getValue() != null) {
            int start_index = ((Integer.parseInt(recordsPages.getValue().toString()) - 1) * RECORDS_PER_PAGE );
            if(start_index < 0)
                start_index = 0;
            films = service.findAll(null, start_index, RECORDS_PER_PAGE);
        } else {
            films = service.findAll(null, 0, RECORDS_PER_PAGE);
        }
        grid.setContainerDataSource(new BeanItemContainer<>(Film.class, films));
    }
}
