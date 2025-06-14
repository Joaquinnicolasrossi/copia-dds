public interface EstadoRevision {
  void aplicar(Hecho hecho, String sugerencia);
}
