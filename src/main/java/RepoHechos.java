import java.util.List;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoHechos implements WithSimplePersistenceUnit {

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
        .createQuery("select h from Hecho h join Fuente f where h.fuente_origen = :fuente", Hecho.class)
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
}
