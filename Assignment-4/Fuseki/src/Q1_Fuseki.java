import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

public class Q1_Fuseki {

    public void run(RDFConnection conn) {
        conn.load("src\\output.ttl") ;
        QueryExecution qe = conn.query("SELECT DISTINCT ?o WHERE { ?s ?p ?o . }");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();
        System.out.println(results);
    }

    public void createGraph(RDFConnection conn, String graphName) {
        String query = "CREATE SILENT GRAPH <" + graphName + ">";
        System.out.println("Executing: " + query);
        conn.update(query);
        conn.commit();
    }

    public void printQueryResult(ResultSet resultSet) {
        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();
            RDFNode node = querySolution.get("n");
            String name = node.asLiteral().getString();
            System.out.println(name);
        }
    }

    public void moveMovies(RDFConnection conn, String[] graphNames) {
        String query = "" +
                "COPY DEFAULT TO <" + graphNames[0]+">";
        conn.update(query);
        query = "" +
                "COPY DEFAULT TO <" + graphNames[1]+">";
        conn.update(query);
        query = "CLEAR DEFAULT";
        conn.update(query);
        conn.commit();

        query = "PREFIX  xsd:    <http://www.w3.org/2001/XMLSchema#>" +
                "PREFIX  node:     <http://www.semanticweb.org/kvats/ontologies/2020/3/Netflix#>" +
                "DELETE {" +
                "  GRAPH <http://iiitd.ac.in/sweb/2016048/newmoviesgraph> {" +
                "    ?x ?property ?value ;" +
                "       node:releasedIn ?year ." +
                "  }" +
                "}" +
                "WHERE {" +
                "  GRAPH <http://iiitd.ac.in/sweb/2016048/newmoviesgraph> {" +
                "    ?x ?property ?value ;" +
                "    node:releasedIn ?year . FILTER( ?year < \"2016\"^^xsd:int)" +
                "  }" +
                "}";
        conn.update(query);
        query = "PREFIX  xsd:    <http://www.w3.org/2001/XMLSchema#>" +
                "PREFIX  node:     <http://www.semanticweb.org/kvats/ontologies/2020/3/Netflix#>" +
                "DELETE {" +
                "  GRAPH <http://iiitd.ac.in/sweb/2016048/oldmoviesgraph> {" +
                "    ?x ?property ?value ;" +
                "       node:releasedIn ?year ." +
                "  }" +
                "}" +
                "WHERE {" +
                "  GRAPH <http://iiitd.ac.in/sweb/2016048/oldmoviesgraph> {" +
                "    ?x ?property ?value ;" +
                "    node:releasedIn ?year . FILTER( ?year >= \"2016\"^^xsd:int)" +
                "  }" +
                "}";
        conn.update(query);
        conn.commit();
    }

    public static void main(String[] args) {
        Q1_Fuseki q1 = new Q1_Fuseki();
        RDFConnection conn = RDFConnectionFactory.connect("http://localhost:3030/Netflix/");
        String[] graphNames = {"http://iiitd.ac.in/sweb/2016048/newmoviesgraph", "http://iiitd.ac.in/sweb/2016048/oldmoviesgraph"};
        q1.run(conn);
        q1.createGraph(conn, graphNames[0]);
        q1.createGraph(conn, graphNames[1]);
        q1.moveMovies(conn, graphNames);
        conn.close();
    }
}
