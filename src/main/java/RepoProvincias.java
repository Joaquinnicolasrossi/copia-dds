import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoProvincias implements WithSimplePersistenceUnit {
  private final EntityManager entityManager;

  // 2. Constructor para PRODUCCIÓN (usa la biblioteca)
  public RepoProvincias() {
    this.entityManager = null;
  }

  // 3. Constructor para TESTING (recibe el EM manual)
  public RepoProvincias(EntityManager em) {
    this.entityManager = em;
  }

  private EntityManager em() {
    return (this.entityManager != null) ? this.entityManager : entityManager();
  }

  public Provincia findByNombre(String nombre) {
    String nombreCapitalizado = new Provincia(nombre).getNombre();
    TypedQuery<Provincia> query = em().createQuery(
        "SELECT p FROM Provincia p WHERE p.nombre = :nombre", Provincia.class);
    query.setParameter("nombre", nombreCapitalizado);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public void guardar(Provincia provincia) {
    em().persist(provincia);
  }

  public Provincia findOrCreate(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      return null;
    }
    Provincia provincia = findByNombre(nombre);
    if (provincia == null) {
      provincia = new Provincia(nombre);
      guardar(provincia);
    }
    return provincia;
  }
}