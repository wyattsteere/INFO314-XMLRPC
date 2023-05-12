package edu.uw.info314.xmlrpc.server;

import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathResult;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    public static String uri = "";

    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void main(String... args) throws Exception {
        String host = args[0];
        int port = Integer.valueOf(args[1]);

        uri = "http://" + host + ":" + port + "/RPC";
//        System.out.println("***");
        System.out.println(add() == 0);
        System.out.println(add(1, 2, 3, 4, 5) == 15);
        System.out.println(add(2, 4) == 6);
        System.out.println(subtract(12, 6) == 6);
        System.out.println(multiply(3, 4) == 12);
        System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        System.out.println(divide(10, 5) == 2);
        System.out.println(modulo(10, 5) == 0);
    }
    public static int add(int lhs, int rhs) throws Exception {
        return sendReq("add", lhs, rhs);
    }
    public static int add(Integer... args) throws Exception {
        return sendReq("add", (Object[])args);
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        return sendReq("subtract", lhs, rhs);
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        return sendReq("multiply", lhs, rhs);
    }
    public static int multiply(Integer... args) throws Exception {
        return sendReq("multiply", (Object[])args);
    }
    public static int divide(int lhs, int rhs) throws Exception {
        return sendReq("divide", lhs, rhs);
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        return sendReq("modulo", lhs, rhs);
    }

    public static int sendReq(String calcOp, Object... args) throws Exception {
        String parameters = "       <params>\r\n";
        for (Object arg : args) {
            if (arg instanceof Integer) {
                parameters += "<param><value><i4>" + arg + "</i4></value></param>\r\n";
            } else if (arg instanceof String) {
                parameters += "<param><value><string>" + arg + "</string></value></param>\r\n";
            }
        }
        parameters += "     </params>";

        String reqBody = "<?xml version='1.0'?>\r\n<methodCall>\r\n<methodName>" + calcOp + "</methodName>\r\n";
        reqBody += parameters + "       </params>\r\n</methodCall>";



//        System.out.println(reqBody);

        HttpRequest httpReq = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/xml")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        HttpResponse<String> httpResp = HttpClient.newHttpClient().send(httpReq, HttpResponse.BodyHandlers.ofString());
        return responseHandler(httpResp.body());
    }

    public static int responseHandler(String response) throws Exception {
        System.out.println(response);
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();

        ByteArrayInputStream input =  new ByteArrayInputStream(
                response.getBytes("UTF-8"));
        Document doc = docBuilder.parse(input);

        XPath xPath =  XPathFactory.newInstance().newXPath();
        String expression = "/methodResponse/params/param/value/i4";
        Node node = (Node) xPath.compile(expression).evaluate(
                doc, XPathConstants.NODE);
        int intValue = Integer.parseInt(node.getTextContent());
        return intValue;
    }
}
