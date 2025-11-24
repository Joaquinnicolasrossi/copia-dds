import java.util.List;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoHechos implements WithSimplePersistenceUnit {
  private final RepoProvincias repoProvincias;

  public RepoHechos(RepoProvincias repoProvincias) {
    this.repoProvincias = repoProvincias;
  }

  public void guardarHecho(Hecho hecho) {
    entityManager().getTransaction().begin();
    entityManager().persist(hecho);
    entityManager().getTransaction().commit();
  }

  public void guardarHechos(List<Hecho> hechos) {
    hechos.forEach(hecho -> entityManager().persist(hecho));
  }

  public List<Hecho> obtenerTodosLosHechos() {
    return entityManager()
        .createQuery("from Hecho", Hecho.class)
        .getResultList();
  }

  public List<Fuente> obtenerTodasLasFuentes() {
    return entityManager()
        .createQuery("select distinct f from Fuente f join Hecho h", Fuente.class)
        .getResultList();
  }

  public List<Hecho> obtenerHechosDeFuentes(List<Fuente> fuentes) {
    return fuentes.stream()
        .flatMap(f -> obtenerHechosPorFuente(f).stream())
        .toList();
  }

  public List<Hecho> obtenerHechosPorFuente(Fuente fuente) {
    return entityManager()
        .createQuery("select h from Hecho h where h.fuenteOrigen = :fuente", Hecho.class)
        .setParameter("fuente", fuente)
        .getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Hecho> buscarFullText(String texto) {
    return entityManager().createNativeQuery(
            "SELECT * FROM hecho WHERE MATCH(titulo, descripcion) AGAINST (? IN NATURAL LANGUAGE MODE)",
            Hecho.class).setParameter(1, texto)
        .getResultList();
  }

  public Hecho obtenerPorId(Long id) {
    return entityManager().find(Hecho.class, id);
  }
  public List<Hecho> obtenerHechosPorUsuario(Long userId) {
    return entityManager()
        .createQuery(
            " SELECT h FROM Hecho h WHERE h.usuario.id = :userId AND h.estado IN (:estados)",
            Hecho.class)
        .setParameter("userId", userId)
        .setParameter("estados", List.of(Estado.PENDIENTE, Estado.ACEPTADA_CON_SUGERENCIAS))
        .getResultList();
  }


  public List<Hecho> obtenerPorCategoria(String categoria) {
    return

        createQuery( "SELECT h FROM Hecho h WHERE LOWER(h.categoria) = LOWER(:categoria)", Hecho.class).
            setParameter("categoria",categoria).getResultList();

  }

  public Hecho save(Hecho hecho) {
    entityManager().getTransaction().begin();
    entityManager().persist(hecho);
    entityManager().getTransaction().commit();
    return hecho;
  }

  public List<Hecho> getHechos() {
    return entityManager().createQuery("SELECT h from Hecho h",
        Hecho.class).getResultList();
  }

  public Hecho findById(long hechoid) {
    return entityManager().createQuery(
            "SELECT h FROM Hecho h WHERE h.id = :hechoid", Hecho.class)
        .setParameter("hechoid", hechoid).getSingleResult();
  }

  public void saveUpdate(Hecho hechoOriginal, Hecho.HechoBuilder hechoBuilder)
  {
    entityManager().getTransaction().begin();
    hechoOriginal.actualizarHecho(hechoOriginal, hechoBuilder, repoProvincias);
    entityManager().merge(hechoOriginal);
    System.out.println("Fecha nueva: " + hechoBuilder.getFechaCarga());
    entityManager().getTransaction().commit();

  }

    public FuenteDinamica obtenerFuenteDinamica() {
        List<FuenteDinamica> fuentes = entityManager()
            .createQuery("from FuenteDinamica", FuenteDinamica.class)
            .getResultList();

        if (!fuentes.isEmpty()) {
            return fuentes.get(0);
        }
        // si no existe la creo
        FuenteDinamica nueva = new FuenteDinamica();
        withTransaction(() -> entityManager().persist(nueva));
        return nueva;
    }

}