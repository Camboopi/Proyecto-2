package uniandes.dpo.cds.modelo;

public class TorneoCompetitivo extends TorneoInterno {
    private double tarifaInscripcion;
    private double totalRecaudado;
    private double porcentajePremioEfectivo;
    private double premioEfectivoEntregado;

    public TorneoCompetitivo(String nombre, Deporte deporte, String categoriaONivel, String modalidad,
                             int maxCupos, String diaTorneo, double tarifaInscripcion) {
        this(nombre, deporte, categoriaONivel, modalidad, maxCupos, diaTorneo, tarifaInscripcion, 0.80);
    }

    public TorneoCompetitivo(String nombre, Deporte deporte, String categoriaONivel, String modalidad,
                             int maxCupos, String diaTorneo, double tarifaInscripcion, double porcentajePremioEfectivo) {
        super(nombre, deporte, categoriaONivel, modalidad, maxCupos, diaTorneo);
        this.tarifaInscripcion = tarifaInscripcion;
        this.totalRecaudado = 0.0;
        this.porcentajePremioEfectivo = porcentajePremioEfectivo > 0 ? porcentajePremioEfectivo : 0.80;
        this.premioEfectivoEntregado = 0.0;
    }

    @Override
    protected double calcularCostoInscripcionPara(boolean esEmpleado) {
        if (esEmpleado) {
            return 0.0;
        }
        return tarifaInscripcion;
    }

    @Override
    protected void registrarIngresoInscripcion(double monto) {
        if (monto > 0) {
            this.totalRecaudado = this.totalRecaudado + monto;
        }
    }

    @Override
    protected void reversarIngresoInscripcion(double monto) {
        if (monto > 0) {
            this.totalRecaudado = Math.max(0.0, this.totalRecaudado - monto);
        }
    }

    public double calcularPremioEfectivoProporcional() {
        return totalRecaudado * porcentajePremioEfectivo;
    }

    /**
     * En los torneos competitivos, si un empleado resulta ganador, NO recibe el premio en efectivo.
     */
    @Override
    protected void otorgarPremioAlGanador(ParticipanteTorneo ganador) {
        if (ganador == null || ganador.esEmpleado()) {
            this.premioEfectivoEntregado = 0.0;
        } else {
            this.premioEfectivoEntregado = calcularPremioEfectivoProporcional();
        }
    }

    @Override
    public String getTipoTorneo() {
        return "Competitivo";
    }

    @Override
    public String obtenerResumenPremio() {
        if (campeon != null && campeon.esEmpleado()) {
            return "Ganador es Empleado del club -> Premio en efectivo entregado: $0 (Total recaudado: $" + Math.round(totalRecaudado) + ")";
        }
        return "Premio en efectivo entregado: $" + Math.round(premioEfectivoEntregado)
                + " (" + Math.round(porcentajePremioEfectivo * 100) + "% de $" + Math.round(totalRecaudado) + " recaudados)";
    }

    public double getTarifaInscripcion() {
        return tarifaInscripcion;
    }

    public double getTotalRecaudado() {
        return totalRecaudado;
    }

    public double getPorcentajePremioEfectivo() {
        return porcentajePremioEfectivo;
    }

    public double getPremioEfectivoEntregado() {
        return premioEfectivoEntregado;
    }
}
