/*
 * ------------------------------------------------------
 * @Author: Kaustav Vats (kaustav16048@iiitd.ac.in)
 * @Roll-No: 2016048
 * @Date: Monday February 10th 2020 8:21:49 pm
 * ------------------------------------------------------
 * Resources used
 * 1. https://mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
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

public class Q3 {
 
    public void hit(String word) {
        try {

            URL url = new URL("http://api.conceptnet.io/c/en/" + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
    
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
            }
    
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    
            String result = "";
            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject) parser.parse(result);
        } 

        catch (MalformedURLException e) {
            e.printStackTrace();
        } 

        catch (IOException e) {
            e.printStackTrace();
        }    
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Enter text: ");
        Q3 q3 = new Q3();
        BufferedReader Reader = new BufferedReader(new InputStreamReader(System.in));  
        String word = Reader.readLine();
        q3.hit(word);
    }
}