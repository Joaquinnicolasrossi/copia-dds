package Exceptions;

public class HechoMalGeneradoException extends RuntimeException {
  public HechoMalGeneradoException() {
    super("El hecho no pudo ser creado exitosamente");
  }
}
