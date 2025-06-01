public class FuenteDinamica {
  RepoFuenteDinamica repoFuenteDinamica;

  public void subirHecho(Hecho hecho) {
    repoFuenteDinamica.save(hecho);
  }

  public void actualizarHecho(String titulo, Hecho.HechoBuilder hechoBuilder) throws Exception {
    var hecho = getHecho(titulo);

    if (!hecho.estaDentroDePlazoDeEdicion()) {
      throw new Exception("El plazo para modificar este hecho ha expirado.");
    }

    Hecho hechoActualizado = repoFuenteDinamica.saveUpdate(hecho, hechoBuilder);
    if (hechoActualizado == null) {
      throw new Exception("No pudo actualizar el hecho");
    }
  }

  private Hecho getHecho(String titulo) {
    Hecho hecho = repoFuenteDinamica.findByTitulo(titulo);
    return hecho;
  }
}
