import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ExportadorEstadisticasTest {

  @Test
  void testExportarPorTipo() throws IOException {
    // Arrange
    RepoEstadistica mockRepo = Mockito.mock(RepoEstadistica.class);
    ExportadorEstadisticas exportador = new ExportadorEstadisticas(mockRepo);

    EstadisticaRegistro reg = new EstadisticaRegistro();
    reg.setId(99L);
    reg.setColeccionId(123L);
    reg.setTipo("CATEGORIA_MAYOR_HECHOS");
    reg.setValor("CategoriaX");
    reg.setCantidad(42);
    reg.setVisiblePublico(true);
    reg.setFechaActualizacion(LocalDateTime.of(2025, 9, 11, 18, 0));

    when(mockRepo.buscarPorTipo("CATEGORIA_MAYOR_HECHOS"))
        .thenReturn(List.of(reg));

    Path tempFile = Files.createTempFile("exportado", ".csv");

    // Act
    exportador.exportarPorTipo("CATEGORIA_MAYOR_HECHOS", tempFile.toString());

    // Assert
    List<String> lineas = Files.readAllLines(tempFile);

    // Header → sin comillas
    String header = lineas.get(0).replace("\"", "");
    assertEquals("id,coleccionId,tipo,valor,cantidad,visiblePublico,fechaActualizacion", header);

    // Registro → remover comillas antes de comparar
    String[] campos = lineas.get(1).split(",");
    for (int i = 0; i < campos.length; i++) {
      campos[i] = campos[i].replace("\"", "");
    }

    assertArrayEquals(new String[]{
        "99", "123", "CATEGORIA_MAYOR_HECHOS", "CategoriaX", "42", "true", "2025-09-11 18:00:00"
    }, campos);
  }
}

