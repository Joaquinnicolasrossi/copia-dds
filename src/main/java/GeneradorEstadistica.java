import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica implements IEstadistica{
 private Fuente fuente;
  @PersistenceContext
  EntityManager entityManager;

  public GeneradorEstadistica(EntityManager entityManager, Fuente fuente) {
    this.entityManager = entityManager;
    this.fuente = fuente;
  }

  @Override
  public String categoriaConMayorHechosReportados() {
    List<Object[]> resultados = entityManager.createQuery(
            "SELECT h.categoria, COUNT(h) " +
                "FROM Hecho h " +
                "WHERE h.fuenteOrigen = :fuente " +
                "GROUP BY h.categoria " +
                "ORDER BY COUNT(h) DESC",
            Object[].class
        )
        .setParameter("fuente", this.fuente)
        .setMaxResults(1)
        .getResultList();

    return (String) resultados.get(0)[0];
  }


}

