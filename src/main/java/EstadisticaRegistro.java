import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "estadisticas")
public class EstadisticaRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long coleccionId;
    private String tipo;
    private String valor;
    private Integer cantidad;
    private Boolean visiblePublico;
    private LocalDateTime fechaActualizacion;

}





