import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepoSolicitudes implements WithSimplePersistenceUnit {
  private DetectorDeSpam detectorDeSpam;

  public RepoSolicitudes(DetectorDeSpam detectorDeSpam) {
    this.detectorDeSpam = detectorDeSpam;
  }

  public void nuevaSolicitud(Hecho hecho, String descripcion) throws Exception {
    withTransaction(() -> {
      String textoHecho = hecho.getTitulo() + " " + hecho.getDescripcion();
      boolean spam = detectorDeSpam.esSpam(textoHecho);
      Solicitud nueva = new Solicitud(hecho, descripcion, this, spam);
      nueva.setEsSpam(spam);
      if (spam) {
        nueva.marcarComoSpam();
      }
      entityManager().persist(nueva);
    });
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
        .createQuery("from Solicitud s where s.eliminado = false AND s.descripcion NOT LIKE 'SPAM:%'", Solicitud.class)
        .getResultList();
  }

//  public Boolean estaEliminado(Hecho hecho) {Solicitud solicitud = entityManager().createQuery("from Solicitud s where s.eliminado = true and s.hecho = :hecho", Solicitud.class).setParameter("hecho", hecho).setMaxResults(1).getSingleResult();return solicitud != null;}
public Boolean estaEliminado(Hecho hecho) {
  // Valida si está eliminado
    if (hecho.getId() == null) {
    return false;
  }
  List<Solicitud> solicitud = entityManager()
      .createQuery("from Solicitud s where s.eliminado = true and s.hecho = :hecho",
          Solicitud.class)
      .setParameter("hecho", hecho)
      .setMaxResults(1)
      .getResultList();

  return !solicitud.isEmpty();
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

  public void nuevaSolicitudRevision(SolicitudRevision solicitudRevision) {
    entityManager().getTransaction().begin();
    entityManager().persist(solicitudRevision);
    entityManager().getTransaction().commit();
  }

  public void eliminarSolicitudRevision(SolicitudRevision solicitudRevision) {
    entityManager().getTransaction().begin();
    entityManager().remove(solicitudRevision);
    entityManager().getTransaction().commit();
  }

  public SolicitudRevision getSolicitudPorHecho(Hecho hecho) {
    return entityManager()
        .createQuery("from SolicitudRevision s where s.hecho = :hecho", SolicitudRevision.class)
        .setParameter("hecho", hecho)
        .setMaxResults(1)
        .getSingleResult();
  }

  public List<SolicitudRevision> getRevisiones() {
    return createQuery(
        "FROM SolicitudRevision s WHERE s.hecho.estado = 'PENDIENTE'", SolicitudRevision.class)
        .getResultList();
  }

  public SolicitudRevision findById(Long id) {
    return entityManager().createQuery("FROM SolicitudRevision s WHERE s.id = :id", SolicitudRevision.class)
        .setParameter("id", id)
        .getSingleResult();
  }
}