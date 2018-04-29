package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import mergesort.Ordenamiento;

/**
 *
 * @author javr
 */
public class SimpleThreadPoolServerCallableFuture {

    private static List<Integer> prepararNumeros(List<Integer> numeros, int longitud) {
        int min = 0;
        int max = 333;
        int randomNum;
        for (int i = 0; i < longitud; i++) {
            randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            numeros.add(randomNum);
        }
        return numeros;
    }

    private static int establecerLongitud() {
        int longitud;
        int min = 4;
        int max = 40;
        do {
            longitud = ThreadLocalRandom.current().nextInt(min, max + 1);
        } while (!((longitud % 4) == 0));
        return longitud;
    }

    private static boolean checkFutures(List<Future<List<Integer>>> futures, int numClients) {
        boolean check = true;
        for (int i = 0; i < numClients; i++) {
            if (!futures.get(i).isDone()) {
                check = false;
                break;
            }
        }
        return check;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Socket> sockets = new ArrayList<>();
        List<Integer> numeros = new ArrayList<>();
        List<Future<List<Integer>>> futures = new ArrayList<>();
        int numClients = 0;

        //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (numClients < 3) {
                System.out.println("Waiting for a client connection");
                Socket socket = serverSocket.accept();
                sockets.add(socket);
                System.out.println("Connected to a client " + numClients);
                numClients++;
            }

            int longitud = establecerLongitud();
            numeros = prepararNumeros(numeros, longitud);
            int rango = longitud / 4;
            int inicio = 0;

            System.out.println("numeros " + numeros.toString());
            TaskClient task = new TaskClient();
            for (int i = 0; i < numClients; i++) {

                List<Integer> segmentoNums = new ArrayList<>(numeros.subList(inicio, rango));
                System.out.println("Segmento creado " + segmentoNums.toString());
                System.out.println("Sock " + sockets.get(i).toString());
                Future<List<Integer>> future = task.ordenarSegmento(segmentoNums, sockets.get(i));

                futures.add(future);
                inicio = rango;
                rango = rango + rango;
            }

            List<Integer> listaResultados = new ArrayList<>();

            System.out.println("Esperando");
            while (true) {
                List<Integer> nuevaLista = null;
                if (checkFutures(futures, numClients)) {

                    for (int i = 0; i < numClients; i++) {
                        nuevaLista = futures.get(i).get();
                        System.out.println("lISTA " + nuevaLista.toString());
                        listaResultados.addAll(nuevaLista);
                    }
                    break;
                }
            }

            Ordenamiento merge = new Ordenamiento();
            Integer[] arreglo = listaResultados.toArray(new Integer[listaResultados.size()]);
            merge.sort(arreglo);
            System.out.println("LISTA ORDENADA FINAL: " + Arrays.toString(arreglo));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //executor.shutdown();
        System.out.println("Terminated");
    }

}
