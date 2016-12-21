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
    private TextField URLTextField = new TextField();
    private Button clearFilterButton = new Button(FontAwesome.TIMES);
    private Button addNewFilmButton = new Button("Add new film");
    private Button addFromURLButton = new Button("Add film from kinopoisk");
    private EditingForm editingForm;
    private CssLayout filtering = new CssLayout();
    private ComboBox recordsOnPageComboBox = new ComboBox();
    public static final int RECORDS_PER_PAGE = 5;
    Label infoRecordsLabel = new Label("На странице : " + RECORDS_PER_PAGE + ". Перейти к странице : ");

    public MainForm(MyUI myUI) {
        this.myUI = myUI;
        editingForm = new EditingForm(myUI, this);
        grid.setColumns("title", "genreName", "rating", "duration", "ageRestrictions");
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

        HorizontalLayout recordsLayout = new HorizontalLayout(infoRecordsLabel, recordsOnPageComboBox);
        recordsLayout.setSpacing(true);
        recordsLayout.setComponentAlignment(infoRecordsLabel, Alignment.MIDDLE_CENTER);

        updateRecordsComboBox();

        recordsOnPageComboBox.setTextInputAllowed(false);
        recordsOnPageComboBox.setNullSelectionAllowed(false);
        recordsOnPageComboBox.setValue(recordsOnPageComboBox.getItemIds().iterator().next());

        HorizontalLayout addFilmLayout = new HorizontalLayout(URLTextField, addFromURLButton);
        addFilmLayout.setSpacing(true);
        URLTextField.setInputPrompt("URL to kinopoisk film");


        HorizontalLayout main = new HorizontalLayout();
        main.addComponents(grid, editingForm);
        addComponents(toolBar, addFilmLayout, main, recordsLayout);
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

        addFromURLButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(!(InternetService.saveFilmFromURL(URLTextField.getValue()))) {
                    Notification.show("Ошибка", "Неверно введен URL к фильму!", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                service.getDataFromDB();
                updateList();
                updateRecordsComboBox();
                URLTextField.setValue("");
            }
        });

        recordsOnPageComboBox.addValueChangeListener(e -> updateList());
    }

    public void updateRecordsComboBox() {
        recordsOnPageComboBox.removeAllItems();
        int i = 1;
        for(i = 1; i <= service.count() / RECORDS_PER_PAGE; i++) {
            recordsOnPageComboBox.addItem(i);
        }
        if((int)service.count() % RECORDS_PER_PAGE != 0)
            recordsOnPageComboBox.addItem(i);
        recordsOnPageComboBox.setValue(recordsOnPageComboBox.getItemIds().iterator().next());
    }

    public void updateList() {
        List<Film> films;
        if(recordsOnPageComboBox.getValue() != null) {
            films = service.findAll(null, -RECORDS_PER_PAGE + (Integer.parseInt(recordsOnPageComboBox.getValue().toString()) * RECORDS_PER_PAGE), RECORDS_PER_PAGE);
        } else {
            films = service.findAll(null, 0, RECORDS_PER_PAGE);
        }
        grid.setContainerDataSource(new BeanItemContainer<>(Film.class, films));
    }
}
