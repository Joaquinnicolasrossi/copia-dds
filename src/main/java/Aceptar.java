public class Aceptar implements EstadoRevision {
  private RepoFuenteDinamica repoFuenteDinamica;

  public Aceptar(RepoFuenteDinamica repoFuenteDinamica) {
    this.repoFuenteDinamica = repoFuenteDinamica;
  }

  @Override
  public void aplicar(Hecho hecho) {
    hecho.setEstado(Estado.ACEPTADA);
    repoFuenteDinamica.save(hecho);
  }
}

