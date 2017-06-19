package tudarmstadt.lt.ABSentiment.util;

import com.clearnlp.util.pair.Pair;
import org.apache.tools.ant.taskdefs.Tar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;


public class Conversion {

    private static BufferedReader reader;
    private static int idField = 3;
    private static int relField = 4;
    private static int catField = 5;
    private static int subcatField = 7;
    private static int polField = 6;
    private static int relationField = 9;

    private static boolean lemmatize = false;

    static int idNotFound = 0;
    private static int idFound = 0;

    private static int textNotFound = 0;

    private static int textFound = 0;

    private static int multiAspect  = 0;


    private static int lastPos = -1;


    static Writer tsvOut = null;

    private static Set<String> exclude = new HashSet<>();
    private static Set<String> irrelevant = new HashSet<>();
    private static Set<String> relevant = new HashSet<>();

    private static Vector<String> relevance = new Vector<>();
    private static Vector<String> sentiment = new Vector<>();

    private static Set<String> superAspect = new HashSet<>();
    private static Set<String> subAspect = new HashSet<>();
    private static Set<String> aspect = new HashSet<>();
    private static List<Target> targets = new ArrayList<>();

    static String document = "";

    private static Map<String, String> documentMap = new HashMap<>();
    private static Map<String, String> idMap = new HashMap<>();

    public static void mainTarget(String[] args) {

        // open input file
        String filename = args[0];
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        } catch (FileNotFoundException e1) {
            System.err.println("File could not be opened: " + filename);
            e1.printStackTrace();
            System.exit(1);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // open output file
        Writer targetOut = null;
        try {
            targetOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("targets_" + filename.replace("tsv", "connl")), "UTF-8"));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        String line;
        String document = null;
        String id = null;
        String relLabel = null;
        String sentLabel = null;
        boolean inside = false;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (document != null || document.isEmpty()) {
                        relLabel = extractRelevance(relevance, "1");
                        if (relLabel.compareTo("1") == 0) {
                            targetOut.append("\n");
                        }

                        inside = false;
                        relevance.clear();
                        sentiment.clear();
                        aspect.clear();
                        superAspect.clear();
                        subAspect.clear();
                        document = "";
                        id = "*";
                        relLabel = "skip";
                        // save sentence
                    }
                    targets.clear();

