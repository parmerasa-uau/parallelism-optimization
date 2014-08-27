package parallelismanalysis.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parallelismanalysis.optimization.Objective;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.entities.patterns.ParallelDesignPattern;
import parallelismanalysis.entities.patterns.ParallelPipeline;
import parallelismanalysis.entities.patterns.PeriodicTaskParallelism;
import parallelismanalysis.entities.patterns.TaskParallelism;

/**
 * Basic Activity entity, either a sequential piece of code or a PDP
 */
public class Activity implements ConsistentElement {

    public final static String XML_NODE_NAME = "activity";
    public String title;
    public int weight = 0;
    public String OID;

    private HashSet<String> globals = null;

    public Activity(String title, int weight) {
        this.title = title;
        this.weight = weight;
    }

    public Activity(String title) {
        this(title, 0);
    }

    public Activity(Node node, Activity parent) {
        if (node.getAttributes().getNamedItem("name") != null) {
            this.title = node.getAttributes().getNamedItem("name").getNodeValue();
        }

        if (node.getAttributes().getNamedItem("weight") != null) {
            this.weight = Integer.parseInt(node.getAttributes().getNamedItem("weight").getNodeValue());
        }

        if (node.getAttributes().getNamedItem("period") != null) {
            if (parent instanceof PeriodicTaskParallelism) {
                PeriodicTaskParallelism ptp = (PeriodicTaskParallelism) parent;
                ptp.periods.put(this, Integer.parseInt(node.getAttributes().getNamedItem("period").getNodeValue()));
            }
        }
    }

    public Activity() {
        this("N/A");
    }

    public void dump() {
        dump(0, "", false);
    }

    public void dump(boolean with_objectives) {
        dump(0, "", with_objectives);
    }

    public void dump(int indent, String prefix, boolean with_objectives) {
        double[] objective_values = new double[Objective.OBJECTIVES.length];
        if (with_objectives) {
            for (int i = 0; i < Objective.OBJECTIVES.length; i++) {
                try {
                    objective_values[i] = getObjectiveValue(Objective.OBJECTIVES[i]);
                } catch (ObjectiveException ex) {
                    System.out.println("Exception: " + ex.getMessage());
                    objective_values[i] = Double.MAX_VALUE;
                }
            }
        }

        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }

        if (prefix.length() > 0) {
            System.out.print(prefix + " ");
        }

        System.out.print(this);

        System.out.print(" ==>");

        if (this instanceof ParallelDesignPattern) {
            ParallelDesignPattern pdp = (ParallelDesignPattern) this;
            System.out.print(" " + pdp.getParameter());
        }

        if (with_objectives) {
            for (int i = 0; i < Objective.OBJECTIVES.length; i++) {
                System.out.print(" " + Objective.OBJECTIVES[i] + "=" + objective_values[i]);
            }
        }

