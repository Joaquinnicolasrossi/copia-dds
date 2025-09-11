import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVHelperTest {

  @Test
  void testExportarEstadisticasCSV() throws IOException {
    // Arrange
    EstadisticaRegistro reg1 = new EstadisticaRegistro();
    reg1.setId(1L);
    reg1.setColeccionId(10L);
    reg1.setTipo("CANTIDAD_SOLICITUDES_SPAM");
    reg1.setValor("valorA");
    reg1.setCantidad(5);
    reg1.setVisiblePublico(true);
    reg1.setFechaActualizacion(LocalDateTime.of(2025, 9, 11, 12, 30));

    EstadisticaRegistro reg2 = new EstadisticaRegistro();
    reg2.setId(2L);
    reg2.setColeccionId(20L);
    reg2.setTipo("PROVINCIA_MAYOR_HECHOS");
    reg2.setValor("valorB");
    reg2.setCantidad(15);
    reg2.setVisiblePublico(false);
    reg2.setFechaActualizacion(LocalDateTime.of(2025, 9, 11, 14, 0));

    List<EstadisticaRegistro> registros = List.of(reg1, reg2);

    Path tempFile = Files.createTempFile("estadisticas", ".csv");

    // Act
    CSVHelper.exportarEstadisticasCSV(registros, tempFile.toString());

    // Assert
    List<String> lineas = Files.readAllLines(tempFile);

    assertEquals("\"id\",\"coleccionId\",\"tipo\",\"valor\",\"cantidad\",\"visiblePublico\",\"fechaActualizacion\"", lineas.get(0));

    String[] campos1 = lineas.get(1).split(",");
    assertArrayEquals(new String[]{
        "\"1\"", "\"10\"", "\"CANTIDAD_SOLICITUDES_SPAM\"", "\"valorA\"", "\"5\"", "\"true\"", "\"2025-09-11 12:30:00\""
    }, campos1);

    String[] campos2 = lineas.get(2).split(",");
    assertArrayEquals(new String[]{
        "\"2\"", "\"20\"", "\"PROVINCIA_MAYOR_HECHOS\"", "\"valorB\"", "\"15\"", "\"false\"", "\"2025-09-11 14:00:00\""
    }, campos2);
  }
}
