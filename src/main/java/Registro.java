public class Registro {
  private String nombre;
  private String apellido;
  private int edad;

  public Registro(String nombre) {
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre es requerido");
    }
    this.nombre = nombre;
    this.edad = 0;
    this.apellido = "";
  }

  public Registro(String nombre, String apellido, int edad) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;

  }

}



