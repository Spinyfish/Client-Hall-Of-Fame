package de.verschwiegener.atero.util.files.config.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.files.config.Config;
import de.verschwiegener.atero.util.files.config.ConfigItem;
import de.verschwiegener.atero.util.files.config.ConfigType;

public class XMLHelper {

    private static void createConfigEnvironment(final File directory) {
	if (!directory.exists()) {
	    directory.mkdir();
	}
    }

    private static Document createDoc(final Config config) {
	try {
	    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    final DocumentBuilder builder = dbf.newDocumentBuilder();
	    final Document doc = builder.newDocument();
	    final Element rootElement = doc.createElement("config");
	    doc.appendChild(rootElement);

	    final Element infoElement = doc.createElement("info");
	    rootElement.appendChild(infoElement);

	    final Element name = doc.createElement("name");
	    name.appendChild(doc.createTextNode(config.getName()));
	    infoElement.appendChild(name);

	    final Element desc = doc.createElement("description");
	    desc.appendChild(doc.createTextNode(config.getDescription()));
	    infoElement.appendChild(desc);

	    final Element server = doc.createElement("server");
	    server.appendChild(doc.createTextNode(config.getRecommendedServerIP()));
	    infoElement.appendChild(server);

	    final Element valueElement = doc.createElement("values");
	    rootElement.appendChild(valueElement);

	    for (final ConfigItem item : config.getItems()) {
		valueElement.appendChild(XMLHelper.createNode(item, doc));
	    }
	    return doc;
	} catch (final Exception e) {

	}
	return null;
    }

    private static Node createNode(final ConfigItem item, final Document doc) {
	final StringBuilder builder = new StringBuilder();
	for (int i = 0; i < item.getArgs().length; i++) {
	    builder.append(item.getArgs()[i]);
	    if (i != item.getArgs().length - 1) {
		builder.append(" ");
	    }
	}

	final Element node = doc.createElement("value");
	node.appendChild(doc.createTextNode(builder.toString()));
	return node;
    }

    public static String createString(final Config config) {
	try {
	    final Document doc = XMLHelper.createDoc(config);

	    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    final Transformer transf = transformerFactory.newTransformer();

	    transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transf.setOutputProperty(OutputKeys.INDENT, "yes");
	    transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

	    final DOMSource source = new DOMSource(doc);

	    final StringWriter writer = new StringWriter();
	    transf.transform(source, new StreamResult(writer));
	    return writer.getBuffer().toString();

	} catch (final Exception e) {
	    //e.printStackTrace();
	}
	return null;
    }


    public static void parse(final InputStream stream, ConfigType type) {
	final StringBuilder textBuilder = new StringBuilder();
	try (Reader reader = new BufferedReader(
		new InputStreamReader(stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
	    int c = 0;
	    while ((c = reader.read()) != -1) {
		textBuilder.append((char) c);
	    }
	} catch (final IOException e) {
	    //e.printStackTrace();
	}
	
	parse(convertStringToXMLDocument(textBuilder.toString()), type);
	
	//parse(textBuilder.toString(), type);
    }
    public static void parse(final File file, ConfigType type) {
	try {
	    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    final DocumentBuilder db = dbf.newDocumentBuilder();
	    final Document doc = db.parse(file);
	    parse(doc, type);
	} catch (SAXException | IOException e) {
	   // e.printStackTrace();
	} catch (ParserConfigurationException e) {
	   // e.printStackTrace();
	}
   	
       }

    private static Document convertStringToXMLDocument(String xmlString) {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = null;
	try {
	    builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
	    return doc;
	} catch (Exception e) {
	    //e.printStackTrace();
	}
	return null;
    }

    public static void parse(final Document doc, ConfigType type) {
	try {
	    //final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    //final DocumentBuilder db = dbf.newDocumentBuilder();
	    //final Document doc = db.parse(input);
	    doc.getDocumentElement().normalize();

	    final NodeList infoList = doc.getElementsByTagName("info");
	    final Config config = new Config(null, null, null, type);
	    for (int i = 0; i < infoList.getLength(); i++) {
		final Node node = infoList.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    final Element eElement = (Element) node;
		    config.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
		    config.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
		    config.setRecommendedServerIP(eElement.getElementsByTagName("server").item(0).getTextContent());
		}
	    }

	    final ArrayList<ConfigItem> items = new ArrayList<>();
	    final NodeList valueList = doc.getElementsByTagName("values");
	    for (int i = 0; i < valueList.getLength(); i++) {
		final Node node = valueList.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    final Element element = (Element) node;
		    final NodeList list = element.getElementsByTagName("value");
		    for (int x = 0; x < list.getLength(); x++) {
			items.add(new ConfigItem(list.item(x).getTextContent().split(" ")));
		    }
		}
	    }
	    config.setItems(items);
	    Management.instance.configmgr.configs.add(config);
	} catch (final Exception e) {
	   // e.printStackTrace();
	}
    }

    public static void write(final Config config) {
	try {

	    final Document doc = XMLHelper.createDoc(config);

	    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    final Transformer transf = transformerFactory.newTransformer();

	    transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transf.setOutputProperty(OutputKeys.INDENT, "yes");
	    transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

	    final DOMSource source = new DOMSource(doc);
	    final String path = Management.instance.CLIENT_DIRECTORY.getAbsolutePath() + File.separator + "Configs";
	    final File myFile = new File(path, config.getName() + ".config");
	    XMLHelper.createConfigEnvironment(new File(path));

	    final StreamResult file = new StreamResult(myFile);

	    transf.transform(source, file);

	} catch (final Exception e) {
	    //e.printStackTrace();
	}
    }

}
