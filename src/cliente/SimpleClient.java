/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mergesort.Ordenamiento;

/**
 *
 * @author javr
 */
public class SimpleClient {

    public static void main(String[] args) {
        System.out.println("Client started");
        List<Integer> segmento;
        List<Future<List<Integer>>> futures = new ArrayList<>();
        try (Socket socket = new Socket("127.0.0.1", 5000)) {
            System.out.println("Connected to a server");
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            //recibir segmento
            Object object = in.readObject();

            System.out.println("Object" + object.toString());
            segmento = (List<Integer>) object;

            List<Integer> segmento1;
            List<Integer> segmento2;
            List<Integer> listaFinal = new ArrayList<>();

            int mitad = segmento.size() / 2;

            segmento1 = segmento.subList(0, mitad);
            segmento2 = segmento.subList(mitad, segmento.size());

            System.out.println("segmento 1: " + segmento1);
            System.out.println("segmento 2: " + segmento2);

            TaskOrdenarSegmento task = new TaskOrdenarSegmento();

            Future<List<Integer>> future1 = task.ordenarSegmento(segmento1);
            
            Future<List<Integer>> future2 = task.ordenarSegmento(segmento2);
            
            while(!future1.isDone() && !future2.isDone()){
                
            }
            
            try {
                segmento1 = future1.get();
                segmento2 = future2.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            listaFinal.addAll(segmento1);
            listaFinal.addAll(segmento2);
            
            Ordenamiento merge = new Ordenamiento();

            Integer[] arreglo = listaFinal.toArray(new Integer[listaFinal.size()]);

            merge.sort(arreglo);

            System.out.println(Arrays.toString(arreglo));

            listaFinal = Arrays.asList(arreglo);

            System.out.println("segmento: " + listaFinal);
            enviarRespuesta(listaFinal, out, socket);

        } catch (IOException | ClassNotFoundException ex) {
        }
        System.out.println("Client Terminated");
    }

    public static void enviarRespuesta(List<Integer> segmento, ObjectOutputStream out, Socket socket) {
        try {
            out.writeObject(segmento);
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
