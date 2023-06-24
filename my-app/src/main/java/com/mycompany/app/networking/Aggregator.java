package networking;

import networking.WebClient;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {
    //Objeto de tipo WebClient
    private WebClient webClient;

    //El constructor realiza la instancia de un objeto WebClient
    public Aggregator() {
        this.webClient = new WebClient();
    }

    /* Método SendTaskToWorkers
        Como parametros recibe:
        List<String> con las direcciones de los trabajadores (Servidores)
        List<String> con las tareas a realizar
    */ 
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {

        /*CompletableFuture se utiliza para poder usar la comunicacion asincrona
            Utiliza un arreglo futures en el cual se almacenan las respuestas futuras de los servidores
        */
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        //Se itera sobre la lista de servidores
        for (int i = 0; i < workersAddresses.size(); i++) {
            //Se obtiene la direccion del servidor actual
            String workerAddress = workersAddresses.get(i);
            //Se obtiene la tarea actual
            String task = tasks.get(i);

            //Se almacenan las tareas en un formato de byte
            byte[] requestPayload = task.getBytes();
            //Se envian las tareas asincronas con el metodo sendTask(direccion del servidor, tarea en formato de byte) y se almacenan en el arreglo futures[]
            futures[i] = webClient.sendTask(workerAddress, requestPayload);

        }

        // Evalúa continuamente si uno de los servidores ha terminado.
        boolean bandera = true;
        while(bandera){
            for(int j = 0; j < 2; j++){
                //System.out.println("futures["+j+"].isDone() = " + futures[j].isDone());
                if (true == futures[j].isDone()){
                    bandera = false;
                    //System.out.println("futures["+j+"].isDone() = " + futures[j].isDone());
                }
            }
        }

        //Creamos una lista de resultados
        List<String> results = new ArrayList();
        //Como se reciben los resultados se agregan a la lista
        for (int i = 0; i < tasks.size(); i++) {
            results.add(futures[i].join());
        }

        //Retornamos la lista de los resultados
        return results;
    }
}
