import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica {
  private Fuente fuente;
  @PersistenceContext
  EntityManager entityManager;

  public GeneradorEstadistica(EntityManager entityManager, Fuente fuente) {
    this.entityManager = entityManager;
    this.fuente = fuente;
  }

}
