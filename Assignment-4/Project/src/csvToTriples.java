import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

public class csvToTriples {
    public OntModel model;
    public String baseURI;
    public String rdf, rdfProperty;
    public ObjectProperty hasCountry, hasDuration, hasGenre, hasDirectingRole, hasActingRole, hasActor, hasDirector, hasTimePeriodMeasurementUnit;
    public DatatypeProperty hasTitle, hasRating, hasDescription, hasShowId, dateAdded, hasUnit, releasedIn, timePeriodValue;
    public OntClass Country, Genre, NetflixContent, Movie, TvSeries, Actor, Director, TimePeriod, TimePeriodMeasurementUnit;
    public Individual acting, directing;

    public csvToTriples(String filename) {
        createOntoModel(filename);
        this.baseURI = "http://www.semanticweb.org/kvats/ontologies/2020/3/Netflix#";
        this.rdf = "http://www.w3.org/2000/01/rdf-schema#";
        this.rdfProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        // Classes
        Actor = this.model.getOntClass(this.baseURI + "Actor");
        Country = this.model.getOntClass(this.baseURI + "Country");
        Director = this.model.getOntClass(this.baseURI + "Director");
        Genre = this.model.getOntClass(this.baseURI + "Genre");
        Movie = this.model.getOntClass(this.baseURI + "Movie");
        NetflixContent = this.model.getOntClass(this.baseURI + "NetflixContent");
        TvSeries = this.model.getOntClass(this.baseURI + "TvSeries");
        TimePeriod = this.model.getOntClass("http://www.ontologydesignpatterns.org/cp/owl/timeperiod.owl#TimePeriod");
        TimePeriodMeasurementUnit = this.model.getOntClass("http://www.ontologydesignpatterns.org/cp/owl/timeperiod.owl#TimePeriodMeasurementUnit");

        // Object Property
        hasCountry = this.model.getObjectProperty(this.baseURI + "hasCountry");
        hasDuration = this.model.getObjectProperty(this.baseURI + "hasDuration");
        hasGenre = this.model.getObjectProperty(this.baseURI + "hasGenre");
        hasActingRole = this.model.getObjectProperty(this.baseURI + "hasActingRole");
        hasDirectingRole = this.model.getObjectProperty(this.baseURI + "hasDirectingRole");
        hasActor = this.model.getObjectProperty(this.baseURI + "hasActor");
        hasDirector = this.model.getObjectProperty(this.baseURI + "hasDirector");
        hasTimePeriodMeasurementUnit = this.model.getObjectProperty("http://www.ontologydesignpatterns.org/cp/owl/timeperiod.owl#hasTimePeriodMeasurementUnit");

        // Data Property
        dateAdded = this.model.getDatatypeProperty(this.baseURI + "dateAdded");
        hasDescription = this.model.getDatatypeProperty(this.baseURI + "hasDescription");
        hasRating = this.model.getDatatypeProperty(this.baseURI + "hasRating");
        hasShowId = this.model.getDatatypeProperty(this.baseURI + "hasShowId");
        hasTitle = this.model.getDatatypeProperty(this.baseURI + "hasTitle");
        hasUnit = this.model.getDatatypeProperty(this.baseURI + "hasUnit");
        releasedIn = this.model.getDatatypeProperty(this.baseURI + "releasedIn");
        timePeriodValue = this.model.getDatatypeProperty("http://www.ontologydesignpatterns.org/cp/owl/timeperiod.owl#timePeriodValue");

        // Individuals
        acting = this.model.getIndividual(this.baseURI + "acting");
        directing = this.model.getIndividual(this.baseURI + "directing");
    }

    public void createOntoModel(String ontofile) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontofile);
            try {
                this.model.read(in, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
    }

    public void convertToTTL(String filepath) throws IOException {
        csvNetflixParser csvNetflixParser = new csvNetflixParser();
        ArrayList<NetflixObject> Arr = csvNetflixParser.parse(filepath);
        List<String> temp;

        for (NetflixObject e : Arr) {
            Individual netflixObject;
            if (e.getType().equals("Movie")) {
                netflixObject = this.model.createIndividual(this.baseURI + e.getShow_id(), Movie);
            }
            else {
                netflixObject = this.model.createIndividual(this.baseURI + e.getShow_id(), TvSeries);
            }
            temp = e.getListed_in();
            for (int i=0; i<temp.size(); i++) {
                this.model.add(netflixObject, hasGenre, this.model.createIndividual(this.baseURI + createURI(temp.get(i)), Genre));
            }
            this.model.add(netflixObject, hasTitle, e.getTitle());
            this.model.add(netflixObject, dateAdded, e.getDate_added());
//            this.model.add(netflixObject, releasedIn, e.getRelease_year());
            this.model.add(netflixObject, hasDescription, e.getDescription());
            this.model.add(netflixObject, hasRating, e.getRating());
            this.model.add(netflixObject, hasShowId, e.getShow_id());
            Literal l = this.model.createTypedLiteral(Integer.valueOf(e.getRelease_year()));
            this.model.add(netflixObject, releasedIn, l);

            temp = e.getDirectors();
            for (int i=0; i<temp.size(); i++) {
                Individual t = this.model.createIndividual(this.baseURI + createURI(temp.get(i)), Director);
                this.model.add(t, hasDirectingRole, directing);
                this.model.add(netflixObject, hasDirector, t);
            }

            temp = e.getCast();
            for (int i=0; i<temp.size(); i++) {
                Individual t = this.model.createIndividual(this.baseURI + createURI(temp.get(i)), Actor);
                this.model.add(t, hasActingRole, acting);
                this.model.add(netflixObject, hasActor, t);
            }

            temp = e.getCountries();
            for (int i=0; i<temp.size(); i++) {
                if (temp.get(i).length() == 0) {
                    continue;
                }
                this.model.add(netflixObject, hasCountry, this.model.createIndividual(this.baseURI + createURI(temp.get(i)), Country));
            }
            temp = e.getDuration();
            Individual tp = this.model.createIndividual(this.baseURI + e.getShow_id() + "-tp", TimePeriod);
            Individual tpmu = this.model.createIndividual(this.baseURI + e.getShow_id() + "-tpmu", TimePeriodMeasurementUnit);
            this.model.add(tpmu, hasUnit, temp.get(1));
            this.model.add(tp, timePeriodValue, temp.get(0));
            this.model.add(tp, hasTimePeriodMeasurementUnit, tpmu);
        }
        this.model.setNsPrefix("tp", "http://www.ontologydesignpatterns.org/cp/owl/timeperiod.owl#");
        this.model.setNsPrefix("node", this.baseURI);
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
        System.out.print("Enter ontology file Relative or Absolute Path: ");
        String filepath = Reader.readLine();
//        filepath = "D:\\College\\Semester-8\\Sweb\\Semantic-Web-Assignments\\Assignment-3\\ontologies\\2016048_Q3.owl";

        filepath = "D:\\College\\Semester-8\\Sweb\\Semantic-Web-Assignments\\Assignment-4\\temp.owl";
        String netflixFilePath = "src\\NetflixList.csv";
        csvToTriples  q4 = new csvToTriples(filepath);
        q4.convertToTTL(netflixFilePath);
        System.out.println("Output stored in output.ttl");
    }
}
