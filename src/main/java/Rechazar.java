public class Rechazar implements EstadoRevision {
  private RepoHechos repoHechos;

  public Rechazar(RepoHechos repoHechos) {
    this.repoHechos = repoHechos;
  }

  @Override
  public void aplicar(Hecho hecho) {
    hecho.setEstado(Estado.RECHAZADA);
  }
}