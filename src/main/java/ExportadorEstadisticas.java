import java.io.IOException;
import java.util.List;

public class ExportadorEstadisticas {

  private final RepoEstadistica repoEstadistica;

  public ExportadorEstadisticas(RepoEstadistica repoEstadistica) {
    this.repoEstadistica = repoEstadistica;
  }

  public void exportarPorTipo(String tipo, String filePath) throws IOException {
    List<EstadisticaRegistro> registros = repoEstadistica.buscarPorTipo(tipo);
    CSVHelper.exportarEstadisticasCSV(registros, filePath);
  }
}