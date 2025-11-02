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
    entityManager().getTransaction().begin();
    entityManager().persist(nueva);
    entityManager().getTransaction().commit();
  }

  public void eliminarSolicitud(Solicitud solicitud) {
    entityManager().getTransaction().begin();
    entityManager().remove(solicitud);
    entityManager().getTransaction().commit();
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

  public Solicitud obtenerPorId(Long id) {
    return entityManager()
        .createQuery("SELECT s FROM Solicitud s WHERE s.id = :id", Solicitud.class)
        .setParameter("id", id)
        .getSingleResult();
  }

  public void aceptarSolicitud(Solicitud solicitud) {
    entityManager().getTransaction().begin();
    solicitud.aceptarSolicitud();
    entityManager().merge(solicitud);
    entityManager().getTransaction().commit();
  }
}
