import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepoHechosTest {

  private RepoHechos repo;
  private List<Hecho> hechos;
  private Fuente fuenteMock;


  @BeforeEach
  public void inicializar() {
    repo = new RepoHechos();

    hechos = new ArrayList<>();
    fuenteMock = new FuenteDinamica(new RepoFuenteDinamica(), new RepoSolicitudesRevision());

    Hecho h1 = crearHechoSimple("tA", "dA", "cA");
    Hecho h2 = crearHechoSimple("tB", "dB", "cB");

    hechos.add(h1);
    hechos.add(h2);
  }

  @Test
  void seGuardanHechosYSePuedenConsultar() {
    repo.guardarHechos(hechos);
    List<Hecho> consulta = repo.obtenerTodosLosHechos();

    assertEquals(hechos.size(), consulta.size());
  }

  private Hecho crearHechoSimple(String titulo, String descripcion, String categoria) {
    return new Hecho(
        titulo,
        descripcion,
        categoria,
        -34.0000,
        -34.0000,
        LocalDateTime.now(),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
  }
}
