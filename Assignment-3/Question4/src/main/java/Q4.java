import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;
import uk.ac.manchester.cs.jfact.JFactFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

/*
* Reference-
* 1. Jfact - http://jfact.sourceforge.net/Example.java
* 2. https://stackoverflow.com/questions/33150182/how-to-get-an-explanation-for-an-inconsistency-using-the-owlexplanation-project
* */


public class Q4 {

    public boolean checkConsistency(OWLOntology o) {
        OWLReasonerFactory reasonerFactory1 = new ReasonerFactory();
        OWLReasoner hermit = reasonerFactory1.createReasoner(o);
        System.out.println("[Hermit] is Ontology Consistent: " + hermit.isConsistent());

        OWLReasonerFactory reasonerFactory2 = new JFactFactory();
        OWLReasoner jfact = reasonerFactory2.createReasoner(o);
        System.out.println("[JFact] is Ontology Consistent: " + jfact.isConsistent());
        return hermit.isConsistent();
    }

    public String getClassName(IRI iri) {
        String i = iri.toString();
        int index = i.lastIndexOf("#");
        return i.substring(index+1, i.length());
    }

    public String getSuperClassesToString(Set<OWLClass> classes) {
        String output = "{";
        for (OWLClass e : classes) {
//            if (getClassName(e.getIRI()).equals("Thing")) {
//                continue;
//            }
            output += getClassName(e.getIRI()) + ", ";
        }
        output = output.substring(0, output.length()-2) + "}";
        return output;
    }

    public HashMap<OWLClass, Set<OWLClass>> hermitClassification(OWLOntology o) throws IOException {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner hermit = reasonerFactory.createReasoner(o);
        OWLClass things = hermit.getTopClassNode().getRepresentativeElement();

        HashMap<OWLClass, Set<OWLClass>> fOutput = new HashMap<OWLClass, Set<OWLClass>>();

        FileWriter myWriter = new FileWriter("HermitOutput.txt");
        NodeSet<OWLClass> subClasses = hermit.getSubClasses(things, false);
        Set<OWLClass> subC = subClasses.getFlattened();
        String output = "";

        for (OWLClass e : subC) {
            output = getClassName(e.getIRI()) + " = ";
//            if (getClassName(e.getIRI()).equals("Thing")) {
//                continue;
//            }
            NodeSet<OWLClass> subClassesTemp = hermit.getSuperClasses(e, false);
            Set<OWLClass> subCTemp = subClassesTemp.getFlattened();
            if (subCTemp.size() < 1) {
                continue;
            }
            output += getSuperClassesToString(subCTemp);
            myWriter.write(output+"\n");
            fOutput.put(e, subCTemp);
        }
        myWriter.close();
        return fOutput;
    }

    public HashMap<OWLClass, Set<OWLClass>> jfactClassification(OWLOntology o) throws IOException {
        OWLReasonerFactory reasonerFactory = new JFactFactory();
        OWLReasoner jfact = reasonerFactory.createReasoner(o);
        OWLClass things = jfact.getTopClassNode().getRepresentativeElement();

        HashMap<OWLClass, Set<OWLClass>> fOutput = new HashMap<OWLClass, Set<OWLClass>>();

        FileWriter myWriter = new FileWriter("JfactOutput.txt");
        NodeSet<OWLClass> subClasses = jfact.getSubClasses(things, false);
        Set<OWLClass> subC = subClasses.getFlattened();
        String output = "";

        for (OWLClass e : subC) {
            output = getClassName(e.getIRI()) + " = ";
//            if (getClassName(e.getIRI()).equals("Thing")) {
//                continue;
//            }
            NodeSet<OWLClass> subClassesTemp = jfact.getSuperClasses(e, false);
            Set<OWLClass> subCTemp = subClassesTemp.getFlattened();
            if (subCTemp.size() < 1) {
                continue;
            }
            output += getSuperClassesToString(subCTemp);
            myWriter.write(output+"\n");
            fOutput.put(e, subCTemp);
        }
        myWriter.close();
        return fOutput;
    }

    public void getOWLAPIExplanation(OWLOntology o, HashMap<OWLClass, Set<OWLClass>> hMap) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
        ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(o);

