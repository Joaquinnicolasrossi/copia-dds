import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.print.attribute.standard.Media;

@Entity
@Table(name="multimedia")
public class Multimedia {

  public Multimedia() {}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hecho_id", nullable = false)
  private Hecho hecho;

  private String tipo;
  private long tamanio;
  private String url;

  public Long getId() {
    return id;
  }

  public Hecho getHecho() {
    return hecho;
  }

  public String getTipo() {
    return tipo;
  }

  public long getTamanio() {
    return tamanio;
  }

  public String getUrl() {
    return url;
  }

  public void setHecho(Hecho hecho) {
    this.hecho = hecho;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public void setTamanio(long tamanio) {
    this.tamanio = tamanio;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
