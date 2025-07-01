import java.util.Collections;
import java.util.List;

public class Curada implements Navegador {


  @Override
  public List<Hecho> NavegarHechosEn(Coleccion coleccion) {
    var tipoAlgoritmo = coleccion.getTipoAlgoritmoConsenso();

    if (tipoAlgoritmo == null) {
      return coleccion.getHechos();
    }
    return coleccion.getHechos().stream().filter(hecho ->
        tipoAlgoritmo.estaConsensuado(hecho, coleccion.getFuentes())).toList();
  }
}
