package servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
public class SimpleThreadServerCallableFuture {

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
    private static void guardarArchivoInicio(List<Integer> arreglo) throws IOException {
      String userHomeFolder = System.getProperty("user.home");
      File textFile = new File(userHomeFolder, "ArregloOriginal.txt");
      BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
      try {
         out.write(arreglo.toString());
      } finally {
         out.close();
      }
   }

   private static void guardarArchivoFinal(Integer[] arreglo) throws IOException {
      String userHomeFolder = System.getProperty("user.home");
      File textFile = new File(userHomeFolder, "ArregloFinal.txt");
      BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
      try {
         out.write(Arrays.toString(arreglo));
      } finally {
         out.close();
      }
   }

   public static void main(String[] args) throws InterruptedException, ExecutionException {
      List<Socket> sockets = new ArrayList<>();
      List<Integer> numeros = new ArrayList<>();
      List<Future<List<Integer>>> futures = new ArrayList<>();
      int numClientsActive = 0;
      int numClientsRequired = 4;

      try {
         ServerSocket serverSocket = new ServerSocket(5000);
         while (numClientsActive < numClientsRequired) {
            System.out.println("Esperando a " + (numClientsRequired - numClientsActive) +" clientes");
            Socket socket = serverSocket.accept();
            sockets.add(socket);
            System.out.println("Conectado con el cliente " + numClientsActive);
            numClientsActive++;
         }

         int longitud = establecerLongitud();
         numeros = prepararNumeros(numeros, longitud);
         guardarArchivoInicio(numeros);
         int rango = longitud / numClientsRequired;
         int inicio = 0;
         int fin = rango;
         TaskClient task = new TaskClient();
         for (int i = 0; i < numClientsActive; i++) {

            List<Integer> segmentoNums = new ArrayList<>(numeros.subList(inicio, fin));
            System.out.println("Segmento enviado a cliente: " + sockets.get(i).toString());
            Future<List<Integer>> future = task.ordenarSegmento(segmentoNums, sockets.get(i));

            futures.add(future);
            inicio = fin;
            fin = fin + rango;
         }

         List<Integer> listaResultados = new ArrayList<>();

         System.out.println("Esperando resultados");
         while (true) {
            List<Integer> nuevaLista = null;
            if (checkFutures(futures, numClientsActive)) {

               for (int i = 0; i < numClientsActive; i++) {
                  nuevaLista = futures.get(i).get();
                  listaResultados.addAll(nuevaLista);
               }
               break;
            }
         }

         Ordenamiento merge = new Ordenamiento();
         Integer[] arreglo = listaResultados.toArray(new Integer[listaResultados.size()]);
         merge.sort(arreglo);
         System.out.println("Resultado guardado home");
         guardarArchivoFinal(arreglo);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
