import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("METAMAPA")
public class FuenteMetaMapa extends Fuente {

  @Transient
  private ClienteMetaMapa cliente;

  public FuenteMetaMapa(ClienteMetaMapa cliente) {
    this.cliente = cliente;
  }
  public FuenteMetaMapa() {}
  @Override
  public List<Hecho> extraerHechos() {
    try {
      return cliente.getHechos();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getIdentificador(){
    return "metamapa";
  }
}