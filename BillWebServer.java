import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;

public class BillWebServer {

    public static void main(String[] args) throws Exception {

        // Use a safe port
        HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);

        // Serve HTML page
        server.createContext("/", exchange -> {
            byte[] response = Files.readAllBytes(new File("index.html").toPath());
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        // Serve CSS file
        server.createContext("/style.css", exchange -> {
            byte[] response = Files.readAllBytes(new File("style.css").toPath());
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        // Handle bill calculation
        server.createContext("/calculate", exchange -> {

            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

            int units = Integer.parseInt(params.get("units"));
            int rate = Integer.parseInt(params.get("rate"));
            int people = Integer.parseInt(params.get("people"));

            int total = units * rate;
            int perPerson = total / people;

            String html =
    "<html>" +
    "<head>" +
    "<meta charset='UTF-8'>" +
    "<link rel='stylesheet' href='/style.css'>" +
    "</head>" +
    "<body>" +
    "<div class='box'>" +
    "<h2>Bill Summary</h2>" +
    "<hr>" +
    "<p><b>Total Electricity Bill:</b> &#8377; " + total + "</p>" +
    "<p><b>Amount Per Person:</b> &#8377; " + perPerson + "</p>" +
    "<br>" +
    "<a href='/'><button>Calculate Again</button></a>" +
    "</div>" +
    "</body>" +
    "</html>";


            byte[] response = html.getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.start();
        System.out.println("Server started at http://localhost:9999");
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            map.put(parts[0], parts[1]);
        }
        return map;
    }
}
