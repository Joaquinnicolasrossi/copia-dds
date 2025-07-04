import java.util.List;

// si al menos dos fuentes del nodo contienen un mismo hecho
// y ninguna otra fuente del nodo contiene otro de igual título
// pero diferentes atributos, se lo considera consensuado;

public class MultiplesMenciones implements AlgoritmoConsenso {
  @Override
  public boolean estaConsensuado(Hecho hecho, List<Fuente> fuentes) {

    // Contamos la cantidad de coincidencias para la primer validaciòn
    long coincidencias = fuentes.stream()
        .filter(f -> f.extraerHechos().contains(hecho))
        .count();

    // Verificamos si existe un hecho con el mismo tìtulo pero con diferente
    // contenido
    boolean hayConflicto = fuentes.stream()
        .flatMap(f -> f.extraerHechos().stream())
        .anyMatch(h -> h.getTitulo().equals(hecho.getTitulo()) && !h.equals(hecho));

    return coincidencias >= 2 && !hayConflicto;
  }
}
