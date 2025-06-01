public class FuenteDinamica {
  RepoFuenteDinamica repoFuenteDinamica;
  RepoSolicitudesRevision repoSolicitudesRevision;

  //TODO Mejorar excepciones podrian ser algo como NotFountException , validationErrorException
  public FuenteDinamica(RepoFuenteDinamica repoFuenteDinamica, RepoSolicitudesRevision repoSolicitudesRevision) {
    this.repoFuenteDinamica = repoFuenteDinamica;
    this.repoSolicitudesRevision = repoSolicitudesRevision;
  }

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

  public void revisarHecho(String titulo, Revision resivion) {

    Hecho hecho = getHecho(titulo);
    repoFuenteDinamica.save(hecho);
    repoSolicitudesRevision.save(resivion);
  }


  private Hecho getHecho(String titulo) {
    Hecho hecho = repoFuenteDinamica.findByTitulo(titulo);
    return hecho;
  }
}
