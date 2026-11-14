package uniandes.dpo.cds.modelo;

public class BonoDescuento {
    private String codigo;
    private double porcentajeDescuento;
    private String nombreTorneoOrigen;
    private boolean redimido;

    public BonoDescuento(String codigo, double porcentajeDescuento, String nombreTorneoOrigen) {
        this.codigo = codigo;
        this.porcentajeDescuento = porcentajeDescuento;
        this.nombreTorneoOrigen = nombreTorneoOrigen;
        this.redimido = false;
    }

    public boolean puedeRedimirse() {
        return !redimido && porcentajeDescuento > 0;
    }

    public void marcarComoRedimido() {
        this.redimido = true;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public String getNombreTorneoOrigen() {
        return nombreTorneoOrigen;
    }

    public boolean isRedimido() {
        return redimido;
    }

    public void setRedimido(boolean redimido) {
        this.redimido = redimido;
    }
}
