package com.vorontsov.Services;

import com.vorontsov.Base.Film;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class InternetService {
    public static boolean saveFilmFromURL(String URL) {
        Film film = new Film();
        String title = "";
        String genreName = "";
        String ageRestrictionsS = "";
        int ageRestrictions = 0;
        String ratingS = "";
        double rating = 0;
        String durationS = "";
        int duration = 0;

        try {
            Document doc = Jsoup.connect(URL).userAgent("Mozilla").get();
            title = doc.select("#viewFilmInfoWrapper #photoInfoTable #headerFilm h1").text();

            genreName = doc.select("[itemprop=genre]").text();
            genreName = genreName.split(",")[0].substring(0, 1).toUpperCase() + genreName.split(",")[0].substring(1);

            ageRestrictionsS = doc.select(".ratePopup span").text();
            ageRestrictionsS = ageRestrictionsS.split(" ")[2];
            ageRestrictions = Integer.parseInt(ageRestrictionsS);

            ratingS = doc.select("#block_rating").text();
            ratingS = ratingS.split(" ")[0];
            rating = Double.parseDouble(ratingS);

            durationS = doc.select("#runtime").text();
            durationS = durationS.split(" ")[0];
            duration = Integer.parseInt(durationS);
        }
        catch(IOException ex){
            ex.printStackTrace();
            return false;
        }

        film.setTitle(title);
        film.setDuration(duration);
        film.setAgeRestrictions(ageRestrictions);
        film.setRating(rating);
        film.setGenreName(genreName);
        film.setGenreID(MySQLService.getGenreIdByName(genreName));
        MySQLService.saveFilmToDB(film);
        return true;
    }
}
