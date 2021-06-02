import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        processCSV();
        processXML();
    }

    private static void processXML() {
        try {
            List<Employee> list = parseXML("data.xml");
            String json = listToJson(list);
            writeString(json, "data2.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String filepath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filepath));
        NodeList nodeList = document.getDocumentElement().getChildNodes();

        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                Long id = Long.valueOf(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }

        return list;
    }

    private static void processCSV() {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).build();
            List<Employee> list = csv.parse();
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
