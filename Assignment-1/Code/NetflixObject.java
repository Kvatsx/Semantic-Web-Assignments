package Netflix;

import java.util.Arrays;
import java.util.List;

public class NetflixObject {
    public String show_id;
    public String type;
    public String title;
    public List<String> directors;
    public List<String> cast;
    public List<String> countries;
    public String date_added;
    public String release_year;
    public String rating;
    public String duration;
    public List<String> listed_in;
    public String description;

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirectors(String[] directors) {
        this.directors = Arrays.asList(directors);
    }

    public void setCast(String[] cast) {
        this.cast = Arrays.asList(cast);
    }

    public void setCountries(String[] countries) {
        this.countries = Arrays.asList(countries);
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public void setRelease_year(String release_year) {
        this.release_year = release_year;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setListed_in(String[] listed_in) {
        this.listed_in = Arrays.asList(listed_in);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShow_id() {
        return show_id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getCast() {
        return cast;
    }

    public List<String> getCountries() {
        return countries;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getRelease_year() {
        return release_year;
    }

    public String getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

    public List<String> getListed_in() {
        return listed_in;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "NetflixObject{" +
                "show_id='" + show_id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", directors=" + directors +
                ", cast=" + cast +
                ", countries=" + countries +
                ", date_added='" + date_added + '\'' +
                ", release_year='" + release_year + '\'' +
                ", rating='" + rating + '\'' +
                ", duration='" + duration + '\'' +
                ", listed_in='" + listed_in + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
