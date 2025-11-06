import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CronTab {

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.err.println("Debe proveer una tarea (ej: fuentedemoadapter)");
        System.exit(1);
      }

      String tarea = args[0];

      switch (tarea) {
        case "fuentedemoadapter": {
          RepoProvincias repoProvincias = new RepoProvincias();
          RepoHechos repo = new RepoHechos(repoProvincias);

          FuenteDemoAdapter fuenteDemo = new FuenteDemoAdapter(
              new URL("https://api.demo/estado"), new ConexionGenerica(), LocalDateTime.now(), repo);

          actualizarFuenteDemo(fuenteDemo);
        }
        break;

        case "fuenteagregador":
          RepoProvincias repoProvincias = new RepoProvincias();
          List<Fuente> fuentes = new ArrayList<>();
          RepoHechos repo = new RepoHechos(repoProvincias);
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

        case "recalcularestadistica":
          DetectorDeSpam spam2 = new DetectorDeSpamFiltro();

          RepoEstadistica repoEstadistica = new RepoEstadistica();
          GeneradorEstadistica generador = new GeneradorEstadistica(repoEstadistica);

          RepoSolicitudes repoSolicitudes2 = new RepoSolicitudes(spam2);
          RepoColecciones repoColecciones2 = new RepoColecciones(repoSolicitudes2);

          List<Long> idColecciones = repoColecciones2.getIdsColecciones();
          idColecciones.forEach(generador::generarTodas);
          break;

        default:
          System.err.println("Tarea desconocida: " + tarea);
          System.exit(1);
      }

      System.out.println("Tarea '" + tarea + "' completada exitosamente.");

    } catch (Exception e) {
      System.err.println("Error ejecutando la tarea: " + e.getMessage());
      e.printStackTrace();
    } finally {
      WithSimplePersistenceUnit.shutdown();
      System.out.println("Sistema de persistencia apagado.");
    }
  }

  private static void actualizarFuenteDemo(FuenteDemoAdapter fuenteDemo) {
    fuenteDemo.actualizarHechos();
    System.out.println("FuenteDemo actualizada en " + LocalDateTime.now());
  }

  private static void actualizarFuenteAgregada(FuenteAgregada fuenteAgregada) {
    fuenteAgregada.actualizarRepositorio();
    System.out.println("FuenteAgregada actualizada");
  }

  private static void recalcularConsensosDeColecciones(List<Coleccion> colecciones) {
    colecciones.forEach(Coleccion::recalcularConsensos);
  }
}