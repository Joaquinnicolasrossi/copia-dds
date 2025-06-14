import java.time.LocalDate;
import java.util.List;

public class FuenteDinamica implements Fuente {
  RepoFuenteDinamica repoFuenteDinamica;
  RepoSolicitudesRevision repoSolicitudesRevision;

  //TODO Mejorar excepciones podrian ser algo como NotFountException , validationErrorException
  public FuenteDinamica(RepoFuenteDinamica repoFuenteDinamica,
                        RepoSolicitudesRevision repoSolicitudesRevision) {
    this.repoFuenteDinamica = repoFuenteDinamica;
    this.repoSolicitudesRevision = repoSolicitudesRevision;
  }

  @Override
  public List<Hecho> extraerHechos() {
    return List.of();
  }

  public void subirHecho(Hecho hecho) {
    repoFuenteDinamica.save(hecho);
    repoSolicitudesRevision.nuevaSolicitud(hecho);
  }


  public void actualizarHecho(Hecho hecho, Hecho.HechoBuilder hechoBuilder, Usuario usuario)
      throws Exception {

    if (!hecho.perteneceA(usuario)) {
      throw new Exception("El usuario no esta registrado");
    }

    if (!hecho.estaDentroDePlazoDeEdicion()) {
      throw new Exception("El plazo para modificar este hecho ha expirado.");
    }
    repoFuenteDinamica.saveUpdate(hecho, hechoBuilder);
  }

  public void revisarSolicitud(Hecho hecho, EstadoRevision estadoRevision,
                               String sugerencia) {
    var solicitudRevision = repoSolicitudesRevision.getSolicitudPorHecho(hecho);
    estadoRevision.aplicar(hecho, sugerencia);
    repoSolicitudesRevision.eliminarSolcitud(solicitudRevision);

  }


}
