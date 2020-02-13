/*
 * ------------------------------------------------------
 * @Author: Kaustav Vats (kaustav16048@iiitd.ac.in)
 * @Roll-No: 2016048
 * @Date: Monday February 10th 2020 8:21:49 pm
 * ------------------------------------------------------
 * Resources used
 * 1. https://mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
 * 2. https://www.geeksforgeeks.org/parse-json-java/
 * 3. https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/riot/ExRIOTw1_writeModel.java
 */

import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.json.simple.parser.ParseException;
import org.apache.jena.rdf.model.Model ;
import org.apache.jena.riot.*;

public class Q3 {
    public Model model;
    public String id;
    public String custom_uri;
    public String node_url, rdf;


    public Q3() {
        this.model = ModelFactory.createDefaultModel();
        this.id = null;
        this.custom_uri = "http://www.iiitd.ac.in/winter2020/sweb/a1/";
        this.node_url = "http://api.conceptnet.io";
        this.rdf = "http://www.w3.org/2000/01/rdf-schema#";
    }

    public String hit(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/ld+json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String result = "";
            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getNextUrl(String jData) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jObject = (JSONObject) parser.parse(jData);
            if (jObject.containsKey("view") ) {
                Map view = (Map) jObject.get("view");
                if (view.containsKey("nextPage")) {
                    return "http://api.conceptnet.io" + view.get("nextPage");
                }
            }
            return "";
        }
        catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void parseJSONLD(String url) {
        try {
            String jData = hit(url);
            JSONParser parser = new JSONParser();
            JSONObject jObject = (JSONObject) parser.parse(jData);
            if (jObject.containsKey("error")) {
                Map error = (Map) jObject.get("error");
                if (error.containsKey("details")) {
                    System.out.println("Error: " + error.get("details"));
                    return;
                }
            }
            this.id = (String) jObject.get("@id");
            JSONArray jArray = (JSONArray) jObject.get("edges");
//            System.out.println(jArray);

            if (jArray.size() == 0) {
                return;
            }
            for (int i=0; i<jArray.size(); i++) {
                JSONObject tempObject = (JSONObject) jArray.get(i);
                Map rel = (Map) tempObject.get("rel");
                Property tempProperty = this.model.createProperty(custom_uri, String.valueOf(rel.get("label")));
                tempProperty.addProperty(RDFS.domain, RDFS.Resource);
                tempProperty.addProperty(RDFS.range, RDFS.Resource);

                Map Node1 = (Map) tempObject.get("start");

                Property label = this.model.createProperty(custom_uri, "label");
                label.addProperty(RDFS.domain, RDFS.Resource);
                label.addProperty(RDFS.range, RDFS.Literal);
                Property language = this.model.createProperty(custom_uri, "language");
                language.addProperty(RDFS.domain, RDFS.Resource);
                language.addProperty(RDFS.range, RDFS.Literal);

                Resource Start = this.model.createResource(node_url + String.valueOf(Node1.get("@id")));
                Start.addProperty(label, String.valueOf(Node1.get("label")));
                Start.addProperty(language, String.valueOf(Node1.get("language")));

                Map Node2 = (Map) tempObject.get("end");
                Resource End = this.model.createResource(node_url + String.valueOf(Node2.get("@id")));
                End.addProperty(label, String.valueOf(Node2.get("label")));
                End.addProperty(language, String.valueOf(Node2.get("language")));

                Start.addProperty(tempProperty, End);
            }
            this.model.setNsPrefix("custom", this.custom_uri);
            this.model.setNsPrefix("rdf", this.rdf);
            RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE_PRETTY) ;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println("Enter text: ");
        Q3 q3 = new Q3();
//        BufferedReader Reader = new BufferedReader(new InputStreamReader(System.in));
//        String word = Reader.readLine();
        String link = "http://api.conceptnet.io/c/en/";
        q3.parseJSONLD(link + "example?offset=0&limit=20");
    }
}