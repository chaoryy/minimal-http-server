import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 9889);

        System.out.printf(
                "Server started on %s:%d%n",
                address.getHostName(),
                address.getPort()
        );

        HttpServer server = HttpServer.create(address, 50);
        System.out.println("      successfully started");
        return server;
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", exchange -> handleRoot(exchange));
        server.createContext("/apps/", exchange -> handleApps(exchange));
        server.createContext("/apps/profile", exchange -> handleProfile(exchange));
    }

//    private static void handleRequest(HttpExchange exchange) {
//        try {
//            sendHeaders(exchange, 200, 0);
//            try (PrintWriter writer = getWriterFrom(exchange)) {
//                String method = exchange.getRequestMethod();
//                URI uri = exchange.getRequestURI();
//                String path = exchange.getHttpContext().getPath();
//
//                write(writer, "HTTP method", method);
//                write(writer, "Request", uri.toString());
//                write(writer, "Handler", path);
//                writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
//                writeData(writer, exchange);
//                writer.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void sendHeaders(HttpExchange exchange, int responseCode, int length) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(responseCode, length);
    }

    private static void handleRoot(HttpExchange exchange) {
        try {
            sendHeaders(exchange, 200, 0);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.write("Hello my friend");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleProfile(HttpExchange exchange) {
        try {
            sendHeaders(exchange, 200, 0);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                String localAddress = exchange.getLocalAddress().toString();
                String remoteAddress = exchange.getRemoteAddress().toString();
                write(writer, "Local Address", localAddress);
                write(writer, "Remote Address", remoteAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleApps(HttpExchange exchange) {
        try {
            sendHeaders(exchange, 200, 0);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(outputStream, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String body = String.format("%s: %s%n%n", msg, method);
        try {
            writer.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static BufferedReader getReader(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        return new BufferedReader(inputStreamReader);
    }

    private static void writeData(Writer writer, HttpExchange exchange) {
        try (BufferedReader reader = getReader(exchange)) {
            if (!reader.ready()) return;

            write(writer, "Data", "");
            reader.lines().forEach(line -> write(writer, "\t", line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

