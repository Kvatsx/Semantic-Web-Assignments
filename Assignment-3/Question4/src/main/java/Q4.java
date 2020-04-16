import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Q4 {

    public void checkConsistency(OWLOntology o) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner hermit = reasonerFactory.createReasoner(o);
        System.out.println("[Hermit] is Ontology Consistent: " + hermit.isConsistent());
    }

    public String getClassName(IRI iri) {
        String i = iri.toString();
        int index = i.lastIndexOf("#");
        return i.substring(index+1, i.length());
    }

    public void hermitClassification(OWLOntology o) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner hermit = reasonerFactory.createReasoner(o);
        OWLClass things = hermit.getTopClassNode().getRepresentativeElement();
        IRI iri = things.getIRI();

        Set<OWLClass> Visited = new HashSet<OWLClass>();
        Stack<OWLClass> classes = new Stack<OWLClass>();
        classes.push(things);
        while (!classes.empty()) {
            OWLClass temp = classes.pop();
            if (Visited.contains(temp)) {
                continue;
            }
            Visited.add(temp);
            NodeSet<OWLClass> subClasses = hermit.getSubClasses(temp, false);
            Set<OWLClass> subC = subClasses.getFlattened();
            if (subC.size() == 1) {
                continue;
            }
            System.out.print(getClassName(temp.getIRI()) + " = {");
            for (OWLClass e : subC) {
                if (getClassName(e.getIRI()).equals("Nothing")) {
                    continue;
                }
                System.out.print(getClassName(e.getIRI())+", ");
                if (!Visited.contains(e)) {
                    classes.push(e);
                }
            }
            System.out.println("}");
        }
    }

    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager oManager = OWLManager.createOWLOntologyManager();
        File owl_file = new File("D:\\College\\Semester-8\\Sweb\\Semantic-Web-Assignments\\Assignment-3\\Question4\\src\\main\\java\\Q2.owl");
        OWLOntology oOntology = oManager.loadOntologyFromOntologyDocument(owl_file);
        Q4 q4 = new Q4();
//        q4.checkConsistency(oOntology);
        q4.hermitClassification(oOntology);
    }
}
