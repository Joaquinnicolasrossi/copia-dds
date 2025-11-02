import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoEstadistica implements WithSimplePersistenceUnit {

  private final EntityManager entityManager;

  public RepoEstadistica() {
    this.entityManager = null;
  }

  public RepoEstadistica(EntityManager em) {
    this.entityManager = em;
  }

  private EntityManager em() {
    return (this.entityManager != null) ? this.entityManager : entityManager();
  }

  public EstadisticaRegistro provinciaConMasHechos(Long coleccionId) {
    // CAMBIO EN LA QUERY: se usa HAVING para no tener arbitrariamente un LIMIT 1 cuyo resultado puede variar en runtime,
    // Ahora si hay un empate (la nueva consulta devuelve dos filas) y nos quedamos con el primero que devuelve la bdd
    String jpql = "SELECT h.provincia.nombre, COUNT(h) " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId " +
        "GROUP BY h.provincia.nombre " +
        "HAVING COUNT(h) >= ALL (" +
        "  SELECT COUNT(h2) " +
        "  FROM Hecho h2, Coleccion c2 " +
        "  WHERE h2.fuenteOrigen = c2.fuente AND c2.id = :coleccionId " +
        "  GROUP BY h2.provincia.nombre" +
        ")";

    TypedQuery<Object[]> query = em().createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);

    Coleccion coleccion = em().find(Coleccion.class, coleccionId);
    if (coleccion == null) {
      return null;
    }

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccion, "PROVINCIA_MAYOR_HECHOS", fila[0], fila[1]);
  }

  public EstadisticaRegistro categoriaConMayorHechos(Long coleccionId) {
    String jpql = "SELECT h.categoria, COUNT(h) " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId " +
        "GROUP BY h.categoria " +
        "HAVING COUNT(h) >= ALL (" +
        "  SELECT COUNT(h2) " +
        "  FROM Hecho h2, Coleccion c2 " +
        "  WHERE h2.fuenteOrigen = c2.fuente AND c2.id = :coleccionId " +
        "  GROUP BY h2.categoria" +
        ")";

    TypedQuery<Object[]> query = em().createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);

    Coleccion coleccion = em().find(Coleccion.class, coleccionId);
    if (coleccion == null) return null;

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccion, "CATEGORIA_MAYOR_HECHOS", fila[0], fila[1]);
  }

  public EstadisticaRegistro provinciaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    String jpql = "SELECT h.provincia.nombre, COUNT(h) " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId AND h.categoria = :categoria " +
        "GROUP BY h.provincia.nombre " +
        "HAVING COUNT(h) >= ALL (" +
        "  SELECT COUNT(h2) " +
        "  FROM Hecho h2, Coleccion c2 " +
        "  WHERE h2.fuenteOrigen = c2.fuente AND c2.id = :coleccionId AND h2.categoria = :categoria " +
        "  GROUP BY h2.provincia.nombre" +
        ")";

    TypedQuery<Object[]> query = em().createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setParameter("categoria", categoria);

    Coleccion coleccion = em().find(Coleccion.class, coleccionId);
    if (coleccion == null) return null;

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccion, "PROVINCIA_MAYOR_HECHOS_CATEGORIA", fila[0], fila[1]);
  }

  public EstadisticaRegistro horaConMasHechosPorCategoria(Long coleccionId, String categoria) {
    String jpql = "SELECT HOUR(h.fecha), COUNT(h) " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId AND h.categoria = :categoria " +
        "GROUP BY HOUR(h.fecha) " +
        "HAVING COUNT(h) >= ALL (" +
        "  SELECT COUNT(h2) " +
        "  FROM Hecho h2, Coleccion c2 " +
        "  WHERE h2.fuenteOrigen = c2.fuente AND c2.id = :coleccionId AND h2.categoria = :categoria " +
        "  GROUP BY HOUR(h2.fecha)" +
        ")";

    TypedQuery<Object[]> query = em().createQuery(jpql, Object[].class);
    query.setParameter("coleccionId", coleccionId);
    query.setParameter("categoria", categoria);

    Coleccion coleccion = em().find(Coleccion.class, coleccionId);
    if (coleccion == null) return null;

    List<Object[]> resultados = query.getResultList();
    if (resultados.isEmpty()) return null;

    Object[] fila = resultados.get(0);
    return crearRegistroEstadistica(
        coleccion, "HORA_MAS_HECHOS_CATEGORIA", fila[0], fila[1]);
  }

  public EstadisticaRegistro cantidadSolicitudesSpam(Long coleccionId) {
    String jpql = "SELECT COUNT(s) " +
        "FROM Solicitud s, Coleccion c " +
        "WHERE s.hecho.fuenteOrigen = c.fuente AND c.id = :coleccionId AND s.esSpam = true";

    TypedQuery<Long> query = em().createQuery(jpql, Long.class);
    query.setParameter("coleccionId", coleccionId);

    Long cantidad = 0L;
    try {
      cantidad = query.getSingleResult();
    } catch (NoResultException e) {
      cantidad = 0L;
    }

    Coleccion coleccion = em().find(Coleccion.class, coleccionId);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccion(coleccion);
    registro.setTipo("CANTIDAD_SOLICITUDES_SPAM");
    registro.setValor("SPAM");
    registro.setCantidad(cantidad != null ? cantidad.intValue() : 0);
    registro.setFecha_actualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);
    return registro;
  }

  public void guardarEstadistica(EstadisticaRegistro registro) {
    em().persist(registro);
  }

  public List<String> categoriasPorColeccion(Long coleccionId) {
    String jpql = "SELECT DISTINCT h.categoria " +
        "FROM Hecho h, Coleccion c " +
        "WHERE h.fuenteOrigen = c.fuente AND c.id = :coleccionId";

    TypedQuery<String> query = em().createQuery(jpql, String.class);
    query.setParameter("coleccionId", coleccionId);
    return query.getResultList();
  }

  public List<EstadisticaRegistro> buscarPorTipo(String tipo) {
    TypedQuery<EstadisticaRegistro> query = em().createQuery(
        "SELECT e FROM EstadisticaRegistro e WHERE e.tipo = :tipo",
        EstadisticaRegistro.class
    );
    query.setParameter("tipo", tipo);
    return query.getResultList();
  }

  private EstadisticaRegistro crearRegistroEstadistica(
      Coleccion coleccion, String tipo, Object valor, Object cantidadObj) {

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccion(coleccion);
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