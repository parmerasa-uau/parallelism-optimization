package parallelismanalysis.entities;

import java.util.ArrayList;
import parallelismanalysis.optimization.Objective;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.entities.patterns.ParallelDesignPattern;

import java.io.File;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parallelismanalysis.Platform;

public class ActivityPatternDiagram extends Activity implements ConsistentElement {

    public final static String XML_NODE_NAME = "activity_pattern_diagram";

    public ArrayList<Activity> activities = new ArrayList<Activity>();

    public ActivityPatternDiagram(String title, Activity start) {
        super(title);
        if (start != null) {
            this.activities.add(start);
        }
    }

    public ActivityPatternDiagram(String title) {
        this(title, null);
    }

    public ActivityPatternDiagram(Node node, Activity parent) {
        super(node, parent);
    }

    public ActivityPatternDiagram(File f) throws Exception {
        System.out.println("Reading " + f.getAbsolutePath());
        
        Document doc = getDocument(f);
        NodeList nodes = doc.getElementsByTagName("activity_pattern_diagram");

        System.out.println();

        ActivityPatternDiagram apd = new ActivityPatternDiagram(nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());

        NodeList children = nodes.item(0).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            apd.handleNode(children.item(i), "", this);
        }

    }

    public ActivityPatternDiagram() {
        this("N/A", null);
    }

    @Override
    public void dump(int indent, String prefix, boolean with_objectives) {
        super.dump(indent, prefix, with_objectives);
        for (Activity a : activities) {
            a.dump(indent + 1, "SEQ", with_objectives);
        }
    }

    @Override
    public void checkConsistency() throws ConsistencyException {
        for (Activity a : activities) {
            if (a == null) {
                System.out.println("Activity cannot be null.");
            }
        }

        for (Activity a : activities) {
            a.checkConsistency();
        }
    }

    private static Document getDocument(File input) throws Exception {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        return db.parse(input);
    }

    public void collectParallelDesignPatterns(ArrayList<ParallelDesignPattern> list) {
        if (list == null) {
            System.out.println("List is null.");
        }
        
        for (Activity a : activities) {
            if (a == null) {
                System.out.println("A i null");
            }

            a.collectParallelDesignPatterns(list);
        }
    }

    /**
     * Calculates the values for several objectives
     */
    // @Override
    public double getObjectiveValue(String obj) throws ObjectiveException {
        if (Objective.OBJ_CORES.equals(obj)) {
            double total_cores = 0;

            for (Activity a : activities) {
                total_cores = Math.max(total_cores, a.getObjectiveValue(Objective.OBJ_CORES));
            }

            return total_cores;
        } else if (Objective.OBJ_DURATION.equals(obj)) {
            double total_weight = 0;

            // Get duration estimates
            for (Activity a : activities) {
                total_weight += a.getObjectiveValue(Objective.OBJ_DURATION);
            }
            
            // Add time for mutator functions
            total_weight += Platform.OVERHEAD_PER_GETSET * this.countSyncGlobalAccesses();

            return total_weight;
        } else if (Objective.OBJ_PATTERNS.equals(obj)) {
            double total_patterns = 0;

            for (Activity a : activities) {
                total_patterns += a.getObjectiveValue(Objective.OBJ_PATTERNS);
            }

            return total_patterns;
        } else if (Objective.OBJ_GLOBALS.equals(obj)) {
            int result = this.getSyncGlobals().size();
            return result;
        } else if (Objective.OBJ_GLOBALS_ACCESSES.equals(obj)) {
            int result = this.countSyncGlobalAccesses();
            return result; // result;
        } else {
            throw new ObjectiveException("Unknown objective " + obj);
        }
    }

    public HashSet<String> getSyncGlobals() throws ObjectiveException {
        HashSet<String> result = new HashSet<>();

        for (Activity a : activities) {
            result.addAll(a.getSyncGlobals());
        }

        return result;
    }
    
    public int countSyncGlobalAccesses() throws ObjectiveException {
        HashSet<String> my_globals = this.getSyncGlobals();
        
        int result = 0;
        for (Activity a : activities) {
            result += a.countSyncGlobalAccesses(my_globals);
        }
        
        return result;
    }
}