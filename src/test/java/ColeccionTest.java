import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColeccionTest {

  private Coleccion crearColeccionBase() {
    FuenteEstaticaIncendios fuenteEstaticaIncendios = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");

    CriterioCategoria criterio1 = new CriterioCategoria("Incendio Forestal");
    LocalDateTime desde = LocalDateTime.of(2018, 8, 23, 10,5);
    LocalDateTime hasta = LocalDateTime.of(2018, 9, 25, 10, 5);
    CriterioFecha criterio2 = new CriterioFecha(desde, hasta);
    List<Criterio> criterios = List.of(criterio1, criterio2);
    DetectorDeSpamFiltro deSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(deSpamFiltro);

    return new Coleccion(
        "Incendios test", "Hechos de prueba", fuenteEstaticaIncendios, criterios, repoSolicitudes);
  }

  @Test
  public void mostrarHechos() {
    Coleccion coleccion = crearColeccionBase();
    List<Hecho> hechos = coleccion.mostrarHechos();

    assertFalse(hechos.isEmpty(), "la lista filtrada no deberia estar vacia");
    assertTrue(hechos.stream().allMatch(h -> "Incendio Forestal".equals(h.getCategoria())),
        "Todos los hechos deben ser de la categoría 'Incendio Forestal'");

    assertTrue(hechos.stream().allMatch(h ->
            !h.getFecha().isBefore(LocalDateTime.of(2018, 8, 23, 10 ,5)) &&
                !h.getFecha().isAfter(LocalDateTime.of(2018, 9, 25, 10, 5))),
        "Todas las fechas deben estar entre 2018-08-23 y 2018-09-25");


  }

  @Test
  public void mostrarHechosFiltrados() {
    Coleccion coleccion = crearColeccionBase();


    LocalDateTime filtroDesde = LocalDateTime.of(2018, 9, 1, 10, 5);
    LocalDateTime filtroHasta = LocalDateTime.of(2018, 9, 15, 10 ,5);
    CriterioFecha filtro = new CriterioFecha(filtroDesde, filtroHasta);

    List<Hecho> hechosFiltrados = coleccion.mostrarHechosFiltrados(filtro);

    assertFalse(hechosFiltrados.isEmpty(), "La lista filtrada no debería estar vacía");
    assertTrue(hechosFiltrados.stream()
            .allMatch(h -> filtro.seCumpleCriterio(h)),
        "Todos los hechos deben cumplir el filtro adicional");


  }
}