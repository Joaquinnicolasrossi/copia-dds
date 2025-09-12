import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "estadistica")
public class EstadisticaRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coleccion_id")
    private Long coleccionId;
    private String tipo;
    private String valor;
    private Integer cantidad;
    private Boolean visiblePublico;
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fecha_actualizacion;

    public EstadisticaRegistro() {
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Long getColeccionId() {
        return coleccionId;
    }

    public void setColeccionId(Long coleccionId) {
        this.coleccionId = coleccionId;
    }

    public LocalDateTime getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(LocalDateTime fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getVisiblePublico() {
        return visiblePublico;
    }

    public void setVisiblePublico(Boolean visiblePublico) {
        this.visiblePublico = visiblePublico;
    }
}
