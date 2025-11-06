public class Aceptar implements EstadoRevision {
  private RepoHechos repoHechos;

  public Aceptar(RepoHechos repoHechos ) {
    this.repoHechos = repoHechos ;
  }

  @Override
  public void aplicar(Hecho hecho) {
    hecho.setEstado(Estado.ACEPTADA);
    repoHechos.guardarHecho(hecho);
  }
}