                    // skip sentence line
                    document = reader.readLine();
                    document = document.replace("#Text=###### ", "");
                    document = unescape(document);
                } else {
                    String[] tokenLine = line.split("\\t");
                    if (tokenLine.length > 6) {
                        addRelevance(tokenLine[relField]);
                        if (!exclude.contains(tokenLine[idField])) {
                            id = tokenLine[idField];
                        }
                        relLabel = extractRelevance(relevance, "1");
                        //System.out.println(relLabel);
                        if (relLabel != null && relLabel.compareTo("1") == 0 && tokenLine[2].compareTo("######") != 0) {
                            //System.out.println(line);
                            targetOut.append(tokenLine[2]).append("\t");
                            if (inside && !exclude.contains(tokenLine[catField])) {
                                targetOut.append("I\n");
                            } else if (!inside && !exclude.contains(tokenLine[catField])) {
                                targetOut.append("B\n");
                                inside = true;
                            } else {
                                inside = false;
                                targetOut.append("O\n");
                            }
                        }
                    }
                }
            }

            targetOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        readDocumentLookup("/media/eugen/Data/DB data xlx/id-lookup.csv");


        irrelevant.add("irr");
        //irrelevant.add("no");
        //irrelevant.add("no sent");
        //
        relevant.add("no sent");
        //relevant.add("no");
        // ??

        relevant.add("*");
        relevant.add("no");
        //
        relevant.add("_");
        relevant.add("rel");

        exclude.add("_");
        exclude.add("*");
        exclude.add("");
        exclude.add(null);
        //exclude.add("unklare_Kategorie");
        exclude.add("nicht_vorhandene_Kategorie");
        exclude.add("nicht_identifizierbare_Kategorie");


        //mainTarget(args);

        // open input file
        String filename = args[0];
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        } catch (FileNotFoundException e1) {
            System.err.println("File could not be opened: " + filename);
            e1.printStackTrace();
            System.exit(1);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // open output files
        Writer sentimentOut = null;
        Writer relevanceOut = null;
        Writer aspectOut = null;
        try {
            sentimentOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("sentiment_" + filename), "UTF-8"));

            relevanceOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("relevance_" + filename), "UTF-8"));

            aspectOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("aspect_" + filename), "UTF-8"));
            tsvOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename.replace(".", "-tsv.")), "UTF-8"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        String line;
        String id = null;
        String relLabel = null;
        String sentLabel = null;
        int maxField = 3;


        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder = null;
        try {
            icBuilder = icFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

            System.exit(1);
        }
        Document xmlDoc = icBuilder.newDocument();
        Element rootElement = xmlDoc.createElement("Documents");
        xmlDoc.appendChild(rootElement);


        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (document != null && !document.isEmpty()) {
                        String condensedDocument = document.replaceAll(" ", "");
                        if (id.compareTo("*") == 0 || id.isEmpty()) {
                            //lookup id
                            id = lookupId(condensedDocument);
                        }
                        if (id == null|| id.isEmpty()) {
                            idNotFound++;
                        } else {
                            idFound++;
                        }

                        //System.out.print(id + "\t" + document + "\t");
                        //System.out.println(extractRelevance(relevance));

                        for(int i=0; i<targets.size(); ++i){
                            Target t = targets.get(i);
                            if (!t.isActive()) {
                                continue;
                            }
                            //System.out.println("current target " + t.getPosition() + " " + t.getTarget());
                            //System.out.println(targets.size());
                            Vector<Integer> relations = t.getRelations();
//                            System.out.println("relations " + relations.toString());
                            //if (relations != null) {targets.remove(t); --i;}
                            if (relations == null) {continue;}
                            for (int rId : relations) {
                                //System.out.println("linked id: " + rId);
                                Set<Target> relatedTargets = getTargetsByPosition(rId, true);
                                for (Target related :relatedTargets) {
                                    //related.setOrigPosition(t.getPosition());
                                    t = combineTargetsDisc(t, related, document);
                                    //targets.add(t);
                                    //targets.remove(getTargetByPosition(rId));
                                    //it.previous();
                                }
                                if (relatedTargets.isEmpty()) {

                                    t = combineTargetsDisc(t, getTargetByPosition(rId, true), document);
                                }
                                //t.setRelations(null);
                            }
                        }


                        relLabel = extractRelevance(relevance, "1");

                        if (relLabel.compareTo("skip") != 0 && relLabel.compareTo("-1") != 0 && id != null && !id.isEmpty()) {
                            Element doc = (Element) rootElement.appendChild(createNode(xmlDoc, document, id));
                        } else if (id != null &&!id.isEmpty() ) {

                            tsvOut.write(id+"\t"+document+"\t-1\t_\t_\n");
                        }


                        if (relLabel.compareTo("skip") != 0) {
                            if (lemmatize) {
                                relevanceOut.write(id + "\t" + document + "\t" + relLabel + "\n");
                            } else {

                                relevanceOut.write(id + "\t" + lookupDocument(condensedDocument) + "\t" + relLabel + "\n");
                            }

                            if (relLabel.compareTo("1") == 0) {
                                sentLabel = extractRelevance(sentiment, "neut");
                                if (sentLabel.compareTo("neut") != 0) {
                                    //System.out.println(sentLabel);
                                }
                                sentimentOut.write(id + "\t" + document + "\t");
                                sentimentOut.write(sentLabel + "\n");

                                if (aspect.size() > 0) {
                                    aspectOut.write(id + "\t" + document + "\t");
                                    for (String asp : aspect) {
                                        aspectOut.write(asp + " ");
                                    }
                                    aspectOut.write("\n");
                                }
                            }
                            ;
                        }// else {
                        //System.out.print(id + "\t" + document + "\t");
                        //System.out.println(relLabel);
                        // }
                        relevance.clear();
                        sentiment.clear();
                        aspect.clear();
                        superAspect.clear();
                        subAspect.clear();
                        targets.clear();
                        lastPos = -1;
                        id = "*";
                        relLabel = "skip";
                        maxField = 3;
                        // save sentence
                    }

                    // skip sentence line
                    document = reader.readLine();
                    if (document.isEmpty()) {
                        document = reader.readLine();
                    }

                    targets.clear();
                    document = document.replace("#Text=###### ", "");
                    document = unescape(document);
                    String condensedDocument = document.replaceAll(" ", "");
                    String origDoc = document;
                    document = lookupDocument(condensedDocument);
                    if (document == null) {
                        textNotFound++;
                        //System.out.println(origDoc);
                        document = origDoc;
                    } else {
                        textFound++;
                    }
                    //System.out.println(document);
                    // lookup document id
                } else {
                    if (line.startsWith("#T_SP")) {
                        line = line.replace("#T_SP=webanno.custom.", "");
                        String[] labels = line.split("\\|");
                        if (labels[0].compareTo("Relevance") == 0) {
                            idField = maxField;
                            relField = maxField + 1;
                        }
                        if (labels[0].compareTo("Sentiment") == 0) {
                            catField = maxField;
                            polField = maxField + 1;
                            subcatField = maxField + 2;
                        }
                        maxField += labels.length - 1;
                        //System.out.println(maxField);
                        document = "";
                    }
                    String[] tokenLine = line.split("\\t");
                    if (tokenLine.length > 6) {
                        // add target
                        if (!exclude.contains(tokenLine[catField]) || !exclude.contains(extractSentiment(tokenLine[polField]))) {

                            Set<String> categories = extractAspects(tokenLine[catField], tokenLine[subcatField]);
                            for (String c: categories) {
                                Target t = new Target();
                                //t.setCategory(extractAspect(tokenLine[catField], tokenLine[subcatField]));
                                t.setCategory(c);
                                t.setTarget(tokenLine[2]);
                                try {
                                    t.setPosition(Integer.parseInt(tokenLine[0].replaceFirst("\\d+-", "")));
                                } catch (Exception e) {
                                    System.out.println(document);
                                    System.out.println(line);
                                    System.exit(1);
                                }
                                //System.out.println("last position: "+lastPos);
                                //System.out.println("document: " + document);
                                lastPos = document.indexOf(t.getTarget(), lastPos);
                                if (lastPos == -1 && t.getTarget().compareTo("######") != 0) {
                                    System.out.println("word not found, last position: " + lastPos);
                                    System.out.println("possible to find?+ " + document.indexOf(t.getTarget()));
                                    System.out.println("'" + t.getTarget() + "'");
                                    System.out.println("target position: " + t.getBegin()+ ", " + t.getEnd());
                                    System.out.println("document: " +document);
                                    lastPos = document.indexOf(t.getTarget());
                                    if (lastPos == -1) {
                                        System.exit(1);
                                    }
                                }
                                t.setBegin(lastPos);
                                t.setEnd(lastPos + t.getTarget().length());
                                //System.out.println(lastPos + " " + t.getTarget().length());
                                t.setPolarity(extractSentiment(tokenLine[polField]));

                                Vector<Integer> relations = extractRelations(tokenLine[relationField]);
                                t.setRelations(relations);
                                if (t.getCategory() != null) {
                                    //System.out.println("not null");
                                    Set<Target> previousTargets = getTargetsByPosition(t.getPosition() - 1, false);
                                    for (Target previous :previousTargets) {
                                        if (previous != null) {
                                            //System.out.println("not null");
                                            Target t2 = combineTargets(t, previous, document);
                                            targets.add(t2);
                                            //targets.remove(previous);
                                        }
                                    }
                                    if (previousTargets.isEmpty()) {
                                        targets.add(t);
                                    }
                                } else {
                                    targets.add(t);
                                }
                            }

                            lastPos++;
                        }
                        addRelevance(tokenLine[relField]);
                        addSentiment(tokenLine[polField]);
                        addAspect(tokenLine[catField], tokenLine[subcatField]);
                        if (!exclude.contains(tokenLine[idField])) {
                            id = tokenLine[idField];
                        }
                        if (tokenLine[polField].compareTo("_") != 0) {
                            //System.out.println(tokenLine[polField]);

                        }
                        //System.out.println(tokenLine[polField]);
                    }

                }


            }
            // final sentence
                if (relLabel.compareTo("1") == 0) {
                    sentLabel = extractRelevance(sentiment, "neutral");
                    //System.out.println(sentLabel);
                    sentimentOut.write(id + "\t" + document + "\t");
                    sentimentOut.write(sentLabel + "\n");
                }
            sentimentOut.close();
            relevanceOut.close();
            aspectOut.close();
            tsvOut.close();
        }catch(IOException e1){
                e1.printStackTrace();
            }


            System.out.println("No id found; " + idNotFound);

            System.out.println("id found; " + idFound);

            System.out.println("No text found; " + textNotFound);

            System.out.println("text found; " + textFound);

        System.out.println("multi-aspect: " + multiAspect);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(xmlDoc);
            StreamResult console = new StreamResult(System.out);

            Writer fileOut =new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename.replace("tsv", "xml")), "UTF-8"));
            StreamResult fileResult = new StreamResult(fileOut);

            transformer.transform(source, fileResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> extractAspects(String c, String sub) {
        Set<String> aspects = new HashSet<>();
        c = c.replaceAll("\\[[0-9]+\\]", "");
        sub = sub.replaceAll("\\[[0-9]+\\]", "");

        String[] cLabels = c.split("\\|");
        String[] subLabels = sub.split("\\|");

        for (int i = 0; i<cLabels.length; i++ ) {
            String cLabel = cLabels[i].replaceAll(" ", "_").replaceAll("ae", "ä").replaceAll("oe", "ö").replaceAll("ue", "ü");
            String subLabel = subLabels[i].replaceAll(" ", "_").replaceAll("ae", "ä").replaceAll("oe", "ö").replaceAll("ue", "ü");
            if (cLabel.compareTo("unklare_Kategorie") == 0 || cLabel.compareTo("Unklare_Kategorie") == 0) {
                cLabel = "Allgemein";
            }
            if (!exclude.contains(cLabel)) {
                if(!exclude.contains(subLabel)) {
                    aspects.add(cLabel+"#"+subLabel);
                } else {
                    aspects.add(cLabel+"#Haupt");
                }
            }
        }

        // correction
        for (String asp : aspects) {
            if (asp.compareTo("Allgemein#Reservierung") == 0) {
                aspects.remove(asp);
                aspects.add("Komfort_und_Ausstattung#Reservierung");
            }
            if (asp.compareTo("Allgemein#Streckennetz") == 0) {
                aspects.remove(asp);
                aspects.add("Zugfahrt#Streckennetz");
            }
            if (asp.compareTo("Komfort_und_Ausstattung#Am-Platz-Service_und_1._Klasse-Service") == 0) {
                aspects.remove(asp);
                aspects.add("Service_und_Kundenbetreuung#Am-Platz-Service_und_1._Klasse-Service");
            }
            if (asp.compareTo("Komfort_und_Ausstattung#Sauberkeit_allgemein") == 0) {
                aspects.remove(asp);
                aspects.add("Atmosphäre#Sauberkeit_allgemein");
            }
            if (asp.compareTo("Allgemein#Streckennetz") == 0) {
                aspects.remove(asp);
                aspects.add("Zugfahrt#Streckennetz");
            }
            if (asp.compareTo("Sonstige_Schäden_und_Störungen#Haupt") == 0) {
                aspects.remove(asp);
                aspects.add("Sonstige_Unregelmässigkeiten#Haupt");
            }
            if (asp.compareTo("Sonstiges_/_Keine_Kategorie#Haupt") == 0) {
                aspects.remove(asp);
                aspects.add("Allgemein#Haupt");
            }
        }

        if (aspects.isEmpty()) {aspects.add(null);}
        return aspects;

    }

    private static Vector<Integer> extractRelations(String s) {
        Vector<Integer> ret = new Vector<>();

        String[] rels = s.split("\\|");
        for  (String r : rels) {
            //System.out.println("link " + r);
            r = r.replaceAll("_", "").replaceAll("\\d+-", "").replaceAll("\\[\\d+\\]", "");
            if (!r.isEmpty() && !exclude.contains(r)) {
                ret.add(Integer.parseInt(r));
            }
        }
        return ret;
    }

    private static Target combineTargets(Target t, Target previous, String document) {
        targets.remove(t);
        //targets.remove(previous);
        if (t.getPosition() == previous.getPosition() ||
                (t.getCategory() != null && previous.getCategory() != null &&t.getCategory().compareTo(previous.getCategory()) != 0)) {
            return t;
        }
        if (previous.getCategory() != null) {
            t.setCategory(previous.getCategory());
            t.setBegin(previous.getBegin());
            t.setTarget(document.substring(t.getBegin(), t.getEnd()));
            Vector<Integer> rels = t.getRelations();
            rels.addAll(previous.getRelations());
            t.setRelations(rels);
            t.setOrigPosition(previous.getPosition());
            previous.setEndPosition(t.getPosition());
            previous.setActive(false);
            previous.setTarget(t.getTarget());
            previous.setEnd(t.getEnd());
        } else if (previous.getPolarity() != null) {
            previous.setActive(false);
        }
        if (previous.getPolarity() != null) {
            t.setPolarity(previous.getPolarity());
        }

        return t;
    }


    private static Target combineTargetsDisc(Target t, Target previous, String document) {
        if (t.getPosition() == previous.getPosition()|| t.getBegin() == previous.getBegin()) { return t;}
        if (previous.getCategory() != null && t.getCategory() != null  && t.getCategory().compareTo(previous.getCategory()) != 0){
            return t;
        }
        if (previous.getCategory() != null && t.getCategory() == null){
            //System.out.println(document);
            //t.setCategory(previous.getCategory());
            return t;
        }
        //System.out.println("combination " + t.getPosition());
        //System.out.println("former id " + previous.getPosition());
        if (previous.getCategory() != null && previous.getCategory().compareTo(t.getCategory()) == 0) {
            // discountinuous annotation
            if (t.getBegin() == previous.getEnd()) {
                t.setBegin(previous.getBegin());
                t.setTarget(document.substring(t.getBegin(), t.getEnd()));
                //t.setOrigPosition(previous.getPosition());
            } else if (previous.getBegin() == t.getEnd()) {
                t.setEnd(previous.getEnd());
                t.setTarget(document.substring(t.getBegin(), t.getEnd()));
                //previous.setOrigPosition(t.getPosition());
            } else if (previous.getBegin() < t.getBegin()) {
                if (previous.isContained(t)) { return previous;}
                if (previous.getEndPosition() != null) {
                    Target pPrev = getTargetByPosition(previous.getEndPosition(), false);
                    while (pPrev.getEndPosition() != null) {

                        pPrev = getTargetByPosition(pPrev.getEndPosition(), false);
                    }

                    t.setTarget(pPrev.getTarget() + " * " + t.getTarget());
                    //t.addDiscontinuous(t.getBegin(), t.getEnd());
                    t.addPositions(pPrev.getPositions());
                    t.addPosition(t.getBegin(), t.getEnd());
                    t.setBegin(pPrev.getBegin());
                    t.setEnd(pPrev.getEnd());

                    pPrev.setActive(false);
                } else {

                    t.setTarget(previous.getTarget() + " * " + t.getTarget());
                    //t.addDiscontinuous(t.getBegin(), t.getEnd());
                    t.addPositions(previous.getPositions());
                    t.addPosition(t.getBegin(), t.getEnd());
                    t.setBegin(previous.getBegin());
                    t.setEnd(previous.getEnd());
                }
                //t.setOrigPosition(previous.getPosition());
            } else {
                if (t.isContained(previous)) { return t;}
                if (previous.getEndPosition() != null) {

                    Target pPrev = getTargetByPosition(previous.getEndPosition(), false);
                    while (pPrev.getEndPosition() != null) {

                        pPrev = getTargetByPosition(pPrev.getEndPosition(), false);
                    }

                    t.setTarget(t.getTarget() + " * " + pPrev.getTarget());
                    t.addPosition(pPrev.getBegin(), pPrev.getEnd());
                    t.addPositions(pPrev.getPositions());
                    pPrev.setActive(false);

                } else {
                    t.setTarget(t.getTarget() + " * " + previous.getTarget());
                    t.addPosition(previous.getBegin(), previous.getEnd());
                    t.addPositions(previous.getPositions());
                }
                //previous.setOrigPosition(t.getPosition());
            }
        }
        // relation to polarity words
        if (previous.getPolarity() != null) {
            t.setPolarity(previous.getPolarity());
        }

        previous.setOrigPosition(t.getPosition());
        previous.setActive(false);
//        if (previous.getCategory() == null) {
//            System.out.println(previous.getTarget() + " setting orig to " + t.getPosition());
//        } else {
//            System.out.println(t.getTarget() + " setting orig to " + previous.getPosition());
//            t.setOrigPosition(previous.getPosition());
//
//        }

        return t;
    }


    private static Target getTargetByPosition(int position, boolean recursive) {
        for (Target t: targets) {
            if (t.getPosition() == position) {
                if (t.getOrigPosition() != -1 && recursive) {
                    //System.out.println("Position -- link " + t.getOrigPosition());
                    return getTargetByPosition(t.getOrigPosition(), false);
                }
                return t;
            }
        }
        return null;
    }

    private static Set<Target> getTargetsByPosition(int position, boolean recursive) {
        Set<Target> resultTargets = new HashSet<>();
        for (Target t: targets) {
            if (t.getPosition() == position) {
                if (t.getOrigPosition() != -1 && recursive) {
                    //System.out.println("Position -- link " + t.getOrigPosition());
                    resultTargets.addAll(getTargetsByPosition(t.getOrigPosition(), recursive, t.getPosition(), 10));
                } else {
                    resultTargets.add(t);
                }
            }
        }
        return resultTargets;
    }

    private static Set<Target> getTargetsByPosition(int position, boolean recursive, int initial, int depth) {
        Set<Target> resultTargets = new HashSet<>();
        for (Target t: targets) {
            if (t.getPosition() == position && depth > 0) {
                if (t.getOrigPosition() != -1 && recursive && t.getPosition() != initial) {
                    //System.out.println("Position -- link " + t.getOrigPosition());
                    resultTargets.addAll(getTargetsByPosition(t.getOrigPosition(), recursive, initial, --depth));
                } else {
                    resultTargets.add(t);
                }
            }
        }
        return resultTargets;
    }


    private static void readDocumentLookup(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            String condensed;
            String id;
            String document;
            while ((line = br.readLine()) != null) {
                String[] catLine = line.split("\\t");
                if (catLine.length < 3) continue;
                id = catLine[0];
                document = catLine[1];
                condensed = catLine[2];
                idMap.put(condensed, id);
                documentMap.put(condensed,document);

                condensed = condensed.replaceAll("�", "?");

                condensed = condensed.replaceAll("\\?", "");

                condensed = condensed.replaceAll("\"", "");
                condensed = condensed.replaceAll("\'", "");
                idMap.put(condensed, id);
                documentMap.put(condensed,document);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            //System.exit(1);
        }
    }

    private static String lookupId(String condensedDocument) {
        //System.out.println(condensedDocument +"\n"+ idMap.get(condensedDocument));
        if (idMap.get(condensedDocument) == null) {

            condensedDocument = condensedDocument.replaceAll("�", "?");
            condensedDocument = condensedDocument.replaceAll("\\?", "").replaceAll("\"", "").replaceAll("'","");
            if (idMap.get(condensedDocument) == null) {
                //System.err.println("not found");
                //System.exit(1);
            }

            return idMap.get(condensedDocument);
        }

        return idMap.get(condensedDocument);
    }

    private static String lookupDocument(String condensedDocument) {
        if (documentMap.get(condensedDocument) == null) {

            condensedDocument = condensedDocument.replaceAll("�", "");

            condensedDocument = condensedDocument.replaceAll("\\?", "");
            if (documentMap.get(condensedDocument) == null) {
                //System.err.println("not found");
                //System.out.println(condensedDocument);
                //System.exit(1);
            }

            return documentMap.get(condensedDocument);
        }

        return documentMap.get(condensedDocument);
    }

    private static void addRelevance(String s) {
        if (!exclude.contains(s)) {
            relevance.add(s);
        }
    }
    private static Node createNode(Document doc, String document, String id) {
        Element documentElement = doc.createElement("Document");
        documentElement.setAttribute("id", id);

        HashMap<String, Integer> aspectCount = new HashMap<>();
        HashMap<String, Integer> polarityCount = new HashMap<>();

        Element text = doc.createElement("text");
        documentElement.appendChild(text);
        text.setTextContent(document);

        Element opinions = doc.createElement("Opinions");
        documentElement.appendChild(opinions);

        if (!targets.isEmpty()) {
            Set<Integer> positions = new HashSet<>();
            for (Target t : targets) {
                if (t.getCategory() == null || !t.isActive()) continue;
                Element opinion = doc.createElement("Opinion");
                opinions.appendChild(opinion);
                String aspect = t.getCategory();
                if (aspectCount.get(aspect) != null) {
                    aspectCount.put(aspect, aspectCount.get(aspect) + 1);
                } else {
                    aspectCount.put(aspect, 1);
                }
                opinion.setAttribute("category", aspect);
                if (t.getPolarity() == null || t.getPolarity().isEmpty()) {
                    opinion.setAttribute("polarity", "neutral");

                } else {
                    String polarity = t.getPolarity();
                    opinion.setAttribute("polarity",polarity);
                    if (polarity.compareTo("neutral") != 0) {
                        if (polarityCount.get(polarity) != null) {
                            polarityCount.put(polarity, polarityCount.get(polarity) + 1);
                        } else {
                            polarityCount.put(polarity, 1);
                        }
                    }
                }
                //opinion.setAttribute("orig-position", t.getOrigPosition()+"");
                //opinion.setAttribute("active", t.isActive()+"");
                //opinion.setAttribute("position", t.getPosition()+"");
                if (t.getBegin() == -1) {
                    opinion.setAttribute("target", "NULL");
                    opinion.setAttribute("from", 0+"");
                    opinion.setAttribute("to", 0+"");
                    if (positions.contains(0)) {
                        multiAspect++;
                    }
                    positions.add(0);
                } else {
                    opinion.setAttribute("target", t.getTarget());
                    opinion.setAttribute("from", t.getBegin() + "");
                    opinion.setAttribute("to", t.getEnd() + "");
                    if (positions.contains(t.getBegin())) {
                        multiAspect++;
                    }
                    positions.add(t.getBegin());
                }
                if (!t.getPositions().isEmpty()) {
                    int currentPos = 2;
                    for (Pair<Integer, Integer> pair : t.getPositions()) {
                        opinion.setAttribute("from"+currentPos, pair.o1+"");

                        opinion.setAttribute("to"+currentPos, pair.o2+"");
                        currentPos++;
                    }
                }
            }
        } else {
            Element opinion = doc.createElement("Opinion");
            opinions.appendChild(opinion);
            opinion.setAttribute("category", "Allgemein#Haupt");

            opinion.setAttribute("polarity", "neutral");

            opinion.setAttribute("target", "NULL");
            opinion.setAttribute("from", 0+"");
            opinion.setAttribute("to", 0+"");
        }

        try {
            String polarity;

            ValueComparator polComp = new ValueComparator(polarityCount);
            TreeMap<String, Integer> sortedPolarities = new TreeMap<>(polComp);
            sortedPolarities.putAll(polarityCount);
            if (!sortedPolarities.isEmpty()) {
                polarity = sortedPolarities.firstKey();
            } else {
                polarity = "neutral";
            }

            StringBuilder aspects = new StringBuilder();
            ValueComparator aspComp = new ValueComparator(aspectCount);
            TreeMap<String, Integer> sortedAspects = new TreeMap<>(aspComp);
            sortedAspects.putAll(aspectCount);
            for (String asp : sortedAspects.keySet()) {
                aspects.append(asp+" ");
            }
            if (aspectCount.isEmpty()) {
                aspects.append("Allgemein#Haupt");
            }

            tsvOut.write(id+"\t"+document+"\t1\t"+ polarity + "\t" + aspects.toString().trim() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        return documentElement;
    }

    private static void addSentiment(String s) {
        s = s.replaceAll("\\[[0-9]+\\]", "");
        String[] labels = s.split("\\|");
        for (String label : labels) {
            if (!exclude.contains(label)) {
                sentiment.add(label);
            }
        }
    }

    private static String extractSentiment(String s) {
        s = s.replaceAll("\\[[0-9]+\\]", "");
        String[] labels = s.split("\\|");
        for (String label : labels) {
            if (!exclude.contains(label) && !label.isEmpty()) {
                if (label.compareTo("pos")==0) {return "positive";}
                if (label.compareTo("neg")==0) {return "negative";}
                if (label.compareTo("neut")==0) {return "neutral";}
                if (label.compareTo("no sent")==0) {return "neutral";}

                return "neutral";
            }
        }
return  null;

    }

    private static void addAspect(String c, String sub) {
        c = c.replaceAll("\\[[0-9]+\\]", "");
        sub = sub.replaceAll("\\[[0-9]+\\]", "");

        String[] cLabels = c.split("\\|");
        String[] subLabels = sub.split("\\|");

        for (int i = 0; i<cLabels.length; i++ ) {
            String cLabel = cLabels[i].replaceAll(" ", "_").replaceAll("ae", "ä").replaceAll("oe", "ö").replaceAll("ue", "ü");
            String subLabel = subLabels[i].replaceAll(" ", "_");
            if (!exclude.contains(cLabel)) {
                if(!exclude.contains(subLabel)) {
                    aspect.add(cLabel+"#"+subLabel);
                } else {
                    aspect.add(cLabel+"#Haupt");
                }
            }
        }
    }
    private static String extractAspect(String c, String sub) {
        c = c.replaceAll("\\[[0-9]+\\]", "");
        sub = sub.replaceAll("\\[[0-9]+\\]", "");

        String[] cLabels = c.split("\\|");
        String[] subLabels = sub.split("\\|");

        for (int i = 0; i<cLabels.length; i++ ) {
            String cLabel = cLabels[i].replaceAll(" ", "_").replaceAll("ae", "ä").replaceAll("oe", "ö").replaceAll("ue", "ü");
            String subLabel = subLabels[i].replaceAll(" ", "_");
            if (!exclude.contains(cLabel) && !cLabel.isEmpty()) {
                if(!exclude.contains(subLabel) &&!subLabel.isEmpty()) {
                    //aspect.add(cLabel+"#"+subLabel);
                    return cLabel+"#"+subLabel;
                } else {
                    return cLabel+"#Haupt";
                }
            }
        }
        return null;

    }


    private static String extractRelevance(Vector<String> set, String standard) {
        Map<String, Integer> counts = new HashMap<>();
        for (String s : set) {
            if (counts.get(s)== null) {
                counts.put(s, 1);
            } else {
                counts.put(s, counts.get(s) + 1);
            }
        }
        if (set.isEmpty()) {
            return standard;
        }
        ValueComparator comp = new ValueComparator(counts);
        TreeMap<String, Integer> sorted = new TreeMap<>(comp);
        sorted.putAll(counts);

        String label = sorted.firstKey();
        if (standard.compareTo("1") == 0) {
            if (irrelevant.contains(label)) {
                return "-1";
            } else if (relevant.contains(label)) {
                return "1";
            } else {
                //System.out.println(label);
                //System.exit(1);
                return "skip";
            }
        } else if (standard.compareTo("neut") == 0) {
            return sorted.firstKey();
        }
        return null;
    }

    static String unescape(String s) {
        int i=0, len=s.length();
        char c;
        StringBuffer sb = new StringBuffer(len);
        while (i < len) {
            c = s.charAt(i++);
            if (c == '\\') {
                if (i < len) {
                    c = s.charAt(i++);
                    if (c == 'u') {
                        try {
                            c = (char) Integer.parseInt(s.substring(i, i + 4), 16);
                            i += 4;
                        } catch (Exception e) {
                            System.out.println("Error: could not convert to character\nOriginal: " + s);
                            System.out.println("substring: " + s.substring(i, i + 4));
                            System.out.println("stats so far: " + sb.toString());
                            System.err.println(e.getMessage());
                            System.exit(1);
                        }
                    } // add other cases here as desired...
                }
            } // fall through: \ escapes itself, quotes any character but u
            sb.append(c);
        }
        return sb.toString();
    }
}

class ValueComparator implements Comparator<String> {
    private Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
