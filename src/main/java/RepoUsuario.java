import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

class RepoUsuario implements WithSimplePersistenceUnit {

  public void save( Usuario usuario){
    entityManager().getTransaction().begin();
    entityManager().persist(usuario);
    entityManager().getTransaction().commit();
  }


  public Usuario findByUser(String email) {
    return entityManager().createQuery("select u from usuario u where email = :amail",Usuario.class).
        setParameter("email",email).
        getSingleResult();
  }


}