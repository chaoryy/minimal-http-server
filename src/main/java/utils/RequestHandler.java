package utils;

import com.sun.net.httpserver.HttpExchange;
import enums.ContentType;
import enums.HttpCode;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RequestHandler {
    private final Path path = Path.of("homework").toAbsolutePath();
    private final ResponseWriter responseWriter = new ResponseWriter();

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

    public void handleRoot(HttpExchange exchange) {
        String response = "Hello my friend";
        responseWriter.sendHeaders(exchange, ContentType.PLAIN, HttpCode.OK, response.length());
        responseWriter.sendResponse(exchange, response);
    }

    public void handleProfile(HttpExchange exchange) {
        String localAddress = exchange.getLocalAddress().toString();
        String remoteAddress = exchange.getRemoteAddress().toString();
        String hostAddress = exchange.getRemoteAddress().getAddress().getHostAddress();
        String response = String.format("Local address: %s%n " +
                "Remote address: %s%n" +
                "Host address: %s%n", localAddress, remoteAddress, hostAddress);
        responseWriter.sendHeaders(exchange, ContentType.PLAIN, HttpCode.OK, response.length());
        responseWriter.sendResponse(exchange, response);

    }

    public void handleApps(HttpExchange exchange) {
        String response = exchange.getRequestMethod().toString();
        responseWriter.sendHeaders(exchange, ContentType.PLAIN, HttpCode.OK, response.length());
        responseWriter.sendResponse(exchange, response);
    }

    public void handleFile(HttpExchange exchange) {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }
            Path resolved = path.resolve(requestPath).normalize();
            byte[] bytes = Files.readAllBytes(resolved);
            responseWriter.sendHeaders(exchange, ContentType.HTML, HttpCode.OK, bytes.length);
            responseWriter.sendResponse(exchange, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader getReader(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        return new BufferedReader(inputStreamReader);
    }
}
