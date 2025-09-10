import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsensoTest {
  private Hecho hechoComun;
  private Hecho hechoVariado;
  private Hecho hechoUnico;

  FuenteEstaticaIncendios fuente1;
  FuenteEstaticaVictimas fuente2;
  FuenteAgregada fuente3;
  FuenteDinamica fuenteA;
  FuenteEstaticaVictimas fuenteB;

  private FuenteAgregada fuenteAgregada;

  @BeforeEach
  void setUp() {


    hechoComun = new Hecho(
        "Incendio en zona rural",
        "Fuego de gran magnitud en la provincia de Córdoba.",
        "Incendio",
        -31.4167,
        -64.1833,
        LocalDate.of(2025, 6, 1),
        LocalDate.now(),
        Estado.PENDIENTE
    );

    hechoVariado = new Hecho(
        "Incendio en zona rural",
        "Fuego de gran magnitud en la provincia de Córdoba.",
        "Incendio",
        -31.4167,
        -64.1833,
        LocalDate.of(2025, 6, 1),
        LocalDate.now(),
        Estado.PENDIENTE
    );

    hechoUnico = new Hecho(
        "Incendio en zona rural",
        "Otro foco distinto con igual título.",
        "Incendio",
        -30.0000,
        -60.0000,
        LocalDate.of(2025, 6, 2),
        LocalDate.now(),
        Estado.PENDIENTE
    );

    fuente1 = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
    fuente2 = new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");
    fuenteA = mock(FuenteDinamica.class);
    fuenteB = mock(FuenteEstaticaVictimas.class);
    List<Fuente> fuentes = List.of(fuenteA, fuenteB);
    fuente3 = new FuenteAgregada(fuentes, new RepoHechos());
    fuenteAgregada = new FuenteAgregada(List.of(fuente1, fuente2, fuente3), new RepoHechos());
  }

  @Test
  void testMultiplesMenciones() {
    AlgoritmoConsenso algoritmo = new MultiplesMenciones();

    // Está en 2 fuentes, pero el titulo es variante
    assertFalse(algoritmo.estaConsensuado(hechoComun, fuenteAgregada),
        "No debe estar consensuado si hay versiones con mismo título y distinto contenido");
  }

  /*@Test
  void testMayoríaSimple() {
    AlgoritmoConsenso algoritmo = new MayoriaSimple();

    // hechoComun --> está en 2 de 3 fuentes (Mayor de la mitad)
    assertTrue(algoritmo.estaConsensuado(hechoComun, fuenteAgregada));

    // hechoUnico --> está en una sola fuente
    assertFalse(algoritmo.estaConsensuado(hechoUnico, fuenteAgregada));
  }
  */
  @Test
  void testConsensoAbsoluto() {
    AlgoritmoConsenso algoritmo = new Absoluto();

    // hechoComun está en 2 de 3 (no abosluto)
    assertFalse(algoritmo.estaConsensuado(hechoComun, fuenteAgregada));

    // hechoUnico está solo en una (no absoluto)
    assertFalse(algoritmo.estaConsensuado(hechoUnico, fuenteAgregada));
  }
}