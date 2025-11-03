import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

class RepoUsuario implements WithSimplePersistenceUnit {

  public void save( Usuario usuario){
    entityManager().getTransaction().begin();
    entityManager().persist(usuario);
    entityManager().getTransaction().commit();
  }


  public Usuario findByUser(String email) {
    email = email.trim().toLowerCase();
    System.out.println("Buscando en base: " + email);
    List<Usuario> usuarios = entityManager()
        .createQuery("select u from Usuario u where lower(u.email) = :email", Usuario.class)
        .setParameter("email", email)
        .getResultList();
    return usuarios.isEmpty() ? null : usuarios.get(0);
  }


}