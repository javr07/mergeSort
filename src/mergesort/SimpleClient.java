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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javr
 */
public class SimpleClient {

   public static void main(String[] args) {
      System.out.println("Client started");
      List<Integer> segmento;
      try (Socket socket = new Socket("127.0.0.1", 5000)) {
         System.out.println("Connected to a server");
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         Object object = in.readObject();
         System.out.println("Objec" + object.toString());
         segmento = (List<Integer>) object;
         System.out.println("Aquí " + segmento.get(0));
         out.writeObject(segmento);
        
         socket.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
         Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
      }
      System.out.println("Client Terminated");
   }
}
