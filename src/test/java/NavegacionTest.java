import java.util.List;
import org.junit.jupiter.api.Test;

public class NavegacionTest {
 //NO ESTOY MUY SEGURO DE SI USAR UNA COLECCION COMO PARAMETRO PARA NAVEGAR_HECHOS

    @Test
    void NavegarModoCurada(){
      DetectorDeSpam detectorDeSpam = null;
      RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpam);

      Coleccion coleccion = new Coleccion(repoSolicitudes);


      ModoFactory modoFactory = new ModoFactory();
      Navegador navegador = modoFactory.obtenerModo(Modo.CURADA);
      navegador.NavegarHechosEn(coleccion);


  }
  @Test
  void NavegarModoIrrestrica(){
    DetectorDeSpam detectorDeSpam = null;
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpam);

    Coleccion coleccion = new Coleccion(repoSolicitudes);


    ModoFactory modoFactory = new ModoFactory();
    Navegador navegador = modoFactory.obtenerModo(Modo.IRRESTRICTA);
    navegador.NavegarHechosEn(coleccion);


  }
}







