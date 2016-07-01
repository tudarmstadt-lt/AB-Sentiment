package tudarmstadt.lt.ABSentiment.xmlparser;

/**
 * Created by eruppert on 6/17/16.
 */
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.DocumentBuilder;
        import org.w3c.dom.Document;
        import org.w3c.dom.NodeList;
        import org.w3c.dom.Node;
        import org.w3c.dom.Element;
        import tudarmstadt.lt.ABSentiment.type.Opinion;

        import java.io.File;
        import java.util.Vector;

public class XMLParser {

    public static void main(String argv[]) {

        String reviewId, sentenceId, sentenceText;
        Vector<Opinion> opinions;

        try {

            File xmlFile = new File("src/main/resources/ABSA16_Laptops_Train_SB1_v2.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList rList = doc.getElementsByTagName("Review");

            for (int rI = 0; rI < rList.getLength(); rI++) {
                Node rNode = rList.item(rI);

                if (rNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) rNode;

                    reviewId =  eElement.getAttribute("rid");

                    System.out.println("\nCurrent Element: " + rNode.getNodeName() + " Rid:" + reviewId);

                    NodeList sList = ((Element) rNode).getElementsByTagName("sentence");

                    for (int sI = 0; sI < sList.getLength(); sI++) {

                        Node sNode = sList.item(sI);
                        sentenceId = ((Element) sNode).getAttribute("id");
                        sentenceText = sNode.getTextContent().trim();

                        opinions = getOpinions(sNode);

                        System.out.println("Sentence '" + sentenceId + "': " + sentenceText);
                        for (Opinion o : opinions) {
                            System.out.println(o.toString());
                        }
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Vector<Opinion> getOpinions(Node sNode) {
        String category,polarity;
        Vector<Opinion> opinions = new Vector<>();
        NodeList oList = ((Element) sNode).getElementsByTagName("Opinion");

        for (int oI = 0; oI < oList.getLength(); oI++) {

            Node oNOde = oList.item(oI);
            category = ((Element) oNOde).getAttribute("category");
            polarity = ((Element) oNOde).getAttribute("polarity");
            opinions.add(new Opinion(category, polarity));
        }
        return  opinions;
    }

}
