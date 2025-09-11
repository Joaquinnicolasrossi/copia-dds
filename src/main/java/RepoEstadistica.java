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
  public EstadisticaRegistro categoriaConMayorHechos(Long coleccionId) {
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

    if (resultados.isEmpty()) {
      return null;
    }

    Object[] fila = resultados.get(0);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("CATEGORIA_MAYOR_HECHOS");
    registro.setValor((String) fila[0]);
    registro.setCantidad(((Number) fila[1]).intValue());
    registro.setFechaActualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);

    return registro;
  }

  public EstadisticaRegistro provinciaConMasHechos(Long coleccionId) {
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

    if (resultados.isEmpty()) {
      return null;
    }

    Object[] fila = resultados.get(0);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("PROVINCIA_MAYOR_HECHOS");
    registro.setValor((String) fila[0]);                  // provincia
    registro.setCantidad(((Number) fila[1]).intValue());  // cantidad
    registro.setFechaActualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);

    return registro;
  }

  public EstadisticaRegistro provinciaConMasHechosPorCategoria(Long coleccionId, String categoria) {
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

    if (resultados.isEmpty()) {
      return null;
    }

    Object[] fila = resultados.get(0);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("PROVINCIA_MAYOR_HECHOS_CATEGORIA");
    registro.setValor((String) fila[0]);                  // provincia
    registro.setCantidad(((Number) fila[1]).intValue());  // cantidad
    registro.setFechaActualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);

    return registro;
  }
  public EstadisticaRegistro horaConMasHechosPorCategoria(Long coleccionId, String categoria) {
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

    if (resultados.isEmpty()) {
      return null;
    }

    Object[] fila = resultados.get(0);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("HORA_MAS_HECHOS_CATEGORIA");
    registro.setValor(String.valueOf(((Number) fila[0]).intValue())); // hora en string
    registro.setCantidad(((Number) fila[1]).intValue());              // cantidad
    registro.setFechaActualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);

    return registro;
  }

  public EstadisticaRegistro cantidadSolicitudesSpam(Long coleccionId) {
    List<Object> resultados = entityManager.createNativeQuery(
            "SELECT COUNT(*) " +
                "FROM solicitud s " +
                "JOIN hecho h ON s.hecho_id = h.id " +
                "JOIN coleccion c ON h.fuente_id = c.fuente_id " +
                "WHERE c.id = ?1 " +
                "AND s.es_spam = true")
        .setParameter(1, coleccionId)
        .getResultList();

    Long cantidad = resultados.isEmpty() ? 0L : ((Number) resultados.get(0)).longValue();

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("CANTIDAD_SOLICITUDES_SPAM");
    registro.setValor("SPAM");
    registro.setCantidad(cantidad.intValue());
    registro.setFechaActualizacion(LocalDateTime.now());

    return registro;
  }

  // --------- INSERCIÓN ---------
  public void guardarEstadistica(EstadisticaRegistro registro) {
    entityManager.getTransaction().begin();
    entityManager.persist(registro);
    entityManager.getTransaction().commit();
  }

  // --------- LECTURA DE LA TABLA ESTADISTICA ---------
  public EstadisticaRegistro ultimaCategoriaConMasHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT e.id, e.coleccion_id, e.tipo, e.valor, e.cantidad, e.visible_publico, e.fecha_actualizacion " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = ?1 " +
                "AND e.tipo = 'CATEGORIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();

    return mapearARegistro(resultados);
  }

  public EstadisticaRegistro ultimaProvinciaConMasHechos(Long coleccionId) {
    List<Object[]> resultados = entityManager.createNativeQuery(
            "SELECT e.id, e.coleccion_id, e.tipo, e.valor, e.cantidad, e.visible_publico, e.fecha_actualizacion " +
                "FROM estadistica e " +
                "WHERE e.coleccion_id = ?1 " +
                "AND e.tipo = 'PROVINCIA_MAYOR_HECHOS' " +
                "ORDER BY e.fecha_actualizacion DESC " +
                "LIMIT 1")
        .setParameter(1, coleccionId)
        .getResultList();

    return mapearARegistro(resultados);
  }

  public List<EstadisticaRegistro> categoriasPorColeccion(Long coleccionId) {
    List<String> categorias = entityManager.createNativeQuery(
            "SELECT DISTINCT h.categoria " +
                "FROM hecho h " +
                "JOIN coleccion c ON c.fuente_id = h.fuente_id " +
                "WHERE c.id = ?1")
        .setParameter(1, coleccionId)
        .getResultList();

    return categorias.stream().map(cat -> {
      EstadisticaRegistro r = new EstadisticaRegistro();
      r.setColeccionId(coleccionId);
      r.setTipo("CATEGORIA_EN_COLECCION");
      r.setValor(cat);
      r.setCantidad(1);
      r.setFechaActualizacion(LocalDateTime.now());
      r.setVisiblePublico(true);
      return r;
    }).toList();
  }

  private EstadisticaRegistro mapearARegistro(List<Object[]> resultados) {
    if (resultados.isEmpty()) {
      return null;
    }
    Object[] fila = resultados.get(0);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setId(((Number) fila[0]).longValue());
    registro.setColeccionId(((Number) fila[1]).longValue());
    registro.setTipo((String) fila[2]);
    registro.setValor((String) fila[3]);
    registro.setCantidad(fila[4] != null ? ((Number) fila[4]).intValue() : null);
    registro.setVisiblePublico(fila[5] != null ? (Boolean) fila[5] : null);
    registro.setFechaActualizacion(fila[6] != null ? ((java.sql.Timestamp) fila[6]).toLocalDateTime() : null);

    return registro;
  }

}
