import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RepoHechosTest {
  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoHechos repo = new RepoHechos();
  private List<Hecho> hechos = new ArrayList<>();
  private Fuente fuenteMock = new FuenteDinamica(new RepoFuenteDinamica(), new RepoSolicitudesRevision());
  @BeforeEach
  public void inicializar() {
    emf = Persistence.createEntityManagerFactory("testPU");
    em = emf.createEntityManager();
    repo.setEntityManager(em);

    em.getTransaction().begin();
    em.persist(fuenteMock);
    em.getTransaction().commit();
    em.close();

    Hecho h1 = crearHechoSimple("tA", "dA", "cA");
    h1.setFuenteOrigen(fuenteMock);
    Hecho h2 = crearHechoSimple("tB", "dB", "cB");
    hechos.add(h1);
    hechos.add(h2);
  }
  @Disabled
  @Test
  void seGuardanHechosYSePuedenConsultar() {
    repo.guardarHechos(hechos);

    List<Hecho> consulta = repo.obtenerTodosLosHechos();
    assertEquals(hechos.size(), consulta.size());
  }
  private Hecho crearHechoSimple(String titulo, String descripcion, String categoria) {
     return new Hecho(titulo, descripcion, categoria, -34.0000, -34.0000, LocalDate.now(), LocalDate.now(), Estado.PENDIENTE);

  }
}
