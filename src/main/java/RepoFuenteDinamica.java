import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.ArrayList;
import java.util.List;


public class RepoFuenteDinamica implements WithSimplePersistenceUnit {

  List<Hecho> hechos = new ArrayList<>();
  RepoProvincias repoProvincias = new RepoProvincias();

  public List<Hecho> getHechos() {
    return new ArrayList<>(hechos);
  }

  public Hecho findByTitulo(String titulo) {
    return hechos.stream()
        .filter(h -> h.getTitulo().equals(titulo))
        .findFirst()
        .orElse(null);
  }

  public void saveUpdate(Hecho hechoOriginal, Hecho.HechoBuilder hechoBuilder) {
    Hecho actualizado = hechoOriginal.actualizarHecho(hechoOriginal, hechoBuilder, repoProvincias);
    hechos.remove(hechoOriginal);
    hechos.add(actualizado);
  }

  public void rechazar(Hecho hecho) {
    hechos.remove(hecho);
  }

   public Hecho save(Hecho hecho) {
   hechos.add(hecho);
   return hecho;
   }

   //metodos para persistir en mysql
  public Hecho save_(Hecho hecho) {
    entityManager().getTransaction().begin();
    entityManager().persist(hecho);
    entityManager().getTransaction().commit();
    return hecho;
  }

   public List<Hecho> getHechos_() {
   return entityManager().createQuery("Select from hecho",
   Hecho.class).getResultList();
   }

   public Hecho findById(long hechoid) {
   return entityManager().createQuery(
   "SELECT h FROM Hecho h WHERE h.id = :hechoid", Hecho.class)
   .setParameter("hechoid", hechoid).getSingleResult();
   }


   public void saveUpdate_(Hecho hechoOriginal, Hecho.HechoBuilder hechoBuilder)
   {
   entityManager().getTransaction().begin();

   Hecho actualizado = hechoOriginal.actualizarHecho(hechoOriginal, hechoBuilder, repoProvincias);
   entityManager().merge(actualizado);

   entityManager().getTransaction().commit();

   }
}