        /* int act = this.getOverallWeight();
         int par = this.getOverallWeight(ActivityPatternDiagram.PARALLEL);
         int seq = this.getOverallWeight(ActivityPatternDiagram.SEQUENTIAL);
        
         String speedup_max = "" + Math.round((((seq) * 1.0) / par) * 100) / 100.0;
         String speedup_act = "" + Math.round((((seq) * 1.0) / act) * 100) / 100.0;

         System.out.print(" ==> ACT " + act + " S " + speedup_act + " | ");

         if (par == seq) {
         System.out.print("PAR/SEQ " + seq);
         } else {
         System.out.print("PAR " + par + " SEQ " + seq + " S " + speedup_max);
         } */
        System.out.println("");
    }

    @Override
    public void checkConsistency() throws ConsistencyException {
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "] " + "" + title + " (" + weight + ")";
    }

    /**
     * Calculates the values for several objectives
     */
    public double getObjectiveValue(Objective obj) throws ObjectiveException {
        if (Objective.OBJ_CORES.equals(obj)) {
            return 1;
        } else if (Objective.OBJ_DURATION.equals(obj)) {
            return weight;
        } else if (Objective.OBJ_PATTERNS.equals(obj)) {
            return 0;
        } else if (Objective.OBJ_GLOBALS.equals(obj)) {
            return 0;
        } else {
            throw new ObjectiveException("Unknown objective " + obj);
        }
    }

    public void collectParallelDesignPatterns(ArrayList<ParallelDesignPattern> list) {
        return;
    }

    public void handleNode(Node node, String base, Activity parent) {
        // System.out.println(node + " - " + node.getNodeName() + " - " + node.getNodeType() + " - " + node.getNodeValue());

        if (node.getNodeName().equals("#text")) {
            // parent.setText(node.getNodeValue());
        } else {
            Activity st;

            switch (node.getNodeName()) {
                case TaskParallelism.XML_NODE_NAME:
                    st = new TaskParallelism(node, parent);
                    break;
                case ActivityPatternDiagram.XML_NODE_NAME:
                    st = new ActivityPatternDiagram(node, parent);
                    break;
                case ParallelPipeline.XML_NODE_NAME:
                    st = new ParallelPipeline(node, parent);
                    break;
                case PeriodicTaskParallelism.XML_NODE_NAME:
                    st = new PeriodicTaskParallelism(node, parent);
                    break;
                case Activity.XML_NODE_NAME:
                    st = new Activity(node, parent);
                    break;
                default:
                    throw new UnsupportedOperationException("Unbekannter Typ: " + node.getNodeName());

            }

            if (parent instanceof ActivityPatternDiagram) {
                ActivityPatternDiagram item = (ActivityPatternDiagram) parent;
                item.activities.add(st);
            } else if (parent instanceof ParallelDesignPattern) {
                ParallelDesignPattern item = (ParallelDesignPattern) parent;
                item.stages.add(st);
            } else if (parent == null) {
                // System.out.println("Parent is null");
            } else {
                throw new UnsupportedOperationException("Kann nicht hinzufügen, parent ist vom Typ " + parent.getClass().getSimpleName());
            }

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                handleNode(children.item(i), base + " > " + node.getNodeName(), st);
            }
        }
    }

    private static boolean no_vars = false;

    public HashSet<String> getGlobals() {
        if (globals == null) {
            HashSet<String> result = new HashSet<>();

            File file = new File("CrawlerCraneVariables.txt");

            BufferedReader br = null;

            if (!no_vars) {
                try {
                    int i = 0;
                    String readLine;
                    br = new BufferedReader(new FileReader(file));

                    while ((readLine = br.readLine()) != null) {
                        boolean skipline = false;
                        if (i == 0 && readLine.contains("Funktion: " + this.title)) {
                            i = 1;
                        }
                        if (i == 1 && readLine.contains("evtl. angesprochene globale Variablen:")) {
                            i = 2;
                            skipline = true;
                        }
                        if (i == 2 && readLine.trim().isEmpty()) {
                            i = 0;
                        }

                        if (i == 2 && !skipline) {
                            String var = readLine.trim();
                            var = var.replace(",rw", "");
                            var = var.replace(",r", "");
                            var = var.replace(",w", "");
                            result.add(var);
                            // System.out.println("Variable gefunden: " + var);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    no_vars = true;
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            globals = result;

            // System.out.println(globals.size() + " globals calculated.");
        }

        return globals;
    }

    public HashSet<String> getSyncGlobals() throws ObjectiveException {
        HashSet<String> result = new HashSet<>();

        return result;
    }

    public int countSyncGlobalAccesses(HashSet<String> all_globals) throws ObjectiveException {
        HashSet<String> my_globals = this.getGlobals();

        int result = 0;

        for (String var : all_globals) {
            if (my_globals.contains(var)) {
                result++;
            }
        }

        return result;

        // System.out.println("All globals: " + globals);
        // System.out.println("My globals: " + my_globals);
        // my_globals.retainAll(all_globals);
        // System.out.println("Intersection: " + my_globals);
        // System.out.println("Acccessed globals for " + this + " is " + my_globals.size() + " (" + my_globals + ")");
        // return my_globals.size();
    }

    /* public HashSet<String> getSyncGlobalsAccesses() throws ObjectiveException {
     HashSet<String> result = new HashSet<>();

     return result;
     } */
}
