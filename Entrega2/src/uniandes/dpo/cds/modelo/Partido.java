package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Partido {
    protected String fecha;
    protected boolean disputado;
    protected boolean cancelado;
    protected String motivoCancelacion;
    protected List<EstadisticaJugadorPartido> estadisticas;

    public Partido(String fecha) {
        this.fecha = fecha;
        this.disputado = false;
        this.cancelado = false;
        this.motivoCancelacion = null;
        this.estadisticas = new ArrayList<EstadisticaJugadorPartido>();
    }

    public abstract boolean puedeDisputarse();

    public void registrarEstadisticasIndividuales(EstadisticaJugadorPartido est) {
        this.estadisticas.add(est);
    }

    public void cancelarPartido(String motivo) {
        this.cancelado = true;
        this.disputado = false;
        this.motivoCancelacion = motivo;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isDisputado() {
        return disputado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public List<EstadisticaJugadorPartido> getEstadisticas() {
        return estadisticas;
    }
}
