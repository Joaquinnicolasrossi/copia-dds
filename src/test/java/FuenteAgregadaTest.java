import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteAgregadaTest {
  FuenteAgregada fuenteAgregada;
  FuenteDinamica fuenteA;
  FuenteEstaticaVictimas fuenteB;

  @BeforeEach
  public void inicializar() {
    fuenteA = mock(FuenteDinamica.class);
    fuenteB = mock(FuenteEstaticaVictimas.class);
    List<Fuente> fuentes = List.of(fuenteA, fuenteB);
    fuenteAgregada = new FuenteAgregada(fuentes, new RepoHechos());
  }

  @Test
  public void seExtraenHechosDeDistintasFuentes() {
    when(fuenteA.extraerHechos()).thenReturn(
        List.of(
            crearHechoSimple("Titulo A", "Descripcion A", "Categoria A"),
            crearHechoSimple("Titulo B", "Descripcion B", "Categoria B")
        )
    );
    when(fuenteB.extraerHechos()).thenReturn(
        List.of(
            crearHechoSimple("Titulo A", "Descripcion B", "Categoria A"),
            crearHechoSimple("Titulo C", "Descripcion C", "Categoria C")
        )
    );

    fuenteAgregada.actualizarRepositorio();
    List<Hecho> hechosPrueba = fuenteAgregada.extraerHechos();

    assertEquals(3, hechosPrueba.size());
    assertEquals("Titulo A", hechosPrueba.get(0).getTitulo());
    assertEquals("Titulo B", hechosPrueba.get(1).getTitulo());
    assertEquals("Titulo C", hechosPrueba.get(2).getTitulo());
  }

  private Hecho crearHechoSimple(String titulo, String descripcion, String categoria) {
    return new Hecho(titulo, descripcion, categoria, -34.0000, -34.0000, LocalDateTime.now(), LocalDateTime.now(), Estado.PENDIENTE);
  }
}

