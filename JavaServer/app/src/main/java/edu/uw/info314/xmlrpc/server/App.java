package edu.uw.info314.xmlrpc.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.logging.*;

import static spark.Spark.*;

class Call {
    public String name;
    public List<Object> args = new ArrayList<Object>();
}

public class App {
    public static final Logger LOG = Logger.getLogger(App.class.getCanonicalName());
    public static String xml;

    public static Call calcOp;

    public static void main(String[] args) {
        LOG.info("Starting up on port 4567");

        // This is the mapping for POST requests to "/RPC";
        // this is where you will want to handle incoming XML-RPC requests
        get("/hello", (req, res) -> "Hello World");
        post("/RPC", (request, response) -> {
            String xmlData = request.body();
            System.out.println(xmlData);
            calcOp = extractXMLRPCCall(xmlData);
            int result = determineCalcOp(calcOp);
            String xmlBody = buildXML(result);
            response.status(200);
            System.out.println(xmlBody);
            return xmlBody; });




        // Each of the verbs has a similar format: get() for GET,
        // put() for PUT, delete() for DELETE. There's also an exception()
        // for dealing with exceptions thrown from handlers.
        // All of this is documented on the SparkJava website (https://sparkjava.com/).
    }

    public static Call extractXMLRPCCall(String xml) {
        Call call = new Call();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList methodNameList = doc.getElementsByTagName("methodName");

            Element methodNameElement = (Element) methodNameList.item(0);
            String methodName = methodNameElement.getTextContent();
            call.name = methodName;

            NodeList i4List = doc.getElementsByTagName("i4");
            List Listi4 = new ArrayList();
//            for (int i = 0; i < i4List.getLength(); i++) {
                Element i4Element = (Element) i4List.item(0);
                String i4 = i4Element.getTextContent();
                String[] splited = i4.split("\\s+");
                for (String s : splited) {
                    Listi4.add(s);
                }
            call.args = Listi4;

        } catch (Exception e) {
//            System.out.println("#");
            e.printStackTrace();
        }

        return call;
    }
    public static int determineCalcOp(Call calcOp) {
        Calc calc = new Calc();
        String methodName = calcOp.name;
        int result;
        switch (methodName) {
            case "add":
                int[] intList = new int[calcOp.args.size()];
                for (int i = 0; i < calcOp.args.size(); i++) {
                  int curr = Integer.parseInt((calcOp.args.get(i)).toString());
                  intList[i] = curr;
        }
                result = calc.add(intList);
                return result;
            case "subtract":
                int first = Integer.parseInt((calcOp.args.get(0)).toString());
                int second = Integer.parseInt((calcOp.args.get(1)).toString());
                result = calc.subtract(first, second);
                return result;

            case "multiply":
                int[] intListMult = new int[calcOp.args.size()];
                for (int i = 0; i < calcOp.args.size(); i++) {
                    int curr = Integer.parseInt((calcOp.args.get(i)).toString());
                    intListMult[i] = curr;
                }
                result = calc.multiply(intListMult);
                return result;
            case "divide":
                int firstDiv = Integer.parseInt((calcOp.args.get(0)).toString());
                int secondDiv = Integer.parseInt((calcOp.args.get(1)).toString());
                result = calc.divide(firstDiv, secondDiv);
                return result;
            case "modulo":
                int firstMod = Integer.parseInt((calcOp.args.get(0)).toString());
                int secondMod = Integer.parseInt((calcOp.args.get(1)).toString());
                result = calc.modulo(firstMod, secondMod);
                return result;
            default:
                return 404;
        }
    }
    public static String buildXML(int result) {
        String params = "<params>";
//        for (Object arg : args) {
//            if (arg instanceof Integer) {
        params += "<param><value><i4>" + result + "</i4></value></param>";
//            } else if (arg instanceof String) {
//                params+= "<param><value><string>" + arg + "</string></value></param>";
//
// }
        params +="</params>";

        String respBody = "<?xml version='1.0'?><methodCall><methodName>" + calcOp.name + "</methodName><params>";
        respBody += params + "</params></methodCall>";

        return respBody;
    }
    //build up XML String
}
