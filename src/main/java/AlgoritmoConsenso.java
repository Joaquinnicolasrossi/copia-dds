/*
  Estrategia de consenso: se utiliza para definir los distintos algoritmos

  Recibe
  - Un hecho (el que se quiere validar)

  Devuelve
  - True si està consensuado
*/

import java.util.List;
import java.util.Map;

public interface AlgoritmoConsenso {
  boolean estaConsensuado(Hecho hecho, Map<Fuente, List<Hecho>> hechosPorFuente);
}