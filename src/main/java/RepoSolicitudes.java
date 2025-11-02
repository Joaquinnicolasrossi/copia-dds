import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudes implements WithSimplePersistenceUnit {
  private DetectorDeSpam detectorDeSpam;

  public RepoSolicitudes(DetectorDeSpam detectorDeSpam) {
    this.detectorDeSpam = detectorDeSpam;
  }

  public void nuevaSolicitud(Hecho hecho, String descripcion) throws Exception {
    boolean spam = detectorDeSpam.esSpam(descripcion);
    Solicitud nueva = new Solicitud(hecho, descripcion, this, spam);
    entityManager().persist(nueva);
  }

  public void eliminarSolicitud(Solicitud solicitud) {
    entityManager().remove(solicitud);
  }

  public List<Solicitud> getSolicitudes() {
    return entityManager()
        .createQuery("from Solicitud", Solicitud.class)
        .getResultList();
  }

  public List<Solicitud> getSolicitudesPendientes() {
    return entityManager()
        .createQuery("from Solicitud s where s.eliminado = false ", Solicitud.class)
        .getResultList();
  }

  public Boolean estaEliminado(Hecho hecho) {
     Solicitud solicitud = entityManager()
         .createQuery("from Solicitud s where s.eliminado = true and s.hecho = :hecho",
             Solicitud.class)
         .setParameter("hecho", hecho)
         .setMaxResults(1)
         .getSingleResult();

     return solicitud != null;
  }

  public Long cantidadSolicitudesSpam() {
    return entityManager()
        .createQuery("select count(s) from Solicitud s where s.esSpam = true", Long.class)
        .getSingleResult();
  }
}
