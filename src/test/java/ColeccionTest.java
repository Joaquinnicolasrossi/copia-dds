import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColeccionTest {

  private Coleccion crearColeccionBase() {
    Fuente fuente = new Fuente("src/test/resources/fires-all.csv");
    LocalDate desde = LocalDate.of(2018, 8, 23);
    LocalDate hasta = LocalDate.of(2018, 9, 25);
    Criterio criterio = new Criterio("Incendio Forestal", desde, hasta);
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes();
    return new Coleccion("Incendios test", "Hechos de prueba", fuente, criterio, repoSolicitudes);
  }

  @Test
  public void mostrarHechos() {
    Coleccion coleccion = crearColeccionBase();
    List<Hecho> hechos = coleccion.mostrarHechos();



    assertFalse(hechos.isEmpty() , "la lista filtrada no deberia estar vacia");
    assertTrue(hechos.stream().allMatch(h -> "Incendio Forestal".equals(h.getCategoria())),
        "Todos los hechos deben ser de la categoría 'Incendio Forestal'");

    assertTrue(hechos.stream().allMatch(h ->
            !h.getFecha().isBefore(LocalDate.of(2018, 8, 23)) &&
                !h.getFecha().isAfter(LocalDate.of(2018, 9, 25))),
        "Todas las fechas deben estar entre 2018-08-23 y 2018-09-25");
    System.out.println("RESULTADOS DEL FILTRO");
    hechos.forEach(System.out::println);
  }

  @Test
  public void mostrarHechosFiltrados() {
    Coleccion coleccion = crearColeccionBase();

    LocalDate filtroDesde = LocalDate.of(2018, 9, 1);
    LocalDate filtroHasta = LocalDate.of(2018, 9, 15);
    Criterio filtroAdicional = new Criterio("Incendio Forestal", filtroDesde, filtroHasta);

    List<Hecho> hechosFiltrados = coleccion.mostrarHechosFiltrados(filtroAdicional);


    assertFalse(hechosFiltrados.isEmpty(), "La lista filtrada no debería estar vacía");
    assertTrue(hechosFiltrados.stream()
            .allMatch(h -> filtroAdicional.seCumpleCriterio(h)),
        "Todos los hechos deben cumplir el filtro adicional");
    System.out.println("RESULTADOS FILTRO ADICIONAL");
    hechosFiltrados.forEach(System.out::println);

  }
}