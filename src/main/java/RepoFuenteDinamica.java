import java.util.ArrayList;
import java.util.List;

public class RepoFuenteDinamica {
  List<Hecho> hechos = new ArrayList<>();

  public Hecho save(Hecho hecho) {
    hechos.add(hecho);
    return hecho;
  }

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
    Hecho actualizado = hechoOriginal.actualizarHecho(hechoOriginal, hechoBuilder);
    hechos.remove(hechoOriginal);
    hechos.add(actualizado);
  }
  public void rechazar(Hecho hecho){
    hechos.remove(hecho);
  }
  //esto era para probar algo , despues lo borro
//  public Hecho save(Hecho hecho) {
//    entityManager.getTransaction().begin();
//    entityManager.persist(hecho);
//    entityManager.getTransaction().commit();
//    return hecho;
//  }
//
//  public List<Hecho> getHechos() {
//    return entityManager.createQuery("Select from hecho", Hecho.class).getResultList();
//  }
//
//  public Hecho findById(long hechoid) {
//    return entityManager.createQuery(
//            "SELECT h FROM Hecho h WHERE h.id = :hechoid", Hecho.class)
//        .setParameter("hechoid", hechoid).getSingleResult();
//  }
//
//
//  public void saveUpdate(Hecho hechoOriginal, Hecho.HechoBuilder hechoBuilder) {
//    entityManager.getTransaction().begin();
//
//    Hecho actualizado = hechoOriginal.actualizarHecho(hechoOriginal, hechoBuilder);
//    entityManager.merge(actualizado);
//
//    entityManager.getTransaction().commit();
//
//  }
}
