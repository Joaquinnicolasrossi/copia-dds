import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // Asegúrate de importar Mockito

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when; // Asegúrate de importar when

class ExportadorEstadisticasTest {

  @Test
  void testExportarPorTipo() throws IOException {
    RepoEstadistica mockRepo = Mockito.mock(RepoEstadistica.class);
    ExportadorEstadisticas exportador = new ExportadorEstadisticas(mockRepo);
    Coleccion mockColeccion = Mockito.mock(Coleccion.class);
    when(mockColeccion.getId()).thenReturn(123L);
    EstadisticaRegistro reg = new EstadisticaRegistro();
    reg.setId(99L);
    reg.setColeccion(mockColeccion);

    reg.setTipo("CATEGORIA_MAYOR_HECHOS");
    reg.setValor("CategoriaX");
    reg.setCantidad(42);
    reg.setVisiblePublico(true);
    reg.setFecha_actualizacion(LocalDateTime.of(2025, 9, 11, 18, 0));

    when(mockRepo.buscarPorTipo("CATEGORIA_MAYOR_HECHOS"))
        .thenReturn(List.of(reg));

    Path tempFile = Files.createTempFile("exportado", ".csv");

    exportador.exportarPorTipo("CATEGORIA_MAYOR_HECHOS", tempFile.toString());
    List<String> lineas = Files.readAllLines(tempFile);

    String header = lineas.get(0).replace("\"", "");
    assertEquals("id,coleccionId,tipo,valor,cantidad,visiblePublico,fechaActualizacion", header);

    String[] campos = lineas.get(1).split(",");
    for (int i = 0; i < campos.length; i++) {
      campos[i] = campos[i].replace("\"", "");
    }

    assertArrayEquals(new String[]{
        "99", "123", "CATEGORIA_MAYOR_HECHOS", "CategoriaX", "42", "true", "2025-09-11 18:00:00"
    }, campos);

    Files.delete(tempFile);
  }
}