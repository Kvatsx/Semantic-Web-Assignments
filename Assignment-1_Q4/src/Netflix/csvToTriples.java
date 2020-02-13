package Netflix;

import java.util.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.rdf.model.Model ;

public class csvToTriples {
    public Model model;
    public String properties_uri;
    public String node_url;
    public Property hasTitle, hasDirector, hasCast, countries, hasRating, listedIn, description, dataAdded, releaseYear, duration;

    public csvToTriples() {
        this.model = ModelFactory.createDefaultModel();
        this.properties_uri = "http://netflix.io/property/";
        this.node_url = "http://netflix.io/node/";
        this.hasTitle = this.model.createProperty(properties_uri, "hasTitle");
        this.hasDirector = this.model.createProperty(properties_uri, "hasDirector");
        this.hasCast = this.model.createProperty(properties_uri, "hasCast");
        this.countries = this.model.createProperty(properties_uri, "countries");
        this.hasRating = this.model.createProperty(properties_uri, "hasRating");
        this.listedIn = this.model.createProperty(properties_uri, "listedIn");
        this.description = this.model.createProperty(properties_uri, "description");
        this.dataAdded = this.model.createProperty(properties_uri, "dataAdded");
        this.releaseYear = this.model.createProperty(properties_uri, "releaseYear");
        this.duration = this.model.createProperty(properties_uri, "duration");
    }

    public void convertToTTL(String filepath) {
        csvNetflixParser csvNetflixParser = new csvNetflixParser();
        ArrayList<NetflixObject> Arr = csvNetflixParser.parse(filepath);
        List<String> temp;
        Resource person = this.model.createResource(node_url + "Person");
        person.addProperty(RDF.type, RDFS.Class);

        for (NetflixObject e : Arr) {
            Resource movie = this.model.createResource(node_url + e.getShow_id());
            movie.addProperty(this.hasTitle, e.getTitle());
            this.hasTitle.addProperty(RDF.type, RDF.Property);
            this.hasTitle.addProperty(RDFS.domain, movie);
            this.hasTitle.addProperty(RDFS.range, RDFS.Literal);

            // Type
            Resource rType = this.model.createResource(node_url + createURI(e.getType()));
            rType.addProperty(RDFS.label, e.getType());
            rType.addProperty(RDF.type, RDFS.Class);
            movie.addProperty(RDF.type, rType);

            movie.addProperty(this.dataAdded, e.getDate_added());
            this.dataAdded.addProperty(RDF.type, RDF.Property);
            this.dataAdded.addProperty(RDFS.domain, movie);
            this.dataAdded.addProperty(RDFS.range, RDFS.Literal);


            movie.addProperty(this.releaseYear, e.getRelease_year());
            this.releaseYear.addProperty(RDF.type, RDF.Property);
            this.releaseYear.addProperty(RDFS.domain, movie);
            this.releaseYear.addProperty(RDFS.range, RDFS.Literal);

            movie.addProperty(this.duration, e.getDuration());
            this.duration.addProperty(RDF.type, RDF.Property);
            this.duration.addProperty(RDFS.domain, movie);
            this.duration.addProperty(RDFS.range, RDFS.Literal);

            movie.addProperty(this.description, e.getDescription());
            this.description.addProperty(RDF.type, RDF.Property);
            this.description.addProperty(RDFS.domain, movie);
            this.description.addProperty(RDFS.range, RDFS.Literal);

            movie.addProperty(this.hasRating, e.getRating());
            this.hasRating.addProperty(RDF.type, RDF.Property);
            this.hasRating.addProperty(RDFS.domain, movie);
            this.hasRating.addProperty(RDFS.range, RDFS.Literal);


            temp = e.getListed_in();
            for (int i=0; i<temp.size(); i++) {
                movie.addProperty(this.listedIn, temp.get(i));
            }
            this.listedIn.addProperty(RDF.type, RDF.Property);
            this.listedIn.addProperty(RDFS.domain, movie);
            this.listedIn.addProperty(RDFS.range, RDFS.Literal);

            temp = e.getDirectors();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                Resource rUser = this.model.createResource(node_url + createURI(temp.get(i)));
                rUser.addProperty(RDFS.label, temp.get(i));
                rUser.addProperty(RDF.type, person);
                movie.addProperty(this.hasDirector, rUser);
            }
            this.hasDirector.addProperty(RDF.type, RDF.Property);
            this.hasDirector.addProperty(RDFS.domain, movie);
            this.hasDirector.addProperty(RDFS.range, RDFS.Literal);

            temp = e.getCast();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                Resource rUser = this.model.createResource(node_url + createURI(temp.get(i)));
                rUser.addProperty(RDFS.label, temp.get(i));
                rUser.addProperty(RDF.type, person);
                movie.addProperty(this.hasCast, rUser);
            }
            this.hasCast.addProperty(RDF.type, RDF.Property);
            this.hasCast.addProperty(RDFS.domain, movie);
            this.hasCast.addProperty(RDFS.range, person);

            temp = e.getCountries();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
//                movie.addProperty(this.countries, this.model.createResource(node_url + createURI(temp.get(i))));
                movie.addProperty(this.countries, temp.get(i));
            }
            this.countries.addProperty(RDF.type, RDF.Property);
            this.countries.addProperty(RDFS.domain, movie);
            this.countries.addProperty(RDFS.range, RDFS.Literal);
        }
        this.model.setNsPrefix("node", this.node_url);
        this.model.setNsPrefix("property", this.properties_uri);
        this.model.write(System.out, "TTL");
    }

    public String createURI(String words) {
        words = words.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        String[] Arr = words.split(" ");
        String result = Arr[0];
        if (Arr.length > 1) {
            for (int i=1; i<Arr.length; i++) {
                result += "_" + Arr[i];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String filepath = "Data/NetflixList.csv";
        csvToTriples  q4 = new csvToTriples();
        q4.convertToTTL(filepath);
    }
}
