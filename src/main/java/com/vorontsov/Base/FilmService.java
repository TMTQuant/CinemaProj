package com.vorontsov.Base;



import com.vorontsov.Services.MySQLService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmService {
    private static FilmService instance;
    private static final Logger LOGGER = Logger.getLogger(FilmService.class.getName());

    private final HashMap<Long, Film> films = new HashMap<>();
    private final ArrayList<Film> filmsDelete = new ArrayList<>();
    private final ArrayList<Film> filmsChange = new ArrayList<>();
    private long nextId = 0;

    private FilmService() {
    }

    public static FilmService getInstance() {
        if (instance == null) {
            instance = new FilmService();
            instance.getDataFromDB();
        }
        return instance;
    }

    public synchronized List<Film> findAll() {
        return findAll(null);
    }

    public synchronized List<Film> findAll(String stringFilter) {
        ArrayList<Film> arrayList = new ArrayList<>();
        for (Film film : films.values()) {
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || film.toString().toLowerCase().contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(film.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(FilmService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(arrayList, new Comparator<Film>() {

            @Override
            public int compare(Film o1, Film o2) {
                return (int) (o2.getId() - o1.getId());
            }
        });
        return arrayList;
    }

    public synchronized long count() {
        return films.size();
    }

    public synchronized void delete(Film value) {
        films.remove(value.getId());
    }

    public synchronized void save(Film entry) {
        if (entry == null) {
            LOGGER.log(Level.SEVERE,
                    "Customer is null.");
            return;
        }
        if (entry.getId() == null) {
            entry.setId(nextId++);
        }
        films.put(entry.getId(), entry);
    }

    public void getDataFromDB() {
        films.clear();
        final ArrayList<Film> films = MySQLService.getFilmsFromDb();
        for (Film film : films) {
            save(film);
        }
    }

    public void saveFilmToDB(Film f) {
        MySQLService.saveFilmToDB(f);
    }

    public void changeFilmInDB(Film f) {
        MySQLService.changeFilmsInDB(f);
    }

    public void deleteFilmFromDB(Film f) {
        MySQLService.deleteFilmsFromDB(f);
    }
}
