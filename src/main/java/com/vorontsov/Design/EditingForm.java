package com.vorontsov.Design;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
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
    private TextField genreId = new TextField("Genre id");
    private ComboBox genreNames = new ComboBox("Genre name :");
    private  TextField rating = new TextField("Rating");
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");

    private FilmService service = FilmService.getInstance();
    private Film film;
    private MyUI myUI;
    private MainForm mainForm;

    public EditingForm(MyUI myUI, MainForm mainForm) {
        this.myUI = myUI;
        this.mainForm = mainForm;

        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        ArrayList<Genre> genres = new ArrayList<>();
        genres = MySQLService.getGenresFromDB();
        for(Genre g : genres) {
            genreNames.addItem(g.getName());
        }
        genreNames.setInputPrompt("No genre selected");
        genreNames.setTextInputAllowed(false);
        genreNames.select(10);

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton);
        buttons.setSpacing(true);
        addComponents(title, duration, ageRestrictions, genreNames, rating, buttons);
        // genre id delete from addComponents

        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());
    }

    public void setFilm(Film film) {
        this.film = film;
        genreNames.select(film.getGenreID());
        BeanFieldGroup.bindFieldsUnbuffered(film, this);
        deleteButton.setVisible(film.isPersisted());
        setVisible(true);
        title.selectAll();
    }

    private void save() {
        if(genreNames.getValue() != null) {
            if (MySQLService.isFilmExists(film.getId())) {
                service.changeFilmInDB(film, genreNames.getValue().toString());
            } else service.saveFilmToDB(film, genreNames.getValue().toString());
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
        setVisible(false);
    }
}