        for (OWLClass k : hMap.keySet()) {
            Set<OWLClass> v = hMap.get(k);
            for (OWLClass e : v) {
                OWLAxiom axiom = SubClassOf(k, e);
                Set<Explanation<OWLAxiom>> expl = gen.getExplanations(axiom, 3);
                System.out.println("Explanation for SubClassOf(" + getClassName(k.getIRI()) +", " + getClassName(e.getIRI()) + ").");
                for (Explanation<OWLAxiom> p : expl) {
                    System.out.println(p.getEntailment().toString());
                }
            }
        }
    }

    public void getInConsistencyExplanationAPI(OWLOntology o) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLDataFactory dataFactory = o.getOWLOntologyManager().getOWLDataFactory();
        ExplanationGenerator<OWLAxiom> explainInconsistency = new InconsistentOntologyExplanationGeneratorFactory(reasonerFactory, 1000L).createExplanationGenerator(o);
        OWLAxiom owlAxiom = dataFactory.getOWLSubClassOfAxiom(dataFactory.getOWLThing(), dataFactory.getOWLNothing());
        Set<Explanation<OWLAxiom>> explanations = explainInconsistency.getExplanations(owlAxiom);
        System.out.println("InConsistency Explanation using Explanation API");
        for (Explanation<OWLAxiom> p : explanations) {
            System.out.println(p.getEntailment().toString());
        }
    }

    // Reference from - https://github.com/phillord/hermit-reasoner/blob/master/examples/org/semanticweb/HermiT/examples/Explanations.java
    public void getInConsistencyOWLAPI(OWLOntology o) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLDataFactory dataFactory = o.getOWLOntologyManager().getOWLDataFactory();
        OWLAxiom owlAxiom = dataFactory.getOWLSubClassOfAxiom(dataFactory.getOWLThing(), dataFactory.getOWLNothing());
        Configuration configuration=new Configuration();
        configuration.throwInconsistentOntologyException=false;
        OWLReasoner reasoner = reasonerFactory.createReasoner(o, configuration);
        reasonerFactory = new Reasoner.ReasonerFactory() {
            protected OWLReasoner createHermiTOWLReasoner(Configuration configuration,OWLOntology ontology) {
                configuration.throwInconsistentOntologyException=false;
                return new Reasoner(configuration,ontology);
            }
        };
        BlackBoxExplanation bbe = new BlackBoxExplanation(o, reasonerFactory, reasoner);
        HSTExplanationGenerator hstExplanationGenerator = new HSTExplanationGenerator(bbe);
        Node<OWLClass> cl= reasoner.getUnsatisfiableClasses();
        System.out.println("InConsistency Explanation using OWL API");
        for (OWLClass e : cl.getEntities()) {
            System.out.println("Explanation for class: "+ getClassName(e.getIRI()));
            Set<Set<OWLAxiom>> explanations = hstExplanationGenerator.getExplanations(e);
            for (Set<OWLAxiom> p : explanations) {
                for (OWLAxiom axiom : p) {
                    System.out.println(axiom);
                }
            }
        }

    }


    public void getExplanation(OWLOntology o, HashMap<OWLClass, Set<OWLClass>> hMap) {
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        DefaultExplanationGenerator gen = new DefaultExplanationGenerator(o.getOWLOntologyManager(), reasonerFactory, o, new SilentExplanationProgressMonitor());

        for (OWLClass k : hMap.keySet()) {
            Set<OWLClass> v = hMap.get(k);
            for (OWLClass e : v) {
                OWLAxiom axiom = SubClassOf(k, e);
                Set<Set<OWLAxiom>> expl = gen.getExplanations(axiom, 3);
                System.out.println("Explanation for SubClassOf(" + getClassName(k.getIRI()) +", " + getClassName(e.getIRI()) + ").");
                for (Set<OWLAxiom> p : expl) {
                    System.out.println(p.toString());
                }
            }
        }
    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        System.out.print("Enter owl file path: ");
        BufferedReader Reader = new BufferedReader(new InputStreamReader(System.in));
        String word = Reader.readLine();
        OWLOntologyManager oManager = OWLManager.createOWLOntologyManager();
        File owl_file = new File(word);
        System.out.println(word);
        OWLOntology oOntology = oManager.loadOntologyFromOntologyDocument(owl_file);
        Q4 q4 = new Q4();
        if (q4.checkConsistency(oOntology)) {
            HashMap<OWLClass, Set<OWLClass>> hMap1 = q4.hermitClassification(oOntology);
            HashMap<OWLClass, Set<OWLClass>> hMap2 = q4.jfactClassification(oOntology);
            System.out.println("Same Output? : " + hMap1.equals(hMap2));
            q4.getOWLAPIExplanation(oOntology, hMap1);
            q4.getExplanation(oOntology, hMap1);
        }
        else {
            q4.getInConsistencyExplanationAPI(oOntology);
            q4.getInConsistencyOWLAPI(oOntology);
        }
    }
}
