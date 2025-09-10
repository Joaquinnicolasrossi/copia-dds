import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoEstadistica {

  private final EntityManager entityManager;

  public RepoEstadistica(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  // --------- QUERIES DE CÁLCULO ---------
  public Object[] categoriaConMayorHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.categoria, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON c.fuente_id = h.fuente_id " +
                "WHERE c.id = :coleccionId " +
                "GROUP BY h.categoria " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter("coleccionId", coleccionId)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public Object[] provinciaConMasHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.provincia, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON h.fuente_id = c.fuente_id " +
                "WHERE c.id = :coleccionId " +
                "GROUP BY h.provincia " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter("coleccionId", coleccionId)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

  // --------- INSERCIÓN ---------
  public void guardarEstadistica(Long coleccionId, String tipo, String valor, int cantidad) {
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery(
            "INSERT INTO estadistica " +
                "(coleccion_id, tipo, valor, cantidad, visible_publico, fecha_actualizacion) " +
                "VALUES (:coleccionId, :tipo, :valor, :cantidad, :visible, :fecha)")
        .setParameter("coleccionId", coleccionId)
        .setParameter("tipo", tipo)
        .setParameter("valor", valor)
        .setParameter("cantidad", cantidad)
        .setParameter("visible", true)
        .setParameter("fecha", LocalDateTime.now())
        .executeUpdate();
    entityManager.getTransaction().commit();
  }

  // --------- LECTURA DE ÚLTIMOS RESULTADOS ---------
  public String ultimaCategoriaConMasHechos(Long coleccionId) {
    List<String> resultados = entityManager.createNativeQuery(
            "SELECT e.valor " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = :coleccionId " +
                "AND e.tipo = 'CATEGORIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter("coleccionId", coleccionId)
        .getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public String ultimaProvinciaConMasHechos(Long coleccionId) {
    List<String> resultados = entityManager.createNativeQuery(
            "SELECT e.valor " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = :coleccionId " +
                "AND e.tipo = 'PROVINCIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter("coleccionId", coleccionId)
        .getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }
}
