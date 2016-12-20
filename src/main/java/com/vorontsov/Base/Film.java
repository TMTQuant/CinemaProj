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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAgeRestrictions() {
        return ageRestrictions;
    }
    public void setAgeRestrictions(int ageRestrictions) {
        this.ageRestrictions = ageRestrictions;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getGenreID() {
        return genreID;
    }
    public void setGenreID(long genreID) {
        this.genreID = genreID;
    }

    public String getGenreName() {
        return genreName;
    }
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
