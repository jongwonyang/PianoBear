package kr.pianobear.application.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Service
public class MusicXmlModifierService {

    private static final Map<String, String> noteToSyllable = new HashMap<>();

    static {
        noteToSyllable.put("C", "도");
        noteToSyllable.put("D", "레");
        noteToSyllable.put("E", "미");
        noteToSyllable.put("F", "파");
        noteToSyllable.put("G", "솔");
        noteToSyllable.put("A", "라");
        noteToSyllable.put("B", "시");
    }

    public String modifyMusicXml(String xmlFilePath) throws IOException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(xmlFilePath));
            doc.getDocumentElement().normalize();

            NodeList noteList = doc.getElementsByTagName("note");

            for (int i = 0; i < noteList.getLength(); i++) {
                Node note = noteList.item(i);

                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element noteElement = (Element) note;

                    NodeList pitchList = noteElement.getElementsByTagName("pitch");
                    if (pitchList.getLength() > 0) {
                        Element pitch = (Element) pitchList.item(0);
                        String step = pitch.getElementsByTagName("step").item(0).getTextContent();
                        String syllable = noteToSyllable.get(step);

                        NodeList lyricList = noteElement.getElementsByTagName("lyric");
                        Element lyric;
                        if (lyricList.getLength() > 0) {
                            lyric = (Element) lyricList.item(0);
                        } else {
                            lyric = doc.createElement("lyric");
                            noteElement.appendChild(lyric);
                        }

                        NodeList textList = lyric.getElementsByTagName("text");
                        Element text;
                        if (textList.getLength() > 0) {
                            text = (Element) textList.item(0);
                        } else {
                            text = doc.createElement("text");
                            lyric.appendChild(text);
                        }

                        text.setTextContent(syllable);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new IOException("Failed to modify MusicXML file" + e);
        }

        return xmlFilePath;
    }
}
