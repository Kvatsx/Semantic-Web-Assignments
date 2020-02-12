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
    public String node_url;
    public Resource root;
    public HashMap<String, Property> propertyMap;


    public Q3() {
        this.model = ModelFactory.createDefaultModel();
        this.id = null;
        this.custom_uri = "http://www.iiitd.ac.in/winter2020/sweb/a1/";
        this.node_url = "http://api.conceptnet.io";
//        this.root = null;
//        this.propertyMap = new HashMap<String, Property>();
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

    public void parseJSONLD(String jData) {
        try {
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

                Map Node1 = (Map) tempObject.get("start");
                Resource Start = this.model.createResource(node_url + String.valueOf(Node1.get("@id")));
                Start.addProperty(this.model.createProperty(custom_uri, "label"), String.valueOf(Node1.get("label")));
                Start.addProperty(this.model.createProperty(custom_uri, "language"), String.valueOf(Node1.get("language")));

                Map Node2 = (Map) tempObject.get("end");
                Resource End = this.model.createResource(node_url + String.valueOf(Node2.get("@id")));
                End.addProperty(this.model.createProperty(custom_uri, "label"), String.valueOf(Node2.get("label")));
                End.addProperty(this.model.createProperty(custom_uri, "language"), String.valueOf(Node2.get("language")));

                Start.addProperty(tempProperty, End);
            }
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
        String jsonData = q3.hit(link + "example?offset=0&limit=500");
        String nextPage = q3.getNextUrl(jsonData);
        System.out.println(nextPage);
        q3.parseJSONLD(jsonData);
//        q3.model.write(System.out, "TTL");
        RDFDataMgr.write(System.out, q3.model, RDFFormat.TURTLE_PRETTY) ;
    }
}