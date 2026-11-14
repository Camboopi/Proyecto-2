package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Venta {
    protected String fecha;
    protected String rubroContable;
    protected Usuario comprador;
    protected double subtotal;
    protected double descuentoEmpleado;
    protected BonoDescuento bonoAplicado;
    protected double descuentoBono;
    protected int puntosRedimidos;
    protected double valorTotal;
    protected int puntosFidelidadGenerados;
    protected List<DetalleVenta> detalles;

    public Venta(String fecha, String rubroContable, Usuario comprador) {
        this.fecha = fecha;
        this.rubroContable = rubroContable;
        this.comprador = comprador;
        this.detalles = new ArrayList<DetalleVenta>();
    }

    public void agregarDetalle(DetalleVenta det) {
        this.detalles.add(det);
        recalcularTotales();
    }

    /**
     * Aplica el codigo de descuento de un empleado (15% propio o 8% para socios).
     * Los empleados y socios SI pueden combinar este descuento con puntos de fidelidad,
     * pero NO es acumulable con un bono de torneo recreativo.
     */
    public boolean aplicarCodigoDescuento(Empleado empleadoDuenoCodigo) {
        if (empleadoDuenoCodigo == null || this.bonoAplicado != null) {
            return false;
        }
        double porcentaje = empleadoDuenoCodigo.obtenerDescuento(comprador);
        this.descuentoEmpleado = this.subtotal * porcentaje;
        recalcularTotales();
        return true;
    }

    /**
     * Aplica un bono de descuento obtenido al ganar un Torneo Recreativo.
     * Regla del negocio: el bono NO es acumulable con otros descuentos (ni descuento de empleado ni puntos).
     */
    public boolean aplicarBonoTorneoRecreativo(BonoDescuento bono) {
        if (bono == null || !bono.puedeRedimirse()) {
            return false;
        }
        if (this.descuentoEmpleado > 0 || this.bonoAplicado != null || this.puntosRedimidos > 0) {
            return false;
        }
        this.bonoAplicado = bono;
        this.bonoAplicado.marcarComoRedimido();
        this.descuentoBono = this.subtotal * bono.getPorcentajeDescuento();
        recalcularTotales();
        return true;
    }

    public int calcularPuntosFidelidad2Porciento() {
        if (comprador != null && comprador.puedeUsarPuntosFidelidad()) {
            return (int) (valorTotal * 0.02);
        }
        return 0;
    }

    /**
     * Permite redimir puntos de fidelidad (1 punto = $1 peso) tanto a Socios como a Empleados,
     * incluso si ya aplicaron su descuento de empleado (15% o 8%).
     * No es acumulable si ya se aplico un bono de torneo recreativo.
     */
    public boolean redimirPuntosComoDescuento(int pts) {
        if (this.bonoAplicado != null) {
            return false;
        }
        if (comprador != null && comprador.puedeUsarPuntosFidelidad()) {
            if (comprador.redimirPuntos(pts)) {
                this.puntosRedimidos = this.puntosRedimidos + pts;
                recalcularTotales();
                return true;
            }
        }
        return false;
    }

    public abstract void recalcularTotales();

    public void acreditarPuntosSocio() {
        if (comprador != null && comprador.puedeUsarPuntosFidelidad()) {
            this.puntosFidelidadGenerados = calcularPuntosFidelidad2Porciento();
            comprador.acumularPuntos2Porciento(valorTotal);
        }
    }

    public String getFecha() {
        return fecha;
    }

    public String getRubroContable() {
        return rubroContable;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuentoEmpleado() {
        return descuentoEmpleado;
    }

    public BonoDescuento getBonoAplicado() {
        return bonoAplicado;
    }

    public double getDescuentoBono() {
        return descuentoBono;
    }

    public int getPuntosRedimidos() {
        return puntosRedimidos;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public int getPuntosFidelidadGenerados() {
        return puntosFidelidadGenerados;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}
