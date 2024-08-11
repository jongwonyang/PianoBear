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
        noteToSyllable.put("D", "레");
        noteToSyllable.put("E", "미");
        noteToSyllable.put("F", "파");
        noteToSyllable.put("G", "솔");
        noteToSyllable.put("A", "라");
        noteToSyllable.put("B", "시");
    }

    public String modifyMusicXml(String mxlFilePath) throws IOException {
        String tempDir = Files.createTempDirectory("mxl_unzip").toString();
        String xmlFilePath = null;

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
                    if (filePath.endsWith(".xml")) {
                        xmlFilePath = filePath;
                    }
                } else {
                    new File(filePath).mkdirs();
                }
                zipIn.closeEntry();
            }
        }

        if (xmlFilePath == null) {
            throw new IOException("No XML file found inside the MXL file");
        }

        // 실제 XML 파일 경로 추출 및 수정
        String fullXmlPath = extractAndModifyXml(tempDir, xmlFilePath);

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

    private String extractAndModifyXml(String tempDir, String xmlFilePath) throws IOException {
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

                return fullXmlPath;
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

            // <note> 요소 출력 및 수정
            NodeList noteList = doc.getElementsByTagName("note");
            Element lyric = null;
            boolean isChord = false;

            for (int i = 0; i < noteList.getLength(); i++) {
                Node note = noteList.item(i);
                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element noteElement = (Element) note;

                    // Check if the note is part of a chord
                    NodeList chordList = noteElement.getElementsByTagName("chord");
                    if (chordList.getLength() > 0) {
                        isChord = true;
                    } else {
                        isChord = false;
                    }

                    NodeList pitchList = noteElement.getElementsByTagName("pitch");
                    if (pitchList.getLength() > 0) {
                        Element pitch = (Element) pitchList.item(0);
                        String step = pitch.getElementsByTagName("step").item(0).getTextContent();
                        String syllable = noteToSyllable.get(step);

                        if (syllable != null) {
                            if (isChord) {
                                if (lyric == null) {
                                    // 새로운 lyric 요소 생성
                                    lyric = doc.createElement("lyric");
                                    Element syllabic = doc.createElement("syllabic");
                                    syllabic.setTextContent("single");
                                    lyric.appendChild(syllabic);

                                    Element text = doc.createElement("text");
                                    text.setTextContent(syllable);
                                    lyric.appendChild(text);

                                    // 필요한 속성 추가
                                    lyric.setAttribute("default-y", "-73");  // y 위치 조정
                                    lyric.setAttribute("name", "verse");
                                    lyric.setAttribute("number", "1");

                                    noteElement.appendChild(lyric);
                                } else {
                                    // 기존 lyric 요소에 텍스트를 추가
                                    Node textNode = lyric.getElementsByTagName("text").item(0);
                                    textNode.setTextContent(textNode.getTextContent() + syllable);
                                }
                            } else {
                                // 새로운 note에 대해 lyric 요소를 생성 (기존 lyric은 폐기)
                                lyric = doc.createElement("lyric");
                                Element syllabic = doc.createElement("syllabic");
                                syllabic.setTextContent("single");
                                lyric.appendChild(syllabic);

                                Element text = doc.createElement("text");
                                text.setTextContent(syllable);
                                lyric.appendChild(text);

                                // 필요한 속성 추가
                                lyric.setAttribute("default-y", "-73");  // y 위치 조정
                                lyric.setAttribute("name", "verse");
                                lyric.setAttribute("number", "1");

                                noteElement.appendChild(lyric);
                            }
                        }
                    }
                }
            }

            // 수정된 XML 출력
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String modifiedXmlContent = writer.getBuffer().toString();

            // 수정된 내용을 새 파일에 저장
            String newFilePath = xmlFilePath.replace(".xml", "_modified.xml");
            StreamResult result = new StreamResult(new File(newFilePath));
            transformer.transform(new DOMSource(doc), result);

            // 원본 XML 파일 삭제
            Files.delete(Paths.get(xmlFilePath));

            // 저장된 파일을 다시 읽어서 로그로 출력하여 수정이 제대로 되었는지 확인
            String resultContent = new String(Files.readAllBytes(Paths.get(newFilePath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to modify XML file", e);
        }
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
