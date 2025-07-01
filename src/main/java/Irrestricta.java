import java.util.List;

public class Irrestricta implements Navegador{


  @Override
  public List<Hecho> NavegarHechosEn(Coleccion coleccion) {
    return coleccion.getHechos();
  }
}
