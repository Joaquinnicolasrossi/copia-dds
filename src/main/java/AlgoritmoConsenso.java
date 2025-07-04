import java.util.List;

/*
  Estrategia de consenso: se utiliza para definir los distintos algoritmos

  Recibe
  - Un hecho (el que se quiere validar
  - Una lista con todas las fuentes de un nodo

  Devuelve
  - True si està consensuado
*/

public interface TipoAlgoritmoConsenso {
  boolean estaConsensuado(Hecho hecho, List<Fuente> fuentes);
}