public class AceptarConSugerencia implements EstadoRevision {
  private Usuario usuario;
  private RepoHechos repoHechos;
  private FuenteDinamica fuenteDinamica;
  private Hecho.HechoBuilder hechoBuilder;

  public AceptarConSugerencia(FuenteDinamica fuenteDinamica, Hecho.HechoBuilder
      hechoBuilder, RepoHechos repoHechos, Usuario usuario) {
    this.fuenteDinamica = fuenteDinamica;
    this.hechoBuilder = hechoBuilder;
    this.repoHechos = repoHechos;
    this.usuario = usuario;
  }

  @Override
  public void aplicar(Hecho hecho) throws Exception {
    hecho.setEstado(Estado.ACEPTADA_CON_SUGERENCIAS);
    fuenteDinamica.actualizarHecho(hecho, hechoBuilder, usuario);
    repoHechos.guardarHecho(hecho);
  }
}
