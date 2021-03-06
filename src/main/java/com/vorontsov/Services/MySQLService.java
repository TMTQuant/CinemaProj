package com.vorontsov.Services;

import com.vaadin.ui.Notification;
import com.vorontsov.Base.Film;
import com.vorontsov.Base.Genre;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQLService {
    private static Connection connection = null;
    private static DataSource dataSource = null;
    private static Statement statement = null;
    private static InitialContext ic = null;
    private static ResultSet rs = null;
    private static ArrayList<Film> films;
    private static ArrayList<Genre> genres;
    private static final String dbname = "java:/cinemadb";

    /**
    Verify if base was already installed.
    Find tables users, films, genre
    @return true if all 3 tables are in use
    @return false if any of 3 tables are not created yet;
     */
    public static boolean isInstalled() {
        boolean installed = false;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            boolean isUsers;
            boolean isFilms;
            boolean isGenre;

            String query = "SHOW TABLES LIKE 'users'";
            rs = statement.executeQuery(query);
            if (rs.next()) {
                isUsers = true;
            } else isUsers = false;
            query = "SHOW TABLES LIKE 'films'";
            rs = statement.executeQuery(query);
            if (rs.next()) {
                isFilms = true;
            } else isFilms = false;
            query = "SHOW TABLES LIKE 'genre'";
            rs = statement.executeQuery(query);
            if (rs.next()) {
                isGenre = true;
            } else isGenre = false;

            installed = (isFilms && isGenre && isUsers);

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return installed;
        }
    }

    /**
    Create tables if not exists Films, Users, Genre
     */
    public static void setup() {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS `Films` (" +
                    "  `ID` int(10) NOT NULL AUTO_INCREMENT," +
                    "  `Title` varchar(30) DEFAULT NULL," +
                    "  `Duration` int(6) DEFAULT NULL," +
                    "  `ageRestrictions` int(2) DEFAULT NULL," +
                    "  `Rating` decimal(5,2) DEFAULT NULL," +
                    "  `GenreID` int(2) DEFAULT NULL," +
                    "  PRIMARY KEY (`ID`)," +
                    "  KEY `GenreID` (`GenreID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS `Users` (" +
                    "  `ID` int(10) NOT NULL AUTO_INCREMENT," +
                    "  `Login` varchar(20) DEFAULT NULL," +
                    "  `Salt` varchar(50) DEFAULT NULL," +
                    "  `Passwd` varchar(50) DEFAULT NULL," +
                    "  `privilege` varchar(50) DEFAULT NULL," +
                    "  PRIMARY KEY (`ID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS `Genre` (" +
                    "  `ID` int(10) NOT NULL AUTO_INCREMENT," +
                    "  `Name` varchar(20) DEFAULT NULL," +
                    "  PRIMARY KEY (`ID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
            statement.executeUpdate(query);
            statement.close();
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
    Create user with some password protection; Password as it is not inserted in database values, only encrypted string;
    @param username Username
     @param password password that will be encrypted
     */
    public static void createUser(String username, String password) {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users VALUES (NULL,  ?, ?, ?, ?);");

            String salt = SupportService.generateSaltString();
            String MD5Password = SupportService.getMD5(password);
            String comboMD5 = SupportService.getMD5(MD5Password + salt);

            ps.setString(1, username);
            ps.setString(2, salt);
            ps.setString(3, comboMD5);
            ps.setString(4, "Privilegies");
            ps.executeUpdate();

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
    Verify if user exists
    @param username username
     @return true if such user already in table Users
     @return false if not
     */
    public static boolean isUserExists(String username) {
        boolean haveUser = false;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE login = ?;");
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next())
                haveUser = true;
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return haveUser;
        }
    }

    /**
     * Method to get Films into ArrayList from table Films
     * Also get film`s genre name by genre id value;
     * @return list of films from table films
     */
    public static ArrayList<Film> getFilmsFromDB() {
        try {
            films = new ArrayList();
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT `Films`.*,`Genre`.`Name` AS `GenreName` FROM `Films` LEFT JOIN `Genre` ON `Films`.`GenreID`=`Genre`.`ID`;");
            rs = ps.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getLong("ID"));
                film.setTitle(rs.getString("Title"));
                film.setDuration(rs.getInt("Duration"));
                film.setAgeRestrictions(rs.getInt("ageRestrictions"));
                film.setRating(rs.getDouble("Rating"));
                film.setGenreID(rs.getLong("GenreID"));
                film.setGenreName(rs.getString("GenreName"));
                films.add(film);
            }

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return films;
        }
    }

    /**
     * Method to get Genres into ArrayList from table genre
     * @return list of genres from table genre
     */
    public static ArrayList<Genre> getGenresFromDB() {
        try {
            genres = new ArrayList();
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM genre;");
            rs = ps.executeQuery();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("ID"));
                genre.setName(rs.getString("Name"));
                genres.add(genre);
            }

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return genres;
        }
    }

    /**
     *
     * @param username user login
     * @param password user passwod
     * @return true if user with such username and password already in table users
     * @return false if not
     */
    public static boolean tryAuthorize(String username, String password) {
        boolean authorize = false;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE login = ?;");
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                String salt = rs.getString("Salt");
                String md5 = rs.getString("Passwd");

                if ((SupportService.getMD5((SupportService.getMD5(password)) + salt)).equals(md5))
                    authorize = true;
            }

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return authorize;
        }
    }

    /**
     * verify if film with such id already in table Films
     * @param id Film Id to be verified
     * @return true if film with such id already in table Films
     * @return false if not
     */
    public static boolean isFilmExists(Long id) {
        boolean haveFilm = false;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM films WHERE ID = ?;");
            ps.setString(1, "" + id);

            rs = ps.executeQuery();
            if (rs.next())
                haveFilm = true;
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            return haveFilm;
        }
    }

    /**
     * Save film to table Films
     * @param film film to be saved
     */
    public static void saveFilmToDB(Film film) {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement insertPs = connection.prepareStatement("INSERT INTO films VALUES (NULL, ?, ?, ?, ?, ?);");
            insertPs.setString(1, film.getTitle());
            insertPs.setString(2, "" + film.getDuration());
            insertPs.setString(3, "" + film.getAgeRestrictions());
            insertPs.setString(4, "" + film.getRating());
            insertPs.setString(5, "" + film.getGenreID());
            insertPs.executeUpdate();

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
     * Change film parametres in table Films
     * @param film to be changed
     */
    public static void changeFilmsInDB(Film film) {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement updatePs = connection.prepareStatement("UPDATE films SET Title = ?, Duration = ?, ageRestrictions = ?, Rating = ?, GenreID = ? WHERE ID = ?;");
            updatePs.setString(1, film.getTitle());
            updatePs.setString(2, "" + film.getDuration());
            updatePs.setString(3, "" + film.getAgeRestrictions());
            updatePs.setString(4, "" + film.getRating());
            updatePs.setString(5, "" + film.getGenreID());
            updatePs.setString(6, "" + film.getId());
            updatePs.executeUpdate();

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
     * Delete film from table Films
     * @param film to be deleted
     */
    public static void deleteFilmsFromDB(Film film) {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement deletePs = connection.prepareStatement("DELETE FROM films WHERE ID = ?;");
            deletePs.setString(1, "" + film.getId());
            deletePs.executeUpdate();

        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
     * Add genre names from kinopoisk to table genre
     */
    public static void addGenresToDB() {
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            String [] genres = new String[] {"Аниме", "Биография", "Боевик", "Вестерн", "Военный", "Детектив", "Детский", "Для взрослых", "Документальный",
            "Драма", "Игра", "История", "Комедия", "Концерт", "Короткометражка", "Криминал", "Мелодрама", "Музыка", "Мультфильм", "Мюзикл", "Новости",
            "Приключения", "Реальное ТВ", "Семейный", "Спорт", "Ток ШОУ", "Триллер", "Ужасы", "Фантастика", "Фильм-нуар", "Фэнтези", "Церемония"};
            for(String genre : genres) {
                PreparedStatement insertPs = connection.prepareStatement("INSERT INTO genre VALUES (NULL, ? )");
                insertPs.setString(1, genre);
                insertPs.executeUpdate();
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
        }
    }

    /**
     * Method to get genreId by genreName
     * @param genreName genre name to know genre id
     * @return genre id value what matches genre name
     */
    public static int getGenreIdByName(String genreName) {
        int genreId = 0;
        try {
            ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbname);
            connection = dataSource.getConnection();
            PreparedStatement selectPs = connection.prepareStatement("SELECT * FROM genre WHERE name = ?");
            selectPs.setString(1, genreName);
            rs = selectPs.executeQuery();
            if(rs.next()) {
                genreId = rs.getInt("ID");
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
                return genreId;
        }
    }
}
