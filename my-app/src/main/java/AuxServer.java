import com.mycompany.app.FrontendSearchResponse;
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

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
        String[] books = {"Adler_Olsen,_Jussi__1997_._La_casa_del_alfabeto_[7745].txt",
                "Adler,_Elizabeth__1991_._La_esmeralda_de_los_Ivanoff_[10057].txt",
                "Aguilera,_Juan_Miguel__1998_._La_locura_de_Dios_[5644].txt",
                "Alameddine,_Rabih__2008_._El_contador_de_historias_[5735].txt",
                "Albom,_Mitch__2002_._Martes_con_mi_viejo_profesor_[382].txt",
                "Alcott,_Louisa_May__1868_._Mujercitas_[11086].txt",
                "Alcott,_Louisa_May__1871_._Hombrecitos_[15392].txt",
                "Alders,_Hanny__1987_._El_tesoro_de_los_templarios_[13014].txt",
                "Alexander,_Caroline__1998_._Atrapados_en_el_hielo_[15727].txt",
                "Allende,_Isabel__1982_._La_casa_de_los_espíritus_[563].txt",
                "Allende,_Isabel__1984_._De_amor_y_de_sombra_[6283].txt",
                "Alten,_Steve__2001_.__Trilogía_maya_01__El_testamento_maya_[8901].txt",
                "Alten,_Steve__2008_._Al_borde_del_infierno_[12141].txt",
                "Amis,_Martin__1990_._Los_monstruos_de_Einstein_[8080].txt",
                "Anderson,_Sienna__2008_._No_me_olvides_[15047].txt",
                "Anónimo__1554_._Lazarillo_de_Tormes_[11043].txt",
                "Anónimo__2004_._Robin_Hood_[11853].txt",
                "Archer,_Jeffrey__1979_._Kane_y_Abel_[1965].txt",
                "Asimov,_Isaac__1950_._Yo,_robot_[10874].txt",
                "Asimov,_Isaac__1967_._Guía_de_la_Biblia__Antiguo_Testamento__[6134].txt",
                "Asimov,_Isaac__1985_._El_monstruo_subatómico_[167].txt",
                "Bach,_Richard__1970_._Juan_Salvador_Gaviota_[15399].txt",
                "Baum,_Lyman_Frank__1900_._El_Mago_de_Oz_[15715].txt",
                "Beevor,_Antony__1998_._Stalingrado_[10491].txt",
                "Benítez,_J._J.__1984_.__Caballo_de_Troya_01__Jerusalén_[4826].txt",
                "Dickens,_Charles__1843_._Cuento_de_Navidad_[3285].txt",
                "Dostoievski,_Fiódor__1865_._Crimen_y_castigo_[13400].txt",
                "Ende,_Michael__1973_._Momo_[1894].txt",
                "Esquivel,_Laura__1989_._Como_agua_para_chocolate_[7750].txt",
                "Flaubert,_Gustave__1857_._Madame_Bovary_[3067].txt",
                "Fromm,_Erich__1947_._El_miedo_a_la_libertad_[11619].txt",
                "Gaarder,_Jostein__1991_._El_mundo_de_Sofía_[6571].txt",
                "Gaiman,_Neil__2002_._Coraline_[1976].txt",
                "García_Márquez,_Gabriel__1967_._Cien_años_de_soledad_[8376].txt",
                "García_Márquez,_Gabriel__1985_._El_amor_en_los_tiempos_del_cólera_[874].txt",
                "García_Márquez,_Gabriel__1989_._El_general_en_su_laberinto_[875].txt",
                "Golding,_William__1954_._El_señor_de_las_moscas_[6260].txt",
                "Goleman,_Daniel__1995_._Inteligencia_emocional_[4998].txt",
                "Gorki,_Máximo__1907_._La_madre_[1592].txt",
                "Harris,_Thomas__1988_._El_silencio_de_los_inocentes_[11274].txt",
                "Hawking,_Stephen__1988_._Historia_del_tiempo_[8536].txt",
                "Hemingway,_Ernest__1952_._El_viejo_y_el_mar_[1519].txt",
                "Hesse,_Herman__1919_._Demian_[2612].txt",
                "Hitler,_Adolf__1935_._Mi_lucha_[11690].txt",
                "Hobbes,_Thomas__1651_._Leviatán_[2938].txt",
                "Huxley,_Aldous__1932_._Un_mundo_feliz_[293].txt"};

        String linea = "";
        int count = 0;
        int palabrasTotales = 0;
        double fdt;

        //Almacema < Palabra, Apariciones >
        LinkedHashMap<String, Integer> listaPalabras = new LinkedHashMap<>();
        //Almacena < Libro, fdt >
        LinkedHashMap<String, Double> listaOcurrencias = new LinkedHashMap<>();
        //Almacena < Libro, fdt >
        ArrayList<Libro> result = new ArrayList<Libro>();
        
        //Agregamos cuales seran las palbras a buscar en el texto
        for (String palabra : stringWords) {
            listaPalabras.put(palabra.toLowerCase(), 0);
        }

        System.out.println("Palabras al inicio:" + listaPalabras);
        for(String a : books){
            try{
                
                String path = "/mnt/c/Users/paole/OneDrive/Escritorio/ESCOM/Distribuidos/Proyect_final/ProyectoDistribuidos/my-app/src/main/resources/books/" + a;

                BufferedReader miBuffer = new BufferedReader( new InputStreamReader(new FileInputStream(path), "UTF-8") );
                
                while ( (linea = miBuffer.readLine()) != null ){
                    //System.out.println("Entrada while");
                    
                    String[] palabra = linea.toLowerCase().split(" ");

                    for(String palabraAnalisis : palabra){
                        //Quitamos los signos de puntuación a las palabras
                        palabraAnalisis = palabraAnalisis.replaceAll(",","");
                        palabraAnalisis = palabraAnalisis.replaceAll("\\.","");
                        palabraAnalisis = palabraAnalisis.replaceAll(";","");
                        palabraAnalisis = palabraAnalisis.replaceAll(":","");
                        palabraAnalisis = palabraAnalisis.replaceAll("\\)","");
                        palabraAnalisis = palabraAnalisis.replaceAll("!","");
                        palabraAnalisis = palabraAnalisis.replaceAll("-","");
                        palabraAnalisis = palabraAnalisis.replaceAll("\\?","");
                        //System.out.println("Entrada for1");
                        palabrasTotales++;
                        for(Map.Entry<String, Integer> mapa : listaPalabras.entrySet()){
                            //System.out.println("Entrada for2");
                            if(palabraAnalisis.equalsIgnoreCase(mapa.getKey())){
                                //System.out.println("Analisis: " + palabraAnalisis + " Map: " + mapa.getValue());
                                listaPalabras.put(palabraAnalisis, mapa.getValue() + 1);
                                count++;
                            } else {
                                //System.out.println("Analisis: " + palabraAnalisis + " Map: " + mapa.getValue());
                            }
                            
                        }
                        
                    }

                }
                System.out.println("Puntuacion: " + count);
                System.out.println("Palabras totales: " + palabrasTotales);
                System.out.println("Texto analizado.");
                fdt = (double)count / (double)palabrasTotales;
                listaOcurrencias.put(a, fdt);
                    
            } catch (IOException e) {
                System.out.println("Error lectura");
            }
        }

        for(Map.Entry<String, Double> mapaToResult : listaOcurrencias.entrySet()){
            
            result.add(new Libro(mapaToResult.getKey(), mapaToResult.getValue()));

        }
        
        System.out.println("Original");
        for(int i = 0; i < result.size(); i ++)
            System.out.println(result.get(i));
        
        Comparator c
            = Collections.reverseOrder(new SortByOcurrencias());
        Collections.sort(result, c);
        
        System.out.println("Orden");
        
for(int i = 0; i < result.size(); i ++)
            System.out.println(result.get(i));
        /* creo un vector tipo string para guardar los 3 titulos con mayor incidencia */
        String libros[];
        libros = new String[3];

        System.out.println("Los 3 resultados con mayor tdf son: ");
        
        for(int i = 0; i < 3; i++){
            libros[i]=result.get(i).toString();
            System.out.println(libros[i]);
        }
        /* webServer */
        FrontendSearchResponse res = new FrontendSearchResponse("");
        res.getNames(libros[0], libros[1], libros[2]);
        
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