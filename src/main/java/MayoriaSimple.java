/*
  Strategy
  El hecho està consensuado si aparece, al menos, en la mitad
  de las fuentes del nodo
 */

import java.util.List;

public class MayoriaSimple implements AlgoritmoConsenso {

  @Override
  public boolean estaConsensuado(Hecho hecho, List<Fuente> fuentes){

    long coincidencias = fuentes.stream()
        .filter(f -> f.extraerHechos().contains(hecho))
        .count();
    return coincidencias >= Math.ceil(fuentes.size() / 2.0);
  }
}
