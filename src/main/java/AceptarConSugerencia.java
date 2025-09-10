public class AceptarConSugerencia implements EstadoRevision {
  private Usuario usuario;
  private RepoFuenteDinamica repoFuenteDinamica;
  private FuenteDinamica fuenteDinamica;
  private Hecho.HechoBuilder hechoBuilder;

  public AceptarConSugerencia(FuenteDinamica fuenteDinamica, Hecho.HechoBuilder hechoBuilder,
      RepoFuenteDinamica repoFuenteDinamica, Usuario usuario) {
    this.fuenteDinamica = fuenteDinamica;
    this.hechoBuilder = hechoBuilder;
    this.repoFuenteDinamica = repoFuenteDinamica;
    this.usuario = usuario;
  }

  @Override
  public void aplicar(Hecho hecho) throws Exception {
    hecho.setEstado(Estado.ACEPTADA_CON_SUGERENCIAS);
    fuenteDinamica.actualizarHecho(hecho, hechoBuilder, usuario);
    repoFuenteDinamica.save(hecho);
  }
}
