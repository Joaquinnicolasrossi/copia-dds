import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FuenteTest {

  @Test
  void extraerHechosCargaCorrectamenteDesdeCSV() {

    // Creamos la clase fuente y le cargamos la ruta del csv
    Fuente fuente = new Fuente("src/test/resources/fires-all.csv");

    // Creamos una lista de hechos y llamamos al metodo extraer
    List<Hecho> hechos = fuente.extraerHechos();

    // Pregunta si la lista está vacía, si es el caso falla
    assertFalse(hechos.isEmpty());
    Hecho primero = hechos.get(24);

    assertEquals("Fuego intencionado en INDETERMINADO", primero.getTitulo());
    assertEquals("", primero.getDescripcion()); // Codigo causa_desc 40 --> hoguera
    assertEquals("Incendio Forestal", primero.getCategoria());
    assertEquals(0.0, primero.getLatitud());
    assertEquals(0.0, primero.getLongitud());
    assertEquals(LocalDate.of(1968, 1, 19), primero.Fecha());

  }

  @Test
  void hechoTieneLatYLongPorDefecto(){

    Fuente fuente = new Fuente("src/test/resources/fires-all.csv");

    List<Hecho> hechos = fuente.extraerHechos();

    Hecho h = hechos.get(0);
    assertEquals(0.0, h.getLatitud());
    assertEquals(0.0, h.getLongitud());

  }

  @Test
  void seCarganTodosLosHechosDelCSV(){

    Fuente fuente = new Fuente("src/test/resources/fires-all.csv");

    List<Hecho> hechos = fuente.extraerHechos();

    assertEquals(284589, hechos.size());

  }
}