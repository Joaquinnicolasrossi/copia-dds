import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fuente")
public class FuenteDinamica extends ClienteMetaMapa.Fuente {
  RepoFuenteDinamica repoFuenteDinamica;
  RepoSolicitudesRevision repoSolicitudesRevision;
  @Id
  @GeneratedValue
  public Long id;

  //TODO Mejorar excepciones podrian ser algo como NotFountException , validationErrorException
  public FuenteDinamica(RepoFuenteDinamica repoFuenteDinamica,
                        RepoSolicitudesRevision repoSolicitudesRevision) {
    this.repoFuenteDinamica = repoFuenteDinamica;
    this.repoSolicitudesRevision = repoSolicitudesRevision;
  }

  public Long getId() { return id; }

  @Override
  public List<Hecho> extraerHechos() {
    return repoFuenteDinamica.getHechos();
  }

  public void subirHecho(Hecho hecho) {
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

  public void revisarSolicitud(Hecho hecho, EstadoRevision estadoRevision) throws Exception {
    var solicitudRevision = repoSolicitudesRevision.getSolicitudPorHecho(hecho);
    estadoRevision.aplicar(hecho);
    repoSolicitudesRevision.eliminarSolcitud(solicitudRevision);

  }


}
