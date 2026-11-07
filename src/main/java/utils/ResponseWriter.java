package utils;

import com.sun.net.httpserver.HttpExchange;
import enums.ContentType;
import enums.HttpCode;

import javax.crypto.CipherOutputStream;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResponseWriter {
    public void sendHeaders(HttpExchange exchange, ContentType contentType, HttpCode code, int length) {
        try {
            exchange.getResponseHeaders().add("Content-Type", contentType.getMimeType());
            exchange.sendResponseHeaders(code.getStatusCode(), length);
        } catch (IOException e) {
            System.out.println("Error while sending Headers");
            e.printStackTrace();
        }

    }

    public void sendResponse(HttpExchange exchange, String response) {
        try (PrintWriter writer = getWriterFrom(exchange)) {
            writer.write(response);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error while sending Response");
            e.printStackTrace();
        }

    }

    public void sendResponse(HttpExchange exchange, byte[] bytes) {
        try (OutputStream outputStream = exchange.getResponseBody() ){
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("Error while sending bytes");
            e.printStackTrace();
        }
    }

    public static PrintWriter getWriterFrom(HttpExchange exchange) throws IOException {
        OutputStream outputStream = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(outputStream, false, charset);
    }
//
//    private static void write(Writer writer, String msg, String method) {
//        String body = String.format("%s: %s%n%n", msg, method);
//        try {
//            writer.write(body);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void writeHeaders(Writer writer, String type, Headers headers) {
//        write(writer, type, "");
//        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
//    }
//
//    private static void writeData(Writer writer, HttpExchange exchange) {
//        try (BufferedReader reader = getReader(exchange)) {
//            if (!reader.ready()) return;
//
//            write(writer, "Data", "");
//            reader.lines().forEach(line -> write(writer, "\t", line));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
