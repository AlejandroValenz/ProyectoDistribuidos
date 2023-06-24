import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.*;
import java.lang.*;

public class AuxServer {

    /*
        Definimos las dos cadenas de los endpoints del servidor
    */
    private static final String ENDPOINT_PROCESS = "/procesar_datos";

    /*
        Variables del servidor.
        port -> Puerto
        HTTPServer -> servidor HTTP
    */
    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        //Puerto default del servidor
        int serverPort = 8080;
        //Si se envian algun parametro por la linea de comandos, este se utiliza como el puerto
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        //Instanciamos un objeto de tipo WebServer
        AuxServer webServer = new AuxServer(serverPort);
        //Ejecutamos el metodo principal de WebServer, el cual es startServer(). Este método inicializa la configuración del servidor
        webServer.startServer();

        //Imprime el puerto de escucha del servidor
        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    //Constructor de WebServer, el cual recibe como parametro el puerto e inicializa la variable privada port
    public AuxServer(int port) {
        this.port = port;
    }

    //Método startServer
    public void startServer() {
        try {
            //El objeto HTTPServer lo igualamos con el objeto resultante de create, el cual crea una instancia de Socket TCP vinculada a una IP y al puerto port
            
            //El primer parametro es el puerto, mientras que el segundo es el tamaño de la lista de solicitudes pendientes que permite el servidor HTTP mantener en una lista de espera. Si se define cero '0' la desicion la toma el sistema
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //HttpContext representa un mapeo entre el URI(Uniform Resource Identifier) y un HttpHandler(Interfaz que se invoca cada vez que se procesa una transaccion HTTP)

        //createContext crea un objeto HttpContext sin un HttpHnadler asociado, pero con la ruta relativa asignada
        HttpContext taskContext = server.createContext(ENDPOINT_PROCESS);

        //setHandler recibe como parametro el metodo que implementa el manejador y vincula el handler para dicho contexto si aun no se inicializa
        taskContext.setHandler(this::handleTaskRequest);

        //setExecutor permite establecer un objeto de tipo Executor para el servidor. Es necesario antes de iniciarlo.
        //newFixedThreadPool define un pool de hilos y deja que el executor los inicialice y  asigne las tareas
        server.setExecutor(Executors.newFixedThreadPool(8));
        //Inicia la ejecución del servidor en un nuevo hilo en segundo plano
        server.start();
    }

    //Manejador del endpoint task. El argumento encapsula todo lo relacionado con la transaccion HTTP actual entre el servidor y el cliente
    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { 
            exchange.close();
            return;
        }

        try {
            byte[] requestBytes = exchange.getRequestBody().readAllBytes();
            byte[] responseBytes = String.format("Palabra del servidor de procesamiento: %s\n", calculateResponse(requestBytes)).getBytes();
            sendResponse(responseBytes, exchange);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    //
    private byte[] calculateResponse(byte[] requestBytes) {
        System.out.println("------------------------------------------------");
        String bodyString = new String(requestBytes);
        String[] stringWords = bodyString.split(" ");

        System.out.println("Frase recibida: " + bodyString);
        
        for (String palabra : stringWords) {
            System.out.println(palabra);
        }

        return bodyString.getBytes();
    }

    //Metodo 'sendResponse' agrega el status code 200 (estado de exito) y la longitud de la respuesta
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        //Crea un stream para el cuerpo de la respuesta
        OutputStream outputStream = exchange.getResponseBody();
        //Se escribe en el cuerpo del mensaje
        outputStream.write(responseBytes);
        //Realiza un limpieza
        outputStream.flush();
        //Cierra el stream
        outputStream.close();
        //Cierra el exchange
        exchange.close();
    }

}