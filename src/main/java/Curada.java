import java.util.Collections;
import java.util.List;

public class Curada implements Navegador {
  private TipoAlgoritmoConsenso tipoAlgoritmoConsenso;

  @Override
  public List<Hecho> NavegarHechosEn(Coleccion coleccion) {
    return coleccion.getHechos().stream().filter(hecho ->
        tipoAlgoritmoConsenso.estaConsensuado(hecho, coleccion.getFuentes())).toList();
  }
}
