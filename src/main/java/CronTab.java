import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CronTab {

  public void main(String[] args) throws MalformedURLException {
    // pasamos como argumento para distinguir las fuentes
    String tarea = args[0];

    switch (tarea) {
      case "fuentedemoadapter": {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
        EntityManager em = emf.createEntityManager();

        RepoHechos repo = new RepoHechos();
        repo.setEntityManager(em);

        FuenteDemoAdapter fuenteDemo = new FuenteDemoAdapter(
            new URL("https://api.demo/estado"), new ConexionGenerica(), LocalDateTime.now(), repo);

        actualizarFuenteDemo(fuenteDemo);

        em.close();
        emf.close();
      }
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
      case "recalcularestadistica":
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        EntityManager em = emf.createEntityManager();
        try {
          DetectorDeSpam spam2 = new DetectorDeSpamFiltro();

          RepoEstadistica repoEstadistica = new RepoEstadistica(em);
          GeneradorEstadistica generador = new GeneradorEstadistica(repoEstadistica);

          RepoSolicitudes repoSolicitudes2 = new RepoSolicitudes(spam2);
          RepoColecciones repoColecciones2 = new RepoColecciones(repoSolicitudes2);

          List<Long> idColecciones = repoColecciones2.getIdsColecciones();
          recalcularEstadisticas(generador, idColecciones);
        } finally {
          // liberar recursos
          em.close();
          emf.close();
        }
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

  private static void recalcularConsensosDeColecciones(List<Coleccion> colecciones) {
    colecciones.forEach(Coleccion::recalcularConsensos);
  }

  private static void recalcularEstadisticas(GeneradorEstadistica  generador , List<Long> idColecciones){
    idColecciones.forEach(  coleccionId -> {
      generador.generarCategoriaConMayorHechos(coleccionId);
      generador.generarProvinciaConMayorHechos(coleccionId);
    }  );
  }
}