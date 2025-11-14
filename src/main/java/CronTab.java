import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CronTab {

  public static void main(String[] args) {
    try {
      configurePersistence();
      String tarea = args.length == 0 ? "all" : args[0];

      System.out.println("== Ejecutando tarea: " + tarea + " ==");

      switch (tarea.toLowerCase()) {

        case "fuentedemoadapter":
          ejecutarFuenteDemo();
          break;

        case "fuenteagregador":
          ejecutarFuenteAgregada();
          break;

        case "recalcularconsensos":
          ejecutarRecalculoConsensos();
          break;

        case "recalcularestadistica":
          ejecutarRecalculoEstadistica();
          break;

        case "all":
          System.out.println("Ejecutando TODAS las tareas...");
          ejecutarFuenteDemo();
          ejecutarFuenteAgregada();
          ejecutarRecalculoConsensos();
          ejecutarRecalculoEstadistica();
          break;

        default:
          System.err.println("Tarea desconocida: " + tarea);
          System.exit(1);
      }

      System.out.println("== Tarea '" + tarea + "' completada ==");

    } catch (Exception e) {
      System.err.println("ERROR ejecutando tarea: " + e.getMessage());
    } finally {
      WithSimplePersistenceUnit.shutdown();
      System.out.println("Sistema de persistencia apagado.");
    }
  }

  private static void ejecutarFuenteDemo() throws Exception {
    RepoProvincias repoProvincias = new RepoProvincias();
    RepoHechos repo = new RepoHechos(repoProvincias);

    FuenteDemoAdapter fuenteDemo = new FuenteDemoAdapter(
        new URL("https://api.demo/estado"),
        new ConexionGenerica(),
        LocalDateTime.now(),
        repo
    );

    fuenteDemo.actualizarHechos();
    System.out.println("[OK] FuenteDemo actualizada");
  }

  private static void ejecutarFuenteAgregada() {
    RepoProvincias repoProvincias = new RepoProvincias();
    List<Fuente> fuentes = new ArrayList<>();
    RepoHechos repo = new RepoHechos(repoProvincias);

    FuenteAgregada fa = new FuenteAgregada(fuentes, repo);
    fa.actualizarRepositorio();

    System.out.println("[OK] FuenteAgregada actualizada");
  }

  private static void ejecutarRecalculoConsensos() {
    DetectorDeSpamFiltro spam = new DetectorDeSpamFiltro();
    RepoSolicitudes repoS = new RepoSolicitudes(spam);
    RepoProvincias repoP = new RepoProvincias();
    RepoHechos repoH = new RepoHechos(repoP);
    RepoColecciones repoC = new RepoColecciones(repoS, repoH);

    repoC.getColecciones().forEach(Coleccion::recalcularConsensos);
    System.out.println("[OK] Consensos recalculados");
  }

  private static void ejecutarRecalculoEstadistica() {
    DetectorDeSpamFiltro spam = new DetectorDeSpamFiltro();
    RepoSolicitudes repoS = new RepoSolicitudes(spam);
    RepoProvincias repoP = new RepoProvincias();
    RepoHechos repoH = new RepoHechos(repoP);
    RepoColecciones repoC = new RepoColecciones(repoS, repoH);

    RepoEstadistica repoE = new RepoEstadistica();
    GeneradorEstadistica gen = new GeneradorEstadistica(repoE, repoC);

    repoC.getIdsColecciones().forEach(gen::generarTodas);
    System.out.println("[OK] Estadísticas recalculadas");
  }

  private static void configurePersistence() {
    Main.configurePersistence(); // reutilizás la config
  }
}
