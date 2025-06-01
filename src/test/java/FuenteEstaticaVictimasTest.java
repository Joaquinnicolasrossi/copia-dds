import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FuenteEstaticaVictimasTest {
  @Test
  void extraerHechosCargaCorrectamenteDesdeCSV() {

    // Creamos la clase fuente y le cargamos la ruta del csv
    FuenteEstaticaVictimas fuenteEstaticaVictimas = new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");

    // Creamos una lista de hechos y llamamos al metodo extraer
    List<Hecho> hechos = fuenteEstaticaVictimas.extraerHechos();

    // Pregunta si la lista está vacía, si es el caso falla
    assertFalse(hechos.isEmpty());
    Hecho primero = hechos.get(4);

    assertEquals("Víctima en Neuquén, Neuquén", primero.getTitulo());
    assertEquals("PICUN LEUFU 19:37:00", primero.getDescripcion()); // Codigo causa_desc 40 --> hoguera
    assertEquals("Accidente Vial", primero.getCategoria());
    assertEquals(-39, primero.getLatitud());
    assertEquals(-68, primero.getLongitud());
    assertEquals(LocalDate.of(2017, 1, 27), primero.Fecha());

  }

  @Test
  void hechoTieneLatYLongPorDefecto(){

    FuenteEstaticaVictimas fuenteEstaticaVictimas = new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");

    List<Hecho> hechos = fuenteEstaticaVictimas.extraerHechos();

    Hecho h = hechos.get(18);
    assertEquals(0.0, h.getLatitud());
    assertEquals(0.0, h.getLongitud());

  }

  @Test
  void seCarganTodosLosHechosDelCSV(){

    FuenteEstaticaVictimas fuenteEstaticaVictimas = new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");

    List<Hecho> hechos = fuenteEstaticaVictimas.extraerHechos();

    assertEquals(52027, hechos.size());

  }
}
