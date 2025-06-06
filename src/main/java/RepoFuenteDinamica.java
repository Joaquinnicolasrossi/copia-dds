import java.util.ArrayList;
import java.util.List;

public class RepoFuenteDinamica {
  List<Hecho> hechos = new ArrayList<>();

  public Hecho save(Hecho hecho) {
    hechos.add(hecho);
    return hecho;
  }

  public List<Hecho> getHechos() {
    return hechos;
  }

  public Hecho findByTitulo(String titulo) {
    return hechos.stream()
        .filter(h -> h.getTitulo().equals(titulo))
        .findFirst()
        .orElse(null);
  }

  public Hecho saveUpdate(Hecho hechoOriginal, Hecho.HechoBuilder hechoBuilder) {
    Hecho actualizado = hechoOriginal.actualizarHechoConBuilderParcial(hechoOriginal, hechoBuilder);
    hechos.removeIf(h -> h.getTitulo().equals(hechoOriginal.getTitulo()));
    hechos.add(actualizado);
    return actualizado;
  }

}
