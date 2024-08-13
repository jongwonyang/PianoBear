package kr.pianobear.application.service;

import org.springframework.stereotype.Service;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class MusicXmlModifierService {

    private static final Map<String, String> noteToSyllable = new HashMap<>();

    static {
        noteToSyllable.put("C", "도");
        noteToSyllable.put("D", "ㄹㅔ");
        noteToSyllable.put("E", "미");
        noteToSyllable.put("F", "파");
        noteToSyllable.put("G", "솔");
        noteToSyllable.put("A", "라");
        noteToSyllable.put("B", "시");
    }

    public String modifyMusicXml(String mxlFilePath) throws IOException {
        String tempDir = Files.createTempDirectory("mxl_unzip").toString();
        String xmlFilePath = null;
        String musicXmlFilePath = null;

        // MXL 파일 압축 해제
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(mxlFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String filePath = tempDir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    File newFile = new File(filePath);
                    new File(newFile.getParent()).mkdirs();
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                    if (filePath.endsWith(".xml") || filePath.endsWith(".musicxml")) {
                        xmlFilePath = filePath;
                    } else if (filePath.endsWith(".musicxml")) {
                        musicXmlFilePath = filePath;
                    }
                } else {
                    new File(filePath).mkdirs();
                }
                zipIn.closeEntry();
            }
        }

        // XML 또는 MusicXML 파일 수정
        if (xmlFilePath != null) {
            extractAndModifyXml(tempDir, xmlFilePath);
        } else if (musicXmlFilePath != null) {
            modifyXmlFileWithOpenedFile(musicXmlFilePath);
            createOrModifyContainerXml(musicXmlFilePath, tempDir);  // container.xml 파일 생성 또는 수정
        } else {
            throw new IOException("No XML or MusicXML file found inside the MXL file");
        }

        // 수정된 파일을 다시 ZIP 압축
        String modifiedMxlFilePath = mxlFilePath.replace(".mxl", "_modified.mxl");
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(modifiedMxlFilePath))) {
            File dir = new File(tempDir);
            for (File file : dir.listFiles()) {
                addFileToZip(file, file.getName(), zipOut);
            }
        }

        // 임시 디렉토리 삭제
        deleteDirectory(new File(tempDir));

        return modifiedMxlFilePath;
    }

    private void extractAndModifyXml(String tempDir, String xmlFilePath) throws IOException {
        try {
            // XML 파일 경로를 읽고 해당 파일을 찾습니다.
            String xmlContent = new String(Files.readAllBytes(Paths.get(xmlFilePath)), StandardCharsets.UTF_8);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            // rootfile의 full-path를 찾아 실제 XML 파일을 로드합니다.
            NodeList rootfileList = doc.getElementsByTagName("rootfile");
            if (rootfileList.getLength() > 0) {
                Element rootfileElement = (Element) rootfileList.item(0);
                String fullPath = rootfileElement.getAttribute("full-path");
                String fullXmlPath = tempDir + File.separator + fullPath;
                System.out.println("Full XML Path: " + fullXmlPath);

                // 실제 XML 파일을 수정하는 메서드 호출
                modifyXmlFileWithOpenedFile(fullXmlPath);
            } else {
                throw new IOException("No rootfile element found in the XML.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to extract and modify XML file", e);
        }
    }

    private void addFileToZip(File file, String fileName, ZipOutputStream zipOut) throws IOException {
        if (file.isHidden()) {
            return;
        }
        if (file.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            for (File childFile : file.listFiles()) {
                addFileToZip(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    private void modifyXmlFileWithOpenedFile(String xmlFilePath) throws IOException {
        try {
            // XML 파일을 읽고 내용을 정리합니다.
            String xmlContent = new String(Files.readAllBytes(Paths.get(xmlFilePath)), StandardCharsets.UTF_8);

            // XML 파싱
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            // <note> 요소 출력 및 수정 (계이름 추가)
            NodeList noteList = doc.getElementsByTagName("note");

            for (int i = 0; i < noteList.getLength(); i++) {
                Node note = noteList.item(i);
                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element noteElement = (Element) note;

                    NodeList pitchList = noteElement.getElementsByTagName("pitch");
                    for (int j = 0; j < pitchList.getLength(); j++) {
                        Element pitch = (Element) pitchList.item(j);
                        String step = pitch.getElementsByTagName("step").item(0).getTextContent();
                        String octave = pitch.getElementsByTagName("octave").item(0).getTextContent();
                        String syllable = noteToSyllable.get(step);

                        if (syllable != null) {
                            // <lyric> 요소 생성
                            Element lyric = doc.createElement("lyric");
                            lyric.setAttribute("default-y", "-60");

                            // <text> 요소 생성
                            Element text = doc.createElement("text");
                            text.setAttribute("font-size", "20");
                            text.setAttribute("font-weight", "bold");
                            text.setAttribute("color", "#00FF00");
                            text.setTextContent(syllable + octave);

                            // <lyric> 요소에 <text> 추가
                            lyric.appendChild(text);

                            // <note> 요소에 <lyric> 추가
                            noteElement.appendChild(lyric);
                        }
                    }
                }
            }

            // Save the modified XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String modifiedXmlContent = writer.getBuffer().toString();

            // 원래 파일 이름으로 저장 (파일명에 _modified 추가하지 않음)
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(new DOMSource(doc), result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to modify XML file", e);
        }
    }


    private void createOrModifyContainerXml(String musicXmlFilePath, String tempDir) throws IOException {
        String containerXmlPath = tempDir + File.separator + "META-INF" + File.separator + "container.xml";
        File containerFile = new File(containerXmlPath);
        String modifiedFileName = new File(musicXmlFilePath).getName().replace(".musicxml", "_modified.musicxml").replace(".xml", "_modified.xml");

        if (containerFile.exists()) {
            // 기존 container.xml 파일이 있을 경우 수정
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                dbFactory.setNamespaceAware(true);
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(containerFile);
                doc.getDocumentElement().normalize();

                // rootfile 요소를 찾아서 full-path 속성을 수정
                NodeList rootfileList = doc.getElementsByTagName("rootfile");
                if (rootfileList.getLength() > 0) {
                    Element rootfileElement = (Element) rootfileList.item(0);
                    rootfileElement.setAttribute("full-path", modifiedFileName);
                }

                // 수정된 내용을 다시 container.xml에 저장
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.transform(new DOMSource(doc), new StreamResult(containerFile));

            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Failed to modify existing container.xml", e);
            }
        } else {
            // container.xml 파일이 없을 경우 새로 생성
            createContainerXml(musicXmlFilePath, tempDir);
        }
    }

    private void createContainerXml(String musicXmlFilePath, String tempDir) throws IOException {
        String containerXmlPath = tempDir + File.separator + "META-INF" + File.separator + "container.xml";
        File containerDir = new File(containerXmlPath).getParentFile();
        if (!containerDir.exists()) {
            containerDir.mkdirs();
        }
        String modifiedFileName = new File(musicXmlFilePath).getName().replace(".musicxml", "_modified.musicxml").replace(".xml", "_modified.xml");
        String containerXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" version=\"1.0\">\n" +
                "  <rootfiles>\n" +
                "    <rootfile full-path=\"" + modifiedFileName + "\" media-type=\"application/vnd.recordare.musicxml+xml\"/>\n" +
                "  </rootfiles>\n" +
                "</container>";
        Files.write(Paths.get(containerXmlPath), containerXmlContent.getBytes(StandardCharsets.UTF_8));
    }

    private void deleteDirectory(File file) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                deleteDirectory(subFile);
            }
        }
        file.delete();
    }
}
