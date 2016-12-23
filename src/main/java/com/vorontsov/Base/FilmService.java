package com.vorontsov.Base;



import com.vorontsov.Services.MySQLService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmService {
    private static FilmService instance;
    private static final Logger LOGGER = Logger.getLogger(FilmService.class.getName());
    private final HashMap<Long, Film> films = new HashMap<>();
    private static ArrayList<Genre> genres = new ArrayList<>();
    private long nextId = 0;

    private FilmService() {
    }

    /**
     * @return a reference to an example facade for Film objects.
     */
    public static FilmService getInstance() {
        if (instance == null) {
            instance = new FilmService();
            instance.getDataFromDB();
        }
        return instance;
    }

    /**
     * @return all available Film objects.
     */
    public synchronized List<Film> findAll() {
        return findAll(null);
    }

    /**
     * Finds all Film's that match given filter.
     *
     * @param stringFilter
     *            filter that returned objects should match or null/empty string
     *            if all objects should be returned.
     * @return list a Film objects
     */
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

    /**
     * Finds all Flm's that match given filter and limits the resultset.
     *
     * @param stringFilter
     *            filter that returned objects should match or null/empty string
     *            if all objects should be returned.
     * @param start
     *            the index of first result
     * @param maxresults
     *            maximum result count
     * @return list a Film objects
     */
    public synchronized List<Film> findAll(String stringFilter, int start, int maxresults) {
        ArrayList<Film> arrayList = new ArrayList<>();
        for (Film contact : films.values()) {
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(contact.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(Film.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(arrayList, new Comparator<Film>() {

            @Override
            public int compare(Film o1, Film o2) {
                return (int) (o2.getId() - o1.getId());
            }
        });
        int end = start + maxresults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        if(start > end) {
            start = end;
        }
        return arrayList.subList(start, end);
    }

    /**
     * @return the amount of all films in the system
     */
    public synchronized long count() {
        return films.size();
    }

    /**
     * Deletes a film from a system
     *
     * @param value the Film to be deleted
     */
    public synchronized void delete(Film value) {
        films.remove(value.getId());
    }

    /**
     * Persists or updates film in the system. Also assigns an identifier
     * for new Film instances.
     *
     * @param entry
     */
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

    /**
     * Add all avalible films from database to films
     */
    public void getDataFromDB() {
        genres = MySQLService.getGenresFromDB();
        films.clear();
        final ArrayList<Film> films = MySQLService.getFilmsFromDB();
        for (Film film : films) {
            save(film);
        }
    }

    /**
     * Persists film in the Database
     *
     * @param f film object to save
     * @param genreName films`s genreName to setup genreId field
     */
    public void saveFilmToDB(Film f, String genreName) {
        f.setGenreID(MySQLService.getGenreIdByName(genreName));
        MySQLService.saveFilmToDB(f);
        save(f);
    }


    /**
     * Updates film in the Database
     *
     * @param f film object to save
     * @param genreName films`s genreName to setup genreId field
     */
    public void changeFilmInDB(Film f, String genreName) {
        f.setGenreID(MySQLService.getGenreIdByName(genreName));
        MySQLService.changeFilmsInDB(f);
        save(f);
    }

    /**
     * Deletes a film from a system
     *
     * @param f the Film to be deleted
     */
    public void deleteFilmFromDB(Film f) {
        MySQLService.deleteFilmsFromDB(f);
    }
}
