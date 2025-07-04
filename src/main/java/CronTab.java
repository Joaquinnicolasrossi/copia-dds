import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CronTab {

  public void main(String[] args) throws MalformedURLException {
    // pasamos como argumento para distinguir las fuentes
    String tarea = args[0];

    switch (tarea) {
      case "fuentedemoadapter":
        FuenteDemoAdapter fuenteDemo = new FuenteDemoAdapter(
            new URL("https://api.demo/estado"), new ConexionGenerica());
        actualizarFuenteDemo(fuenteDemo);
        break;
      case "fuenteagregador":
        List<Fuente> fuentes = new ArrayList<>();
        RepoHechos repo = new RepoHechos();
        FuenteAgregada fuenteAgregada = new FuenteAgregada(fuentes, repo);
        actualizarFuenteAgregada(fuenteAgregada);
        break;
      case "recalcularconsensos":
        DetectorDeSpamFiltro spam = new DetectorDeSpamFiltro();
        RepoSolicitudes repoSolicitudes = new RepoSolicitudes(spam);
        RepoColecciones repoColecciones = new RepoColecciones(repoSolicitudes);
        List<Coleccion> colecciones = repoColecciones.getColecciones();
        recalcularConsensosDeColecciones(colecciones);
        break;
      default:
        System.err.println("Tarea desconocida: " + tarea);
        System.exit(1);
    }
  }

  private static void actualizarFuenteDemo(FuenteDemoAdapter fuenteDemo) throws MalformedURLException {
    fuenteDemo.actualizarHechos();
    System.out.println("FuenteDemo actualizada en " + LocalDateTime.now());
  }

  private static void actualizarFuenteAgregada(FuenteAgregada fuenteAgregada) {
    fuenteAgregada.actualizarRepositorio();
    System.out.println("FuenteAgregada actualizada");
  }

  private static void recalcularConsensosDeColecciones(List<Coleccion> colecciones){
      colecciones.forEach(Coleccion::recalcularConsensos);
    }

}