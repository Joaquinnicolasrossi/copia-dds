import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("DINAMICA")
public class FuenteDinamica extends Fuente {
  @Transient
  RepoFuenteDinamica repoFuenteDinamica;
  @Transient
  RepoSolicitudesRevision repoSolicitudesRevision;
  public FuenteDinamica(RepoFuenteDinamica repoFuenteDinamica,
      RepoSolicitudesRevision repoSolicitudesRevision) {
    this.repoFuenteDinamica = repoFuenteDinamica;
    this.repoSolicitudesRevision = repoSolicitudesRevision;
  }

  protected FuenteDinamica(){}

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

  }

  public void revisarSolicitud(Hecho hecho, EstadoRevision estadoRevision) throws Exception {
    var solicitudRevision = repoSolicitudesRevision.getSolicitudPorHecho(hecho);
    estadoRevision.aplicar(hecho);
    repoSolicitudesRevision.eliminarSolcitud(solicitudRevision);
  }
  public String getIdentificador(){
    return "dinamica";
  }
}
