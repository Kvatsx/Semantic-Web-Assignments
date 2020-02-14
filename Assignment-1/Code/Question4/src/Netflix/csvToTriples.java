package Netflix;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.rdf.model.Model ;

public class csvToTriples {
    public Model model;
    public String properties_uri;
    public String node_url;
    public String rdf, rdfProperty;
    public Property hasTitle, hasDirector, hasCast, countries, hasRating, listedIn, description, dateAdded, releaseYear, duration;
    public Resource person;

    public csvToTriples() {
        this.model = ModelFactory.createDefaultModel();
        this.properties_uri = "http://netflix.io/property/";
        this.node_url = "http://netflix.io/node/";
        this.rdf = "http://www.w3.org/2000/01/rdf-schema#";
        this.rdfProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        this.person = this.model.createResource(node_url + "Person");
        this.person.addProperty(RDF.type, RDFS.Class);

        this.hasTitle = this.model.createProperty(properties_uri, "hasTitle");
        this.hasTitle.addProperty(RDF.type, RDF.Property);
        this.hasTitle.addProperty(RDFS.domain, RDFS.Resource);
        this.hasTitle.addProperty(RDFS.range, RDFS.Literal);

        this.hasDirector = this.model.createProperty(properties_uri, "hasDirector");
        this.hasDirector.addProperty(RDF.type, RDF.Property);
        this.hasDirector.addProperty(RDFS.domain, RDFS.Resource);
        this.hasDirector.addProperty(RDFS.range, this.person);

        this.hasCast = this.model.createProperty(properties_uri, "hasCast");
        this.hasCast.addProperty(RDF.type, RDF.Property);
        this.hasCast.addProperty(RDFS.domain, RDFS.Resource);
        this.hasCast.addProperty(RDFS.range, this.person);

        this.countries = this.model.createProperty(properties_uri, "countries");
        this.countries.addProperty(RDF.type, RDF.Property);
        this.countries.addProperty(RDFS.domain, RDFS.Resource);
        this.countries.addProperty(RDFS.range, RDFS.Literal);

        this.hasRating = this.model.createProperty(properties_uri, "hasRating");
        this.hasRating.addProperty(RDF.type, RDF.Property);
        this.hasRating.addProperty(RDFS.domain, RDFS.Resource);
        this.hasRating.addProperty(RDFS.range, RDFS.Literal);

        this.listedIn = this.model.createProperty(properties_uri, "listedIn");
        this.listedIn.addProperty(RDF.type, RDF.Property);
        this.listedIn.addProperty(RDFS.domain, RDFS.Resource);
        this.listedIn.addProperty(RDFS.range, RDFS.Literal);

        this.description = this.model.createProperty(properties_uri, "description");
        this.description.addProperty(RDF.type, RDF.Property);
        this.description.addProperty(RDFS.domain, RDFS.Resource);
        this.description.addProperty(RDFS.range, RDFS.Literal);

        this.dateAdded = this.model.createProperty(properties_uri, "dateAdded");
        this.dateAdded.addProperty(RDF.type, RDF.Property);
        this.dateAdded.addProperty(RDFS.domain, RDFS.Resource);
        this.dateAdded.addProperty(RDFS.range, RDFS.Literal);

        this.releaseYear = this.model.createProperty(properties_uri, "releaseYear");
        this.releaseYear.addProperty(RDF.type, RDF.Property);
        this.releaseYear.addProperty(RDFS.domain, RDFS.Resource);
        this.releaseYear.addProperty(RDFS.range, RDFS.Literal);

        this.duration = this.model.createProperty(properties_uri, "duration");
        this.duration.addProperty(RDF.type, RDF.Property);
        this.duration.addProperty(RDFS.domain, RDFS.Resource);
        this.duration.addProperty(RDFS.range, RDFS.Literal);
    }

    public void convertToTTL(String filepath) throws IOException {
        csvNetflixParser csvNetflixParser = new csvNetflixParser();
        ArrayList<NetflixObject> Arr = csvNetflixParser.parse(filepath);
        List<String> temp;


        for (NetflixObject e : Arr) {
            Resource movie = this.model.createResource(node_url + e.getShow_id());

            // Type
            Resource rType = this.model.createResource(node_url + createURI(e.getType()));
            rType.addProperty(RDFS.label, e.getType());
            rType.addProperty(RDF.type, RDFS.Class);
            movie.addProperty(RDF.type, rType);

            movie.addProperty(this.hasTitle, e.getTitle());
            movie.addProperty(this.dateAdded, e.getDate_added());
            movie.addProperty(this.releaseYear, e.getRelease_year());
            movie.addProperty(this.duration, e.getDuration());
            movie.addProperty(this.description, e.getDescription());
            movie.addProperty(this.hasRating, e.getRating());

            temp = e.getListed_in();
            for (int i=0; i<temp.size(); i++) {
                movie.addProperty(this.listedIn, temp.get(i));
            }

            temp = e.getDirectors();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                Resource rUser = this.model.createResource(node_url + createURI(temp.get(i)));
                rUser.addProperty(RDFS.label, temp.get(i));
                rUser.addProperty(RDF.type, RDFS.Resource);
                movie.addProperty(this.hasDirector, rUser);
            }

            temp = e.getCast();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                Resource rUser = this.model.createResource(node_url + createURI(temp.get(i)));
                rUser.addProperty(RDFS.label, temp.get(i));
                rUser.addProperty(RDF.type, RDFS.Resource);
                movie.addProperty(this.hasCast, rUser);
            }

            temp = e.getCountries();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                movie.addProperty(this.countries, temp.get(i));
            }
        }
        this.model.setNsPrefix("node", this.node_url);
        this.model.setNsPrefix("property", this.properties_uri);
        this.model.setNsPrefix("rdf", this.rdf);
        this.model.setNsPrefix("rdfProperty", this.rdfProperty);
        Writer out = new OutputStreamWriter(new FileOutputStream("output.ttl"), StandardCharsets.UTF_8);
        this.model.write(out, "TTL");
        out.close();
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

    public static void main(String[] args) throws IOException {
        BufferedReader Reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter NetflixList.csv Relative or Absolute Path: ");
        String filepath = Reader.readLine();
        csvToTriples  q4 = new csvToTriples();
        q4.convertToTTL(filepath);
        System.out.println("Output stored in output.ttl");
    }
}
