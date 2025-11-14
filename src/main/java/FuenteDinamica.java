import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("DINAMICA")
public class FuenteDinamica extends Fuente {
  @Transient
  RepoHechos repoHechos;
  @Transient
  RepoSolicitudes repoSolicitudes;

  public FuenteDinamica(RepoHechos repoHechos,
                        RepoSolicitudes repoSolicitudes) {
    this.repoHechos = repoHechos;
    this.repoSolicitudes = repoSolicitudes;
  }

  protected FuenteDinamica() {
  }

  @Override
  public List<Hecho> extraerHechos() {
    return repoHechos.getHechos();
  }

  public void crearSolicitud(Hecho hecho) {
    SolicitudRevision solicitudRevision = new SolicitudRevision(hecho, hecho.getUsuario());
    repoSolicitudes.nuevaSolicitudRevision(solicitudRevision);
  }

  public void actualizarHecho(Hecho hecho, Hecho.HechoBuilder hechoBuilder, Usuario usuario) {
    repoHechos.saveUpdate(hecho, hechoBuilder);
  }

  public void revisarSolicitud(Hecho hecho, EstadoRevision estadoRevision) throws Exception {
    var solicitudRevision = repoSolicitudes.getSolicitudPorHecho(hecho);
    estadoRevision.aplicar(hecho);
    repoSolicitudes.eliminarSolicitudRevision(solicitudRevision);
  }

  public String getIdentificador() {
    return "dinamica";
  }

  public void setRepoHechos(RepoHechos repoHechos) {
    this.repoHechos = repoHechos;
  }

  public void setRepoSolicitudes(RepoSolicitudes repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

}