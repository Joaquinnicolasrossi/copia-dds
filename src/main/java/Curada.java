import java.util.Collections;
import java.util.List;

public class Curada implements Navegador {

  // Obtiene la lista de hechos filtrados por criterios y solicitudes
  // Filtra y Junta todos los hechos "consensuados" en una nueva lista
  @Override
  public List<Hecho> NavegarHechosEn(Coleccion coleccion) {
    return coleccion.mostrarHechos().stream()
        .filter(coleccion::estaConsensuado)
        .toList();
  }
}
