import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepoHechosTest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoHechos repo;
  private List<Hecho> hechos;
  private Fuente fuenteMock;

  @BeforeAll
  static void initFactory() {
    emf = Persistence.createEntityManagerFactory("testPU");
  }

  @AfterAll
  static void closeFactory() {
    emf.close();
  }

  @BeforeEach
  public void inicializar() {
    em = emf.createEntityManager();
    repo = new RepoHechos();
    repo.setEntityManager(em);

    hechos = new ArrayList<>();
    fuenteMock = new FuenteDinamica(new RepoFuenteDinamica(), new RepoSolicitudesRevision());

    em.getTransaction().begin();
    em.persist(fuenteMock);
    em.getTransaction().commit();

    Hecho h1 = crearHechoSimple("tA", "dA", "cA");
    h1.setFuenteOrigen(fuenteMock);

    Hecho h2 = crearHechoSimple("tB", "dB", "cB");
    h2.setFuenteOrigen(fuenteMock);

    hechos.add(h1);
    hechos.add(h2);
  }

  @AfterEach
  void cerrarEntityManager() {
    if (em.isOpen()) {
      em.close();
    }
  }

  @Test
  void seGuardanHechosYSePuedenConsultar() {
    em.getTransaction().begin();
    repo.guardarHechos(hechos);
    em.getTransaction().commit();

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
        LocalDate.now(),
        LocalDate.now(),
        Estado.PENDIENTE
    );
  }
}
