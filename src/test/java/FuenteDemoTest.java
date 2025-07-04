import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteDemoTest {

  private Conexion conexionMock;
  private FuenteDemoAdapter fuenteDemoAdapter;
  private URL url;
  private LocalDateTime fechaInicio;

  @BeforeEach
  public void setUp() throws Exception {
    conexionMock = mock(ConexionGenerica.class);
    url = new URL("http://localhost/fuente-demo");
    fechaInicio = LocalDateTime.now().minusHours(2); // simulamos que la última consulta fue hace 2 horas

    fuenteDemoAdapter = new FuenteDemoAdapter(url, conexionMock);
  }

  @Test
  public void extraerHechosDevuelveListaConUnHecho() {
    // Simulamos lo que devuelve la fuente externa (como si fuera un JSON parseado)
    Map<String, Object> hechoMap = new HashMap<>();
    hechoMap.put("titulo", "Corte de luz");
    hechoMap.put("descripcion", "Zona sin suministro eléctrico");
    hechoMap.put("categoria", "Servicio");
    hechoMap.put("latitud", -34.6037);
    hechoMap.put("longitud", -58.3816);
    hechoMap.put("fecha", LocalDate.of(2025, 6, 1));
    hechoMap.put("estado", "PENDIENTE");

    // Primera llamada devuelve un hecho, la segunda null para cortar el bucle
    when(conexionMock.siguienteHecho(eq(url), any(LocalDateTime.class)))
        .thenReturn(hechoMap)
        .thenReturn(null);

    fuenteDemoAdapter.actualizarHechos(); // no podemos testear cron
    List<Hecho> hechos = fuenteDemoAdapter.extraerHechos();

    assertEquals(1, hechos.size());

    Hecho hecho = hechos.get(0);
    assertEquals("Corte de luz", hecho.getTitulo());
    assertEquals("Zona sin suministro eléctrico", hecho.getDescripcion());
    assertEquals("Servicio", hecho.getCategoria());
    assertEquals(-34.6037, hecho.getLatitud());
    assertEquals(-58.3816, hecho.getLongitud());
    assertEquals(LocalDate.of(2025, 6, 1), hecho.getFecha());
    assertEquals(Estado.PENDIENTE, hecho.getEstado());
  }

//  @Test
//  void extraer2HechosConLlamadaAlScheduler() throws Exception {
//    // Creamos dos Mapas distintos que el mock devolverá en secuencia:
//    Map<String, Object> mapa1 = new HashMap<>();
//    mapa1.put("titulo", "H1");
//    mapa1.put("descripcion", "D1");
//    mapa1.put("categoria", "C1");
//    mapa1.put("latitud", 1.0);
//    mapa1.put("longitud", 2.0);
//    mapa1.put("fecha", LocalDate.of(2025,6,1));
//    mapa1.put("estado", "PENDIENTE");
//
//    Map<String, Object> mapa2 = new HashMap<>();
//    mapa2.put("titulo", "H2");
//    mapa2.put("descripcion", "D2");
//    mapa2.put("categoria", "C2");
//    mapa2.put("latitud", 3.0);
//    mapa2.put("longitud", 4.0);
//    mapa2.put("fecha", LocalDate.of(2025,6,2));
//    mapa2.put("estado", "PENDIENTE");
//
//    // Secuencia de retornos:
//    //   - Primera ejecución: devuelve mapa1, después null.
//    //   - Segunda ejecución: devuelve mapa2, después null.
//    when(conexionMock.siguienteHecho(any(URL.class), any(LocalDateTime.class)))
//        .thenReturn(mapa1)
//        .thenReturn(null)
//        .thenReturn(mapa2)
//        .thenReturn(null)
//        .thenReturn(null);
//
//    // Chequeo que la lista este vacia al comienzo
//    assertTrue(fuenteDemoAdapter.extraerHechos().isEmpty());
//
//    // Arrancamos el scheduler: cada 200ms ejecutará extraerHechos() y
//    // agregará al buffer todo lo que devuelva.
//    // fuenteDemoAdapter.startScheduler();
//
//    // Esperamos un poco más de 400 ms para garantizar que ocurran al menos dos ciclos:
//    Thread.sleep(450);
//
//    // Tomamos el snapshot del buffer acumulado
//    List<Hecho> listaHechosActualizada = fuenteDemoAdapter.extraerHechos();
//
//    // Dado que en dos ciclos devolvimos 2 hechos distintos, deberían estar ahí los dos.
//    assertEquals(2, listaHechosActualizada.size(), "El buffer debería tener exactamente dos hechos");
//
//    // Verificamos títulos/pedazos de cada hecho:
//    boolean encontroH1 = listaHechosActualizada.stream().anyMatch(h -> h.getTitulo().equals("H1"));
//    boolean encontroH2 = listaHechosActualizada.stream().anyMatch(h -> h.getTitulo().equals("H2"));
//    assertTrue(encontroH1, "Debe haber aparecido el Hecho con título 'H1'");
//    assertTrue(encontroH2, "Debe haber aparecido el Hecho con título 'H2'");
//
//    // Finalmente, detenemos el scheduler
//    // fuenteDemoAdapter.stopScheduler();
//
//    // Guardamos cuántos hay ahora
//    int tamListaHechosFinal = fuenteDemoAdapter.extraerHechos().size();
//
//    // Esperamos 300ms adicionales para asegurarnos de que, al haber detenido el scheduler,
//    // no se agreguen más elementos:
//    Thread.sleep(300);
//
//    int tamListaHechosDespuesPause = fuenteDemoAdapter.extraerHechos().size();
//    assertEquals(tamListaHechosFinal, tamListaHechosDespuesPause,
//        "Después de detener el scheduler, el buffer no debería crecer más");
//  }
}
