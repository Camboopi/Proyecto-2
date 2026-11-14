package uniandes.dpo.cds.modelo;

import java.util.List;

public class PartidoCompetencia extends Partido {
    private Competencia competencia;
    private String institucionRival;
    private String escenario;
    private String marcadorFinal;
    private List<Socio> alineados;

    public PartidoCompetencia(Competencia competencia, String fecha, String institucionRival, String escenario, List<Socio> alineados) {
        super(fecha);
        this.competencia = competencia;
        this.institucionRival = institucionRival;
        this.escenario = escenario;
        this.alineados = alineados;
    }

    public boolean validarEnNominaCompetencia() {
        if (competencia == null || alineados == null) {
            return false;
        }
        return competencia.getNominaOficial().containsAll(alineados);
    }

    public boolean verificarSinSuspensionActiva(Equipo eq) {
        if (eq == null || alineados == null) {
            return false;
        }
        for (int i = 0; i < alineados.size(); i++) {
            Socio s = alineados.get(i);
            InscripcionEquipo insc = eq.buscarInscripcion(s);
            if (insc == null || insc.tieneSuspensionActiva()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean puedeDisputarse() {
        if (competencia == null || competencia.getEquipoInscrito() == null || alineados == null) {
            cancelarPartido("Cancelado: competencia o alineación no válida");
            return false;
        }
        Equipo eq = competencia.getEquipoInscrito();
        boolean ok = validarEnNominaCompetencia()
                && verificarSinSuspensionActiva(eq)
                && alineados.size() >= competencia.getNominaMinimaHabilitados()
                && eq.obtenerJugadoresHabilitados().size() >= competencia.getNominaMinimaHabilitados();
        if (!ok) {
            cancelarPartido("Cancelado: jugador alineado con suspensión activa por acumulación de tarjetas amarillas / roja o mínimo de habilitados insuficiente");
            return false;
        }
        this.cancelado = false;
        this.motivoCancelacion = null;
        return true;
    }

    @Override
    public void registrarEstadisticasIndividuales(EstadisticaJugadorPartido est) {
        super.registrarEstadisticasIndividuales(est);
        if (this.disputado) {
            aplicarSuspensionPorTarjetas();
        }
    }

    public boolean registrarMarcador(String marcador) {
        if (!puedeDisputarse()) {
            return false;
        }
        Equipo eq = competencia.getEquipoInscrito();
        for (int i = 0; i < eq.getNomina().size(); i++) {
            InscripcionEquipo insc = eq.getNomina().get(i);
            if (!alineados.contains(insc.getSocio())) {
                insc.cumplirSuspensionesActivas();
            }
        }
        this.marcadorFinal = marcador;
        this.disputado = true;
        this.cancelado = false;
        this.motivoCancelacion = null;
        aplicarSuspensionPorTarjetas();
        return true;
    }

    public void aplicarSuspensionPorTarjetas() {
        if (competencia == null || competencia.getEquipoInscrito() == null) {
            return;
        }
        Equipo eq = competencia.getEquipoInscrito();
        for (int i = 0; i < estadisticas.size(); i++) {
            EstadisticaJugadorPartido est = estadisticas.get(i);
            if (est instanceof EstadisticaFutbol) {
                EstadisticaFutbol ef = (EstadisticaFutbol) est;
                if (!ef.isTarjetasProcesadas()) {
                    InscripcionEquipo insc = eq.buscarInscripcion(ef.getSocio());
                    if (insc != null) {
                        insc.acumularTarjetasYVerificarSuspension(ef.getTarjetasAmarillas(), ef.getTarjetasRojas());
                        ef.setTarjetasProcesadas(true);
                    }
                }
            }
        }
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public String getInstitucionRival() {
        return institucionRival;
    }

    public String getEscenario() {
        return escenario;
    }

    public String getMarcadorFinal() {
        return marcadorFinal;
    }

    public List<Socio> getAlineados() {
        return alineados;
    }
}
