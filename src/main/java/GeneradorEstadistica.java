import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica {
  private ClienteMetaMapa.Fuente fuente;
  @PersistenceContext
  EntityManager entityManager;

  public GeneradorEstadistica(EntityManager entityManager, ClienteMetaMapa.Fuente fuente) {
    this.entityManager = entityManager;
    this.fuente = fuente;
  }
}