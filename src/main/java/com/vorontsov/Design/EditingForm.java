package com.vorontsov.Design;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vorontsov.Base.Film;
import com.vorontsov.Base.FilmService;
import com.vorontsov.Base.Genre;
import com.vorontsov.MyUI;
import com.vorontsov.Services.MySQLService;

import java.util.ArrayList;

public class EditingForm extends FormLayout {
    private TextField title = new TextField("Title");
    private TextField duration = new TextField("Duration");
    private TextField ageRestrictions = new TextField("ageRestrictions");
    private ComboBox genreName = new ComboBox("Genre name :");
    private  TextField rating = new TextField("Rating");
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private MyUI myUI;
    private MainForm mainForm;
    private Film film;
    private FilmService service = FilmService.getInstance();


    public EditingForm(MyUI myUI, MainForm mainForm) {
        this.myUI = myUI;
        this.mainForm = mainForm;

        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        ArrayList<Genre> genres = new ArrayList<>();
        genres = MySQLService.getGenresFromDB();
        for(Genre g : genres) {
            genreName.addItem(g.getName());
        }
        genreName.setInputPrompt("No genre selected");
        genreName.setTextInputAllowed(false);
        genreName.select(10);

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton);
        buttons.setSpacing(true);
        addComponents(title, duration, ageRestrictions, genreName, rating, buttons);

        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());
    }

    public void setFilm(Film film) {
        this.film = film;
        genreName.setValue(0);
        BeanFieldGroup.bindFieldsUnbuffered(film, this);
        if(title.getValue() == null)
            title.setValue("");
        deleteButton.setVisible(film.isPersisted());
        setVisible(true);
        title.selectAll();
    }

    private void save() {
        if(genreName.getValue() != null) {
            if (MySQLService.isFilmExists(film.getId())) {
                service.changeFilmInDB(film, genreName.getValue().toString());
            } else {
                service.saveFilmToDB(film, genreName.getValue().toString());
                mainForm.updateRecordsComboBox();
            }
            service.getDataFromDB();
            mainForm.updateList();
            setVisible(false);
        }
        else Notification.show("Ошибка", "Выберите жанр", Notification.Type.ERROR_MESSAGE);
    }

    private void delete() {
        if(MySQLService.isFilmExists(film.getId())) {
            service.deleteFilmFromDB(film);
        }
        service.getDataFromDB();
        mainForm.updateList();
        mainForm.updateRecordsComboBox();
        setVisible(false);
    }
}
