package com.vorontsov.Design;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vorontsov.Base.*;
import com.vorontsov.MyUI;
import com.vorontsov.Services.MySQLService;

import java.util.ArrayList;

public class EditingForm extends FormLayout {
    private TextField title = new TextField("Title");
    private TextField duration = new TextField("Duration");
    private TextField ageRestrictions = new TextField("ageRestrictions");
    private ComboBox genreName = new ComboBox("Genre name :");
    private TextField rating = new TextField("Rating");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private MyUI myUI;
    private MainForm main;
    private Film film;
    private FilmService service = FilmService.getInstance();


    public EditingForm(MyUI myUI, MainForm main) {
        this.myUI = myUI;
        this.main = main;

        ArrayList<Genre> genres = MySQLService.getGenresFromDB();
        for(Genre g : genres) {
            genreName.addItem(g.getName());
        }
        genreName.setInputPrompt("No genre selected");
        genreName.setTextInputAllowed(false);
        genreName.select(10);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> {
            if(genreName.getValue() == null) {
                Notification.show("Ошибка", "Выберите жанр", Notification.Type.ERROR_MESSAGE);
                return;
            }
            save();
        });
        delete.addClickListener(e -> delete());
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        buttons.setSpacing(true);

        setSizeUndefined();
        addComponents(title, duration, ageRestrictions, genreName, rating, buttons);
    }

    /*
     * Load Film fields values to editing fields in form
     */
    public void setFilm(Film film) {
        this.film = film;
        BeanFieldGroup.bindFieldsUnbuffered(film, this);
        if(title.getValue() == null)
            title.setValue("");
        delete.setVisible(film.isPersisted());
        setVisible(true);
        title.selectAll();
    }

    /**
     * If film exists in database - change values and update list
     * If film new to database - insert new record to database, update list and recordsComboBox
     * After that editing form disappears.
     */
    private void save() {
        if (MySQLService.isFilmExists(film.getId())) {
            service.changeFilmInDB(film, genreName.getValue().toString());
        } else {
            service.saveFilmToDB(film, genreName.getValue().toString());
            main.updateRecordsComboBox();
        }
        service.getDataFromDB();
        main.updateList();
        setVisible(false);
    }

    /**
     * If film exists in database - delete record from database, update list and recordsComboBox
     * After that editing form disappears.
     */
    private void delete() {
        if(MySQLService.isFilmExists(film.getId())) {
            service.deleteFilmFromDB(film);
        }
        service.getDataFromDB();
        main.updateList();
        main.updateRecordsComboBox();
        setVisible(false);
    }
}