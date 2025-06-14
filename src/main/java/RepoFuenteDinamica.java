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

}
