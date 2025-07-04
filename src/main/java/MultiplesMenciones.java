import java.util.List;

// si al menos dos fuentes del nodo contienen un mismo hecho
// y ninguna otra fuente del nodo contiene otro de igual título
// pero diferentes atributos, se lo considera consensuado;

public class MultiplesMenciones implements AlgoritmoConsenso {
  @Override
  public boolean estaConsensuado(Hecho hecho, Fuente fuenteDelNodo) {

    // Contamos la cantidad de coincidencias para la primer validaciòn
    List<Hecho> hechos = fuenteDelNodo.extraerHechos();
    if(hechos.isEmpty()){ return false; }
    long coincidencias = hechos.stream()
        .filter(h -> h.tieneMismoContenidoQue(hecho))
        .count();

    // Verificamos si existe un hecho con el mismo tìtulo pero con diferente
    // contenido
    boolean hayConflicto = hechos.stream()
        .filter(h -> h.getTitulo().equals(hecho.getTitulo()))
        .anyMatch(h -> !h.tieneMismoContenidoQue(hecho));

    return coincidencias >= 2 && !hayConflicto;
  }
}
