public class FuenteDinamica {
  RepoFuenteDinamica repoFuenteDinamica;


  public void subirHecho(Hecho hecho){
    repoFuenteDinamica.save(hecho);
  }


}
