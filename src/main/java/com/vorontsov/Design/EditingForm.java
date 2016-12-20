package com.vorontsov.Design;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vorontsov.Base.Film;
import com.vorontsov.Base.FilmService;
import com.vorontsov.MyUI;
import com.vorontsov.Services.MySQLService;

public class EditingForm extends FormLayout {
    private TextField title = new TextField("Title");
    private TextField duration = new TextField("Duration");
    private TextField ageRestrictions = new TextField("ageRestrictions");
    private TextField genreId = new TextField("Genre id");
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

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton);
        buttons.setSpacing(true);
        addComponents(title, duration, ageRestrictions, genreId, rating, buttons);

        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());
    }

    public void setFilm(Film film) {
        this.film = film;
        BeanFieldGroup.bindFieldsUnbuffered(film, this);

        deleteButton.setVisible(film.isPersisted());
        setVisible(true);
        title.selectAll();
    }

    private void save() {
        if(MySQLService.isFilmExists(film.getId())) {
            service.changeFilmInDB(film);
        } else service.saveFilmToDB(film);
        service.getDataFromDB();
        mainForm.updateList();
        setVisible(false);
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
