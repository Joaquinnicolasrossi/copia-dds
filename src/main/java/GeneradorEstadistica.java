import java.time.LocalDateTime;
import java.util.List;
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

  public void categoriaConMayorHechosReportados(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.categoria, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON c.fuente_id = h.fuente_id " +
                "WHERE c.id = :coleccionId " +
                "GROUP BY h.categoria " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1"
        )
        .setParameter("coleccionId", coleccionId)
        .getResultList();

    if (resultados.isEmpty()) return;

    Object[] fila = resultados.get(0);
    String categoria = (String) fila[0];
    Long cantidad = ((Number) fila[1]).longValue();

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("CATEGORIA_MAYOR_HECHOS");
    registro.setValor(categoria);
    registro.setCantidad(cantidad.intValue());
    registro.setVisiblePublico(true);
    registro.setFechaActualizacion(LocalDateTime.now());

    entityManager.getTransaction().begin();
    entityManager.persist(registro);
    entityManager.getTransaction().commit();
  }

  public String provinciaConMasHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.provincia, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON h.fuente_id = c.fuente_id " +
                "WHERE c.id = :coleccionId " +
                "GROUP BY h.provincia " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1"
        )
        .setParameter("coleccionId", coleccionId)
        .getResultList();

    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    String provincia = (String) fila[0];
    Number cantidad = (Number) fila[1];


    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("PROVINCIA_MAYOR_HECHOS");
    registro.setValor(provincia);
    registro.setCantidad(cantidad.intValue());
    registro.setVisiblePublico(true);
    registro.setFechaActualizacion(LocalDateTime.now());


    entityManager.getTransaction().begin();
    entityManager.persist(registro);
    entityManager.getTransaction().commit();

    return provincia;
  }
}

