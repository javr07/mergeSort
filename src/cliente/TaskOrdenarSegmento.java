package cliente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import mergesort.Ordenamiento;

/**
 *
 * @author Alex Cámara
 */
public class TaskOrdenarSegmento {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<List<Integer>> ordenarSegmento(List<Integer> segmento) throws IOException {
        return (Future<List<Integer>>) executor.submit(() -> {
            List<Integer> segmentoOrdenado = new ArrayList<>();
            Ordenamiento merge = new Ordenamiento();
            Integer[] arreglo = segmento.toArray(new Integer[segmento.size()]);
            merge.sort(arreglo);
            System.out.println("LISTA ORDENADA HILO: " + Arrays.toString(arreglo));
            segmentoOrdenado = Arrays.asList(arreglo);
            return segmentoOrdenado;
        });
    }
}
