import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudesRevision implements WithSimplePersistenceUnit {

  public void nuevaSolicitud(Hecho hecho) {
    entityManager().persist(hecho);
  }

  public void eliminarSolcitud(SolicitudRevision solicitudRevision) {
    entityManager().remove(solicitudRevision);
  }

  public SolicitudRevision getSolicitudPorHecho(Hecho hecho) {
    return entityManager()
        .createQuery("from SolicitudRevision s where s.hecho = :hecho", SolicitudRevision.class)
        .setParameter("hecho", hecho)
        .setMaxResults(1)
        .getSingleResult();
  }

  public List<SolicitudRevision> getRevisiones() {
    return entityManager()
        .createQuery("from SolicitudRevision", SolicitudRevision.class)
        .getResultList();
  }
}