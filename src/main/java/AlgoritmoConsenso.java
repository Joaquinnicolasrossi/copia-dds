/*
  Estrategia de consenso: se utiliza para definir los distintos algoritmos

  Recibe
  - Un hecho (el que se quiere validar)

  Devuelve
  - True si està consensuado
*/

public interface AlgoritmoConsenso {
  boolean estaConsensuado(Hecho hecho, Fuente fuente);
}