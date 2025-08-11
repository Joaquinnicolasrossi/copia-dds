public class Rechazar implements EstadoRevision {
 private RepoFuenteDinamica repoFuenteDinamica;

  public Rechazar(RepoFuenteDinamica repoFuenteDinamica) {
    this.repoFuenteDinamica = repoFuenteDinamica;
  }

  @Override
  public void aplicar(Hecho hecho) {
    hecho.setEstado(Estado.RECHAZADA);
  }
}