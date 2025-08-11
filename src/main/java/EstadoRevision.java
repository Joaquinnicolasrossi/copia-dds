public interface EstadoRevision {
  void aplicar(Hecho hecho) throws Exception;
}
