package uniandes.dpo.cds.modelo;

public class PartidoInterno extends Partido {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private String marcadorFinal;

    public PartidoInterno(String fecha, Equipo equipoLocal, Equipo equipoVisitante) {
        super(fecha);
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }

    public boolean validarMismaCategoriaYDeporte() {
        if (equipoLocal == null || equipoVisitante == null) {
            return false;
        }
        if (equipoLocal == equipoVisitante) {
            return false;
        }
        boolean mismoDeporte = equipoLocal.getDeporte().getNombre().equalsIgnoreCase(equipoVisitante.getDeporte().getNombre());
        boolean mismaCategoria = equipoLocal.getCategoria().equalsIgnoreCase(equipoVisitante.getCategoria());
        return mismoDeporte && mismaCategoria;
    }

    public boolean validarMinimoJugadoresHabilitados() {
        if (equipoLocal == null || equipoVisitante == null) {
            return false;
        }
        DeporteConjunto dep = equipoLocal.getDeporte();
        int habLocal = equipoLocal.obtenerJugadoresHabilitados().size();
        int habVis = equipoVisitante.obtenerJugadoresHabilitados().size();
        return dep.alcanzaMinimoJugadores(habLocal) && dep.alcanzaMinimoJugadores(habVis);
    }

    public boolean verificarSinSuspendidosEnEstadisticas() {
        for (int i = 0; i < estadisticas.size(); i++) {
            Socio s = estadisticas.get(i).getSocio();
            InscripcionEquipo insc = equipoLocal != null ? equipoLocal.buscarInscripcion(s) : null;
            if (insc == null && equipoVisitante != null) {
                insc = equipoVisitante.buscarInscripcion(s);
            }
            if (insc != null && insc.tieneSuspensionActiva()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean puedeDisputarse() {
        if (!validarMismaCategoriaYDeporte()) {
            cancelarPartido("Cancelado: los equipos no pertenecen al mismo deporte o categoría");
            return false;
        }
        if (!validarMinimoJugadoresHabilitados() || !verificarSinSuspendidosEnEstadisticas()) {
            cancelarPartido("Cancelado: jugadores habilitados insuficientes por suspensión / acumulación de tarjetas amarillas");
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

    public boolean registrarResultado(int ptsLocal, int ptsVisitante) {
        if (!puedeDisputarse()) {
            return false;
        }
        DeporteConjunto dep = equipoLocal.getDeporte();
        if (!dep.isPermiteEmpates() && ptsLocal == ptsVisitante) {
            return false;
        }
        for (int i = 0; i < equipoLocal.getNomina().size(); i++) {
            equipoLocal.getNomina().get(i).cumplirSuspensionesActivas();
        }
        for (int i = 0; i < equipoVisitante.getNomina().size(); i++) {
            equipoVisitante.getNomina().get(i).cumplirSuspensionesActivas();
        }
        this.marcadorFinal = ptsLocal + " - " + ptsVisitante;
        this.disputado = true;
        this.cancelado = false;
        this.motivoCancelacion = null;
        aplicarSuspensionPorTarjetas();
        return true;
    }

    public void aplicarSuspensionPorTarjetas() {
        for (int i = 0; i < estadisticas.size(); i++) {
            EstadisticaJugadorPartido est = estadisticas.get(i);
            if (est instanceof EstadisticaFutbol) {
                EstadisticaFutbol ef = (EstadisticaFutbol) est;
                if (!ef.isTarjetasProcesadas()) {
                    InscripcionEquipo insc = equipoLocal != null ? equipoLocal.buscarInscripcion(ef.getSocio()) : null;
                    if (insc == null && equipoVisitante != null) {
                        insc = equipoVisitante.buscarInscripcion(ef.getSocio());
                    }
                    if (insc != null) {
                        insc.acumularTarjetasYVerificarSuspension(ef.getTarjetasAmarillas(), ef.getTarjetasRojas());
                        ef.setTarjetasProcesadas(true);
                    }
                }
            }
        }
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public String getMarcadorFinal() {
        return marcadorFinal;
    }
}
