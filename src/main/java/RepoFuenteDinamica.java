import java.util.ArrayList;
import java.util.List;

public class RepoFuenteDinamica {
  List<Hecho> hechos = new ArrayList<>();

  public Hecho save(Hecho hecho) {
    hechos.add(hecho);
    return hecho;
  }

}
