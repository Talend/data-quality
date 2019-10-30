// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.libraries.devops;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Application to bump versions automatically to facilitate DQ library releases.
 * Please note that this tool works when there are only trivial version changes. For major/minor version changes, some manual
 * changes need to be done.
 * It's always recommended to verify all changed file before committing the changes.
 * <p>
 * Usage:
 * 1. put the expected snapshot or release version into the TARGET_VERSION field and run the current class as Java application.
 * 2. update the p2 dependency declaration in studio-se-master and studio-full-master repositories.
 * 3. Run {@link UpdateComponentDefinition} to update the components.
 */
public class ReleaseVersionBumper {

    private static final String TARGET_VERSION = "7.4.0";

    private static final String TARGET_DAIKON_VERSION = "0.31.10";

    private static final String DAIKON_VERSION_PROPERTY_NAME = "org.talend.daikon.version";

    private static final String BUNDLE_VERSION_STRING = "Bundle-Version: ";

    private XPath xPath = getXPathFactory().newXPath();

    private Transformer xTransformer;

    private ReleaseVersionBumper() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        xTransformer = TransformerFactory.newInstance().newTransformer();
        xTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    }

    private XPathFactory getXPathFactory() {
        XPathFactory xpf = XPathFactory.newInstance();
        try {
            xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (XPathFactoryConfigurationException e) {
            e.printStackTrace(); // NOSONAR
        }
        return xpf;
    }

    private void bumpPomVersion()
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {

        final String resourcePath = ReleaseVersionBumper.class.getResource(".").getFile();
        final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getPath() + File.separator;

        // update root pom file
        String rootPomPath = "../pom.xml";
        File rootPomFile = new File(projectRoot + rootPomPath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document rootDoc = dbf.newDocumentBuilder().parse(rootPomFile);
        Node rootVersion = (Node) xPath.evaluate("/project/version", rootDoc, XPathConstants.NODE);
        rootVersion.setTextContent(TARGET_VERSION);
        xTransformer.transform(new DOMSource(rootDoc), new StreamResult(rootPomFile));

        // update library parent pom file and all the modules
        String parentPomPath = "../dataquality-libraries/pom.xml";
        File inputFile = new File(projectRoot + parentPomPath);
        if (inputFile.exists()) {
            System.out.println("Updating: " + inputFile.getAbsolutePath()); // NOSONAR
            Document doc = dbf.newDocumentBuilder().parse(inputFile);

            // replace parent version
            Node rootParentVersion = (Node) xPath.evaluate("/project/parent/version", doc, XPathConstants.NODE);
            rootParentVersion.setTextContent(TARGET_VERSION);

            // replace version value of this project
            Node parentVersion = (Node) xPath.evaluate("/project/version", doc, XPathConstants.NODE);
            parentVersion.setTextContent(TARGET_VERSION);

            // replace property value of this project
            NodeList propertyNodes = ((Node) xPath.evaluate("/project/properties", doc, XPathConstants.NODE)).getChildNodes();
            for (int idx = 0; idx < propertyNodes.getLength(); idx++) {
                Node node = propertyNodes.item(idx);
                String propertyName = node.getNodeName();
                if (DAIKON_VERSION_PROPERTY_NAME.equals(propertyName) && TARGET_DAIKON_VERSION.length() >= 5) {
                    node.setTextContent(TARGET_DAIKON_VERSION);
                }
            }
            // re-write pom.xml file
            xTransformer.transform(new DOMSource(doc), new StreamResult(inputFile));

            // update manifest of this project
            Path manifestPath = Paths.get(inputFile.getParentFile().getAbsolutePath(), "META-INF", "MANIFEST.MF");
            updateManifestVersion(manifestPath);

            // update modules
            NodeList moduleNodes = (NodeList) xPath.evaluate("/project/modules/module", doc, XPathConstants.NODESET);
            for (int idx = 0; idx < moduleNodes.getLength(); idx++) {
                String modulePath = moduleNodes.item(idx).getTextContent();
                updateChildModules(new File(projectRoot + modulePath + "/pom.xml"));
            }
        }
    }

    private void updateChildModules(File inputFile)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {
        if (inputFile.exists()) {
            System.out.println("Updating: " + inputFile.getAbsolutePath()); // NOSONAR
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = dbf.newDocumentBuilder().parse(inputFile);

            // replace parent version value
            Node parentVersion = (Node) xPath.evaluate("/project/parent/version", doc, XPathConstants.NODE);
            parentVersion.setTextContent(TARGET_VERSION);

            // re-write pom.xml file
            xTransformer.transform(new DOMSource(doc), new StreamResult(inputFile));

            // update manifest file of child project
            Path manifestPath = Paths.get(inputFile.getParentFile().getAbsolutePath(), "META-INF", "MANIFEST.MF");
            updateManifestVersion(manifestPath);
        }
    }

    private void updateManifestVersion(Path manifestPath) throws IOException {
        if (Files.exists(manifestPath)) {
            System.out.println("Updating: " + manifestPath.toString()); // NOSONAR
            List<String> lines = Files.readAllLines(manifestPath);
            DataOutputStream writer = new DataOutputStream(Files.newOutputStream(manifestPath));

            for (String line : lines) {
                if (line.startsWith(BUNDLE_VERSION_STRING)) {

                    writer.write(
                            (BUNDLE_VERSION_STRING + TARGET_VERSION.replace("-", ".") + "\n").getBytes(StandardCharsets.UTF_8));
                } else {
                    writer.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            writer.flush();
            writer.close();
        }
    }

    public static void main(String[] args) throws TransformerFactoryConfigurationError, XPathExpressionException, IOException,
            SAXException, ParserConfigurationException, TransformerException {
        ReleaseVersionBumper appli = new ReleaseVersionBumper();
        appli.bumpPomVersion();
    }

}
