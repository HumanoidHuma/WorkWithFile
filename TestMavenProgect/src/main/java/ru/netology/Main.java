package ru.netology;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.*;
import com.opencsv.bean.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) {
//        // Задача 1: CSV - JSON парсер
//        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
//        String fileName = "data.csv";
//
//        List<Employee> list = parseCSV(columnMapping, fileName);
//        String json = listToJson(list);
//        writeString(json, "staff.json");


//        // Задача 2: XML - JSON парсер
//        List<Employee> list = parseXML("data.xml");
//        String json = listToJson(list);
//        writeString(json, "data2.json");


        String json = readString("new_data.json");
        List<Employee> list = jsonToList(json);
        list.forEach(System.out::println);
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(json);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        for (JsonElement jsonObject : jsonArray) {
            Employee employee = gson.fromJson(jsonObject, Employee.class);
            list.add(employee);
        }
        return list;
    }

    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bfReader = new BufferedReader(new FileReader(fileName))) {
            String nextLine;
            while ((nextLine = bfReader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return sb.toString();
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csvToBean.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeString(String text, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    long id = Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employee.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                    list.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return list;
    }
}