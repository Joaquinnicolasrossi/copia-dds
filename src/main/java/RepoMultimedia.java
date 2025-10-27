import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoMultimedia implements WithSimplePersistenceUnit {
  public void crearMultimedia(Multimedia multimedia) {
    entityManager().persist(multimedia);
  }
}
