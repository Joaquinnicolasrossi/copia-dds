import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


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
                "WHERE c.id = ?1 " +
                "GROUP BY h.categoria " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public Object[] provinciaConMasHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.provincia, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON h.fuente_id = c.fuente_id " +
                "WHERE c.id = ?1 " +
                "GROUP BY h.provincia " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public Object[] provinciaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT h.provincia, COUNT(*) AS cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON h.fuente_id = c.fuente_id " +
                "WHERE c.id = ?1 " +
                "AND h.categoria = ?2 " +
                "GROUP BY h.provincia " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .setParameter(2, categoria)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public Object[] horaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT EXTRACT(HOUR FROM h.fecha) as hora, COUNT(*) as cantidad " +
                "FROM hecho h " +
                "JOIN coleccion c ON c.fuente_id = h.fuente_id " +
                "WHERE c.id = ?1 " +
                "AND h.categoria = ?2 " +
                "GROUP BY hora " +
                "ORDER BY cantidad DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .setParameter(2, categoria)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }


  // --------- INSERCIÓN ---------
  public void guardarEstadistica(Long coleccionId, String tipo, String valor, int cantidad) {
    entityManager.getTransaction().begin();
    Query query = entityManager.createNativeQuery(
        "INSERT INTO estadistica " +
            "(coleccion_id, tipo, valor, cantidad, fecha_actualizacion) " +
            "VALUES (?1, ?2, ?3, ?4, ?5)"
    );

    query.setParameter(1, coleccionId);
    query.setParameter(2, tipo);
    query.setParameter(3, valor);
    query.setParameter(4, cantidad);
    query.setParameter(5, Timestamp.valueOf(LocalDateTime.now()));

    query.executeUpdate();
    entityManager.getTransaction().commit();
  }

  // --------- LECTURA DE ÚLTIMOS RESULTADOS ---------
  public String ultimaCategoriaConMasHechos(Long coleccionId) {
    List<String> resultados = entityManager.createNativeQuery(
            "SELECT e.valor " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = ?1 " +
                "AND e.tipo = 'CATEGORIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public String ultimaProvinciaConMasHechos(Long coleccionId) {
    List<String> resultados = entityManager.createNativeQuery(
            "SELECT e.valor " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = ?1 " +
                "AND e.tipo = 'PROVINCIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public Long cantidadSolicitudesSpam(Long coleccionId) {
    List<Object> resultados = entityManager.createNativeQuery(
      "SELECT COUNT(*) " +
          "FROM solicitud s " +
          "JOIN hecho h on s.hecho_id = h.id " +
          "JOIN coleccion c on h.fuente_id = c.fuente_id " +
          "WHERE c.id = ?1 " +
          "AND s.es_spam = true")
        .setParameter(1, coleccionId)
        .getResultList();
    return resultados.isEmpty() ? 0L : ((Number) resultados.get(0)).longValue();
  }

  public List<String> categoriasPorColeccion(Long coleccionId) {
    return entityManager.createNativeQuery(
            "SELECT DISTINCT h.categoria " +
                "FROM hecho h " +
                "JOIN coleccion c ON c.fuente_id = h.fuente_id " +
                "WHERE c.id = ?1")
        .setParameter(1, coleccionId)
        .getResultList();
  }
}
