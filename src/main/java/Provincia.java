import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "provincia")
public class Provincia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String nombre;

  public Provincia() {
  }

  public Provincia(String nombre) {
    this.setNombre(nombre);
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException();
    }
    String lower = nombre.toLowerCase();
    this.nombre = Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Provincia provincia = (Provincia) o;
    return Objects.equals(nombre, provincia.nombre);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre);
  }
}