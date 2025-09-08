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
    fechaInicio = LocalDateTime.of(2025, 6, 1, 10, 0); // simulamos que la última consulta fue hace 2 horas

    fuenteDemoAdapter = new FuenteDemoAdapter(url, conexionMock, fechaInicio);
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

    // Esperamos exactamente la fecha que inyectamos (primera llamada)
    when(conexionMock.siguienteHecho(eq(url), eq(fechaInicio)))
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
}
