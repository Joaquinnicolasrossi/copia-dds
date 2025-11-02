import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class RepoEstadistica {

  private final EntityManager entityManager;

  public RepoEstadistica(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  // --------- QUERIES CON JPQL (CORREGIDAS) ---------

  // 1) Provincia con mayor cantidad de hechos reportados
  public EstadisticaRegistro provinciaConMasHechos(Long coleccionId) {
    String jpql = "SELECT h.provincia, COUNT(h) AS cantidad " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId " +
        "GROUP BY h.provincia " +
        "ORDER BY cantidad DESC";

    TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setMaxResults(1);

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccionId, "PROVINCIA_MAYOR_HECHOS", fila[0], fila[1]);
  }

  // 2) Categoria con mayor cantidad de hechos reportados
  public EstadisticaRegistro categoriaConMayorHechos(Long coleccionId) {
    String jpql = "SELECT h.categoria, COUNT(h) AS cantidad " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId " +
        "GROUP BY h.categoria " +
        "ORDER BY cantidad DESC";

    TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setMaxResults(1);

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccionId, "CATEGORIA_MAYOR_HECHOS", fila[0], fila[1]);
  }

  // 3) Provincia con mayor cantidad de hechos reportados de una cierta categoria
  public EstadisticaRegistro provinciaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    String jpql = "SELECT h.provincia, COUNT(h) AS cantidad " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId AND h.categoria = :categoria " +
        "GROUP BY h.provincia " +
        "ORDER BY cantidad DESC";

    TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setParameter("categoria", categoria);
    query.setMaxResults(1);

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccionId, "PROVINCIA_MAYOR_HECHOS_CATEGORIA", fila[0], fila[1]);
  }

  // 4) Hora del dia con mayor cantidad de hechos por categoria
  public EstadisticaRegistro horaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    String jpql = "SELECT HOUR(h.fecha) as hora, COUNT(h) as cantidad " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId AND h.categoria = :categoria " +
        "GROUP BY HOUR(h.fecha)" +
        "ORDER BY cantidad DESC";

    TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setParameter("categoria", categoria);
    query.setMaxResults(1);

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccionId, "HORA_MAS_HECHOS_CATEGORIA", fila[0], fila[1]);
  }

  // 5) Cantidad de solicitudes de eliminacion son spam
  public EstadisticaRegistro cantidadSolicitudesSpam(Long coleccionId) {
    String jpql = "SELECT COUNT(s) " +
        "FROM Solicitud s, Coleccion c " +
        "WHERE s.hecho.fuenteOrigen = c.fuente AND c.id = :coleccionId AND s.esSpam = true";

    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
    query.setParameter("coleccionId", coleccionId);

    Long cantidad = 0L;
    try {
      cantidad = query.getSingleResult();
    } catch (NoResultException e) {
      cantidad = 0L;
    }

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo("CANTIDAD_SOLICITUDES_SPAM");
    registro.setValor("SPAM");
    registro.setCantidad(cantidad != null ? cantidad.intValue() : 0);
    registro.setFecha_actualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);
    return registro;
  }

  // --------- INSERCIÓN ---------
  public void guardarEstadistica(EstadisticaRegistro registro) {
    entityManager.getTransaction().begin();
    entityManager.persist(registro);
    entityManager.getTransaction().commit();
  }

  // --------- LECTURA DE LA TABLA ESTADISTICA ---------
  public List<String> categoriasPorColeccion(Long coleccionId) {
    // se usa un join implícito
    String jpql = "SELECT DISTINCT h.categoria " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId";

    TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
    query.setParameter("coleccionId", coleccionId);
    return query.getResultList();
  }

  public List<EstadisticaRegistro> buscarPorTipo(String tipo) {
    TypedQuery<EstadisticaRegistro> query = entityManager.createQuery(
        "SELECT e FROM EstadisticaRegistro e WHERE e.tipo = :tipo",
        EstadisticaRegistro.class
    );
    query.setParameter("tipo", tipo);
    return query.getResultList();
  }

  private EstadisticaRegistro crearRegistroEstadistica(Long coleccionId, String tipo, Object valor, Object cantidadObj) {
    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(coleccionId);
    registro.setTipo(tipo);

    if (valor instanceof Number) {
      registro.setValor(String.valueOf(((Number) valor).intValue()));
    } else {
      registro.setValor((String) valor);
    }

    registro.setCantidad(((Number) cantidadObj).intValue());
    registro.setFecha_actualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);
    return registro;
  }
}