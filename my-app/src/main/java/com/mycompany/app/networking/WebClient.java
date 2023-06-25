package com.mycompany.app.networking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WebClient {
    //Objeto de tipo HttpClient
    private HttpClient client;

    public WebClient() {
        //Creamos un objeto HttpClient
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    //Metodo sendTask(direccion url del servidor, tarea en formato de byte)
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        //Creamos una solicitud HttpRequest, metodo POST
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .build();

        //Utilizamos sendAsync para enviar la solicitud de manera asincrona
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
