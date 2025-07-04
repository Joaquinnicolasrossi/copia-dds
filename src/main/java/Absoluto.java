import java.util.List;

public class Absoluto implements AlgoritmoConsenso {

  // Si todas las fuentes del nodo contienen el mismo hecho

  @Override
  public boolean estaConsensuado(Hecho hecho, List<Fuente> fuentes) {

    return fuentes.stream()
        // Mas eficiente - Devuelve True si cumple con la condiciòn
        .allMatch(f -> f.extraerHechos().contains(hecho));

  }
}

