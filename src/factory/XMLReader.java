package factory;

import data.Processus;
import log.LoggerUtility;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amelie on 11/03/17.
 */
public class XMLReader {
    private final static Logger logger = LoggerUtility.getLogger(XMLReader.class);
    private Element rootElt;
    private XPath path;
    private ArrayList<Processus> listOfProcess = new ArrayList<Processus>();


    /**
     * @param file is the file where the XML is read
     */
    public XMLReader(String file) {
        Document dom = parseDom(file);
        rootElt = dom.getDocumentElement();
        XPathFactory xpf = XPathFactory.newInstance();
        this.path = xpf.newXPath();
    }

    /**
     * @param file is the file where it is parsed
     * @return the document parsed
     */
    private static Document parseDom(String file) {
        try {
            // création d'une fabrique de parseurs
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            //fabrique.setValidating(true); //si l’on veut vérifier une DTD

            // création d'un parseur
            DocumentBuilder builder = factory.newDocumentBuilder();

            // transformation d'un fichier XML en DOM
            File xml = new File(file);
            Document document = builder.parse(xml);

            return document;

        } catch (ParserConfigurationException pce) {
            logger.error("Erreur de configuration du parseur DOM");
        } catch (SAXException se) {
            logger.error("Erreur lors du parsing du document");
        } catch (IOException ioe) {
            logger.error("Erreur d'entrée/sortie");
        }
        return null;
    }

    /**
     * This method is used to build the list of process
     */
    public void buildData() {
        try {
            int listID = ((Double) path.evaluate("/listOfProcessus/@id", rootElt, XPathConstants.NUMBER)).intValue();
            logger.trace("List of process " + listID);
            NodeList processList = (NodeList) path.evaluate("/listOfProcessus/process", rootElt, XPathConstants.NODESET);
            for (int i = 0; i < processList.getLength(); i++) { // Process list flow
                HashMap<Integer, Integer> arrayOfInOut = new HashMap<>();
                HashMap<Integer, Integer> arrayOfRessources = new HashMap<>();

                Node currentProcess = processList.item(i);

                String nameProcess = (path.evaluate("name", currentProcess)); //Name of the process

                int startTime = ((Double) path.evaluate("start", currentProcess, XPathConstants.NUMBER)).intValue(); // time when the process started

                NodeList inOutList = (NodeList) path.evaluate("listOfInOut/time", currentProcess, XPathConstants.NODESET); // inOut list flow
                for (int j = 0; j < inOutList.getLength(); j++) {
                    Node currentInOut = inOutList.item(j);
                    int inOut = Integer.parseInt(currentInOut.getTextContent());
                    arrayOfInOut.put(j, inOut);
                }

                NodeList ressourcetList = (NodeList) path.evaluate("listOfRessource/time", currentProcess, XPathConstants.NODESET); // ressource list flow
                for (int k = 0; k < ressourcetList.getLength(); k++) {
                    Node currentRessource = ressourcetList.item(k);
                    int ressource = Integer.parseInt(currentRessource.getTextContent());
                    arrayOfRessources.put(k, ressource);
                }

                Processus processus = new Processus(nameProcess, startTime, arrayOfInOut, arrayOfRessources); // Creation of a process
                listOfProcess.add(processus); // Adding the process to the list of process
            }
            this.listOfProcess = listOfProcess;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return listOfProcess is the list of process built from the XML file
     */
    public ArrayList<Processus> getListOfProcess() {
        return listOfProcess;
    }

}

