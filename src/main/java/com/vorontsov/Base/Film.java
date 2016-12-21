package com.vorontsov.Base;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Film implements Serializable, Cloneable {
    private Long id;
    private String title;
    private int duration;
    private int ageRestrictions;
    private double rating;
    private long genreID;
    private String genreName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title
     *            new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the value of duration
     *
     * @return the value of duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the value of duration
     *
     * @param duration
     *            new value of duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get the value of age restrictions
     *
     * @return the value of age restrictions
     */
    public int getAgeRestrictions() {
        return ageRestrictions;
    }

    /**
     * Set the value of ageRestrictions
     *
     * @param ageRestrictions
     *            new value of ageRestrictions
     */
    public void setAgeRestrictions(int ageRestrictions) {
        this.ageRestrictions = ageRestrictions;
    }

    /**
     * Get the value of rating
     *
     * @return the value of rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Set the value of rating
     *
     * @param rating
     *            new value of rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Get the value of genreID
     *
     * @return the value of genreID
     */
    public long getGenreID() {
        return genreID;
    }

    /**
     * Set the value of genreID
     *
     * @param genreID
     *            new value of genreID
     */
    public void setGenreID(long genreID) {
        this.genreID = genreID;
    }

    /**
     * Get the value of genreName
     *
     * @return the value of genreName
     */
    public String getGenreName() {
        return genreName;
    }

    /**
     * Set the value of genreName
     *
     * @param genreName
     *            new value of genreName
     */
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.id == null) {
            return false;
        }
        if (obj instanceof Film && obj.getClass().equals(getClass())) {
            return this.id.equals(((Film) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (id == null ? 0 : id.hashCode());
        return hash;
    }

    @Override
    public Film clone() throws CloneNotSupportedException {
        return (Film) super.clone();
    }

    @Override
    public String toString() {
        return title;
    }
}
