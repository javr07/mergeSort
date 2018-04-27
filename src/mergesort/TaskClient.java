/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mergesort;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author javr
 */
class TaskClient {

   public TaskClient() {

   }

   private final ExecutorService executor = Executors.newSingleThreadExecutor();

   public Future<List<Integer>> ordenarSegmento(List<Integer> segmento, Socket socket) throws IOException {
      return (Future<List<Integer>>) executor.submit(() -> {
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         out.writeObject(segmento);
         List<Integer> segmentoOrdenado;
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
         Object object = in.readObject();
         segmentoOrdenado = (List<Integer>) object;
         return segmento;
      });
   }

}
