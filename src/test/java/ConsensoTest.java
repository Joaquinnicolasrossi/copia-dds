import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsensoTest {

  private Hecho h1;
  private Hecho h1_copy;
  private Hecho hDistinto;

  private Fuente f1;
  private Fuente f2;
  private FuenteAgregada fuenteAgregada;

  @BeforeEach
  void setUp() {

    h1 = new Hecho("Incendio", "Fuego grande", "Incendio",
        -31.4, -64.1,
        LocalDateTime.now(), LocalDateTime.now(), Estado.PENDIENTE);

    h1_copy = new Hecho("Incendio", "Fuego grande", "Incendio",
        -31.4, -64.1,
        LocalDateTime.now(), LocalDateTime.now(), Estado.PENDIENTE);

    hDistinto = new Hecho("Incendio", "Otro texto", "Incendio",
        -31.4, -64.1,
        LocalDateTime.now(), LocalDateTime.now(), Estado.PENDIENTE);


    // FAKES de fuentes
    f1 = mock(Fuente.class);
    f2 = mock(Fuente.class);

    when(f1.extraerHechos()).thenReturn(List.of(h1, h1_copy));
    when(f2.extraerHechos()).thenReturn(List.of(h1));

    // Fuente agregada combinando f1 y f2
    fuenteAgregada = mock(FuenteAgregada.class);
    when(fuenteAgregada.extraerHechos()).thenReturn(List.of(h1, h1_copy, h1));
  }


  @Test
  void testAbsolutoFalseSiNoTodasLasFuentesCoinciden() {
    Absoluto a = new Absoluto();

    // hay 1 distinto así que debe dar false
    when(fuenteAgregada.extraerHechos()).thenReturn(List.of(h1, h1_copy, hDistinto));

    assertFalse(a.estaConsensuado(h1, fuenteAgregada));
  }

  @Test
  void testMultiplesMencionesOk() {
    MultiplesMenciones m = new MultiplesMenciones();

    assertTrue(m.estaConsensuado(h1, fuenteAgregada));
  }

  @Test
  void testMayoriaSimpleTrue() {
    MayoriaSimple ms = new MayoriaSimple();

    // 3 hechos, 2 coinciden
    assertTrue(ms.estaConsensuado(h1, fuenteAgregada));
  }

  @Test
  void testMayoriaSimpleFalse() {
    MayoriaSimple ms = new MayoriaSimple();

    when(fuenteAgregada.extraerHechos()).thenReturn(List.of(h1, hDistinto, hDistinto));

    assertFalse(ms.estaConsensuado(h1, fuenteAgregada));
  }
}