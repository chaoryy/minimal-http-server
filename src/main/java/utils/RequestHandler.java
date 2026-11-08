package utils;

import com.sun.net.httpserver.HttpExchange;
import enums.ContentType;
import enums.HttpCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class RequestHandler {
    private static final Path HOME_PATH = Path.of("homework").toAbsolutePath();
    private final ResponseWriter responseWriter = new ResponseWriter();

    public void handleRoot(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        if ("/".equals(requestPath)) {
            Path indexPath = HOME_PATH.resolve("index.html");
            if (Files.isRegularFile(indexPath)) {
                handleFile(exchange, indexPath);
            }
        } else {
            if (requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }
            try {
                Path basePath = HOME_PATH.toRealPath();
                Path resolved = HOME_PATH.resolve(requestPath).normalize();
                Path realResolved = resolved.toRealPath();
                if (!realResolved.startsWith(basePath) || !Files.isRegularFile(realResolved)) {
                    sendErrorResponse(exchange);
                    return;
                }
                handleFile(exchange, realResolved);
            } catch (NoSuchFileException | InvalidPathException e) {
                sendErrorResponse(exchange);
            } catch (IOException e) {
                responseWriter.sendResponse(exchange, ContentType.PLAIN, HttpCode.SERVER_ERROR, getBytesFrom(HttpCode.SERVER_ERROR));
            }
        }

    }

    public void handleProfile(HttpExchange exchange) {
        String localAddress = exchange.getLocalAddress().toString();
        String remoteAddress = exchange.getRemoteAddress().toString();
        String hostAddress = exchange.getRemoteAddress().getAddress().getHostAddress();
        String response = String.format("Local address: %s%n " +
                "Remote address: %s%n" +
                "Host address: %s%n", localAddress, remoteAddress, hostAddress);
        responseWriter.sendResponse(exchange, ContentType.PLAIN, HttpCode.OK, getBytesFrom(response));

    }

    public void handleApps(HttpExchange exchange) {
        String response = exchange.getRequestMethod();
        responseWriter.sendResponse(exchange, ContentType.PLAIN, HttpCode.OK, getBytesFrom(response));
    }

    public void handleFile(HttpExchange exchange, Path filePath) {
        try {
            ContentType contentType = ContentType.getContentTypeFrom(filePath.toString());
            byte[] bytes = Files.readAllBytes(filePath);
            responseWriter.sendResponse(exchange, contentType, HttpCode.OK, bytes);
        } catch (IOException e) {
            System.out.println("Error while handling file");
            responseWriter.sendResponse(exchange, ContentType.PLAIN, HttpCode.SERVER_ERROR, getBytesFrom(HttpCode.SERVER_ERROR));
            e.printStackTrace();
        }
    }

    private byte[] getBytesFrom(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getBytesFrom(HttpCode code) {
        return code.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void sendErrorResponse(HttpExchange exchange) {
        responseWriter.sendResponse(exchange, ContentType.PLAIN, HttpCode.NOT_FOUND, getBytesFrom(HttpCode.NOT_FOUND));
    }
}
