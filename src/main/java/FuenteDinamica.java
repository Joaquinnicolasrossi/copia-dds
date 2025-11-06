import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
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

  protected FuenteDinamica(){}

  @Override
  public List<Hecho> extraerHechos() {
    return repoHechos.getHechos();
  }

  public void subirHecho(Hecho hecho) {
    repoSolicitudes.

        nuevaSolicitudRevision(hecho);
  }

  public void actualizarHecho(Hecho hecho, Hecho.HechoBuilder hechoBuilder, Usuario usuario)
      throws Exception {

    if (!hecho.perteneceA(usuario)) {
      throw new Exception("El usuario no esta registrado");
    }

    if (!hecho.estaDentroDePlazoDeEdicion()) {
      throw new Exception("El plazo para modificar este hecho ha expirado.");
    }
    repoHechos.saveUpdate(hecho,hechoBuilder);
  }

  public void revisarSolicitud(Hecho hecho, EstadoRevision estadoRevision) throws Exception {
    var solicitudRevision = repoSolicitudes.getSolicitudPorHecho(hecho);
    estadoRevision.aplicar(hecho);
    repoSolicitudes.eliminarSolicitudRevision(solicitudRevision);
  }
  public String getIdentificador(){
    return "dinamica";
  }
}
