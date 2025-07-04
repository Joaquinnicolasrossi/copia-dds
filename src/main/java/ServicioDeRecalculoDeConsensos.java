import java.util.List;

public class ServicioDeRecalculoDeConsensos {
  private final List<Coleccion> colecciones;

  public ServicioDeRecalculoDeConsensos(List<Coleccion> colecciones, List<Fuente> fuentesDelNodo) {
    this.colecciones = colecciones;
  }
  public void recalcularTodos() {


    colecciones.stream()
        .filter(c -> c.getAlgoritmoConsenso() != null)
        .filter(c -> c.getFuentes() instanceof FuenteAgregada)
        .forEach(Coleccion::recalcularConsensos);
  }

}

