package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class TorneoInterno {
    protected String nombre;
    protected Deporte deporte;
    protected String categoriaONivel;
    protected String modalidad;
    protected int maxCupos;
    protected int cuposPreferencialesReservados;
    protected boolean cuposPreferencialesLiberados;
    protected String diaTorneo;
    protected String estado;
    protected List<ParticipanteTorneo> participantes;
    protected List<EncuentroTorneo> encuentros;
    protected Map<String, Integer> tablaPosiciones;
    protected ParticipanteTorneo campeon;

    public TorneoInterno(String nombre, Deporte deporte, String categoriaONivel, String modalidad, int maxCupos, String diaTorneo) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.categoriaONivel = categoriaONivel;
        this.modalidad = modalidad;
        this.maxCupos = maxCupos;
        this.cuposPreferencialesReservados = calcularVeintePorcientoPreferencial(maxCupos);
        this.cuposPreferencialesLiberados = false;
        this.diaTorneo = diaTorneo;
        this.estado = "Inscripciones Abiertas";
        this.participantes = new ArrayList<ParticipanteTorneo>();
        this.encuentros = new ArrayList<EncuentroTorneo>();
        this.tablaPosiciones = new LinkedHashMap<String, Integer>();
        this.campeon = null;
    }

    private int calcularVeintePorcientoPreferencial(int totalCupos) {
        if (totalCupos <= 0) {
            return 0;
        }
        int veinte = (int) Math.ceil(totalCupos * 0.20);
        return veinte;
    }

    public int contarCuposPreferencialesOcupados() {
        int cont = 0;
        for (int i = 0; i < participantes.size(); i++) {
            if (participantes.get(i).isEsCupoPreferencial()) {
                cont++;
            }
        }
        return cont;
    }

    public int contarCuposGeneralesOcupados() {
        int cont = 0;
        for (int i = 0; i < participantes.size(); i++) {
            if (!participantes.get(i).isEsCupoPreferencial()) {
                cont++;
            }
        }
        return cont;
    }

    public int getCuposGeneralesMaximos() {
        return maxCupos - cuposPreferencialesReservados;
    }

    /**
     * Si los cupos preferenciales no se llenan antes del inicio del torneo,
     * pueden liberarse para cualquier participante elegible.
     */
    public boolean liberarCuposPreferenciales() {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado)) {
            return false;
        }
        this.cuposPreferencialesLiberados = true;
        return true;
    }

    public ParticipanteTorneo buscarParticipante(String nombrePart) {
        if (nombrePart == null) {
            return null;
        }
        for (int i = 0; i < participantes.size(); i++) {
            if (participantes.get(i).getNombreParticipante().equalsIgnoreCase(nombrePart.trim())) {
                return participantes.get(i);
            }
        }
        return null;
    }

    private boolean puedeTomarCupo(boolean elegiblePreferencial) {
        if (participantes.size() >= maxCupos) {
            return false;
        }
        if (elegiblePreferencial || cuposPreferencialesLiberados) {
            return true;
        }
        return contarCuposGeneralesOcupados() < getCuposGeneralesMaximos();
    }

    private boolean asignarComoCupoPreferencial(boolean elegiblePreferencial) {
        if (elegiblePreferencial && contarCuposPreferencialesOcupados() < cuposPreferencialesReservados) {
            return true;
        }
        return false;
    }

    /**
     * Inscribe un equipo de socios del club en un torneo de deporte de conjunto.
     * Elegible para el 20% de cupos preferenciales.
     */
    public boolean inscribirEquipoClub(Equipo equipo) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || equipo == null) {
            return false;
        }
        if (!(deporte instanceof DeporteConjunto) || !"Equipos".equalsIgnoreCase(modalidad)) {
            return false;
        }
        if (!equipo.getDeporte().getNombre().equalsIgnoreCase(deporte.getNombre())) {
            return false;
        }
        if (!"Libre".equalsIgnoreCase(categoriaONivel) && !equipo.getCategoria().equalsIgnoreCase(categoriaONivel)) {
            return false;
        }
        DeporteConjunto depConj = (DeporteConjunto) deporte;
        if (!depConj.alcanzaMinimoJugadores(equipo.obtenerJugadoresHabilitados().size())) {
            return false;
        }
        if (buscarParticipante(equipo.getNombre()) != null) {
            return false;
        }
        if (!puedeTomarCupo(true)) {
            return false;
        }
        boolean usaPref = asignarComoCupoPreferencial(true);
        double costo = calcularCostoInscripcionPara(false);
        ParticipanteTorneo nuevo = new ParticipanteTorneo(
                equipo.getNombre(), "Equipo Club", equipo.getCategoria(), equipo, null, usaPref, costo);
        registrarIngresoInscripcion(costo);
        participantes.add(nuevo);
        tablaPosiciones.put(nuevo.getNombreParticipante(), 0);
        return true;
    }

    /**
     * Inscribe un equipo externo o de la universidad en un torneo de deporte de conjunto.
     * Solo puede ocupar cupos generales a menos que se hayan liberado los cupos preferenciales.
     */
    public boolean inscribirEquipoExterno(String nombreEquipo, String categoria, int cantidadJugadores) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            return false;
        }
        if (!(deporte instanceof DeporteConjunto) || !"Equipos".equalsIgnoreCase(modalidad)) {
            return false;
        }
        if (!"Libre".equalsIgnoreCase(categoriaONivel) && !categoriaONivel.equalsIgnoreCase(categoria)) {
            return false;
        }
        DeporteConjunto depConj = (DeporteConjunto) deporte;
        if (!depConj.alcanzaMinimoJugadores(cantidadJugadores)) {
            return false;
        }
        if (buscarParticipante(nombreEquipo) != null) {
            return false;
        }
        if (!puedeTomarCupo(false)) {
            return false;
        }
        double costo = calcularCostoInscripcionPara(false);
        ParticipanteTorneo nuevo = new ParticipanteTorneo(
                nombreEquipo.trim(), "Equipo Externo", categoria, null, null, false, costo);
        registrarIngresoInscripcion(costo);
        participantes.add(nuevo);
        tablaPosiciones.put(nuevo.getNombreParticipante(), 0);
        return true;
    }

    /**
     * Verifica si un empleado se encuentra de turno el dia en que se desarrolla el torneo.
     */
    public boolean empleadoEstaDeTurnoEnDiaTorneo(Empleado empleado, List<TurnoSemanal> turnosClub) {
        if (empleado == null || turnosClub == null) {
            return false;
        }
        for (int i = 0; i < turnosClub.size(); i++) {
            TurnoSemanal t = turnosClub.get(i);
            if (t.getDia().equalsIgnoreCase(this.diaTorneo) && t.getEmpleadosAsignados().contains(empleado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inscribe un miembro del club (Socio o Empleado) en un torneo de disciplina individual.
     * - Administrador no puede participar.
     * - Empleado solo puede participar en disciplinas individuales, si NO esta de turno el dia del torneo, y paga $0.
     * - Socio y Empleado tienen derecho a los cupos preferenciales del 20%.
     */
    public boolean inscribirMiembroIndividual(Usuario usuario, String nivelDeclarado, List<TurnoSemanal> turnosClub) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || usuario == null) {
            return false;
        }
        if (!(deporte instanceof DisciplinaIndividual)) {
            return false;
        }
        if (usuario instanceof Administrador || !usuario.puedeReservarUsoPersonal()) {
            return false;
        }
        if (buscarParticipante(usuario.getNombre()) != null) {
            return false;
        }
        boolean esEmpleado = usuario instanceof Empleado;
        String nivelEfectivo = nivelDeclarado;
        if (esEmpleado) {
            if (empleadoEstaDeTurnoEnDiaTorneo((Empleado) usuario, turnosClub)) {
                return false;
            }
            if (nivelEfectivo == null || nivelEfectivo.trim().isEmpty()) {
                nivelEfectivo = categoriaONivel;
            }
        } else if (usuario instanceof Socio) {
            Socio s = (Socio) usuario;
            PerfilAcademiaIndividual perf = s.buscarPerfil(deporte.getNombre());
            if (perf != null) {
                nivelEfectivo = perf.getNivel();
            }
        }
        if (!"Libre".equalsIgnoreCase(categoriaONivel) && (nivelEfectivo == null || !categoriaONivel.equalsIgnoreCase(nivelEfectivo))) {
            return false;
        }
        if (!puedeTomarCupo(true)) {
            return false;
        }
        boolean usaPref = asignarComoCupoPreferencial(true);
        double costo = calcularCostoInscripcionPara(esEmpleado);
        String tipo = esEmpleado ? "Empleado" : "Socio";
        ParticipanteTorneo nuevo = new ParticipanteTorneo(
                usuario.getNombre(), tipo, nivelEfectivo, null, usuario, usaPref, costo);
        registrarIngresoInscripcion(costo);
        participantes.add(nuevo);
        tablaPosiciones.put(nuevo.getNombreParticipante(), 0);
        return true;
    }

    /**
     * Inscribe una pareja (Dobles) en un torneo de disciplina individual.
     */
    public boolean inscribirParejaDobles(String nombrePareja, Usuario jug1, Usuario jug2, String nivelPareja, List<TurnoSemanal> turnosClub) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || !"Dobles".equalsIgnoreCase(modalidad)) {
            return false;
        }
        if (!(deporte instanceof DisciplinaIndividual) || nombrePareja == null || nombrePareja.trim().isEmpty()) {
            return false;
        }
        if ((jug1 instanceof Administrador) || (jug2 instanceof Administrador)) {
            return false;
        }
        if (jug1 instanceof Empleado && empleadoEstaDeTurnoEnDiaTorneo((Empleado) jug1, turnosClub)) {
            return false;
        }
        if (jug2 instanceof Empleado && empleadoEstaDeTurnoEnDiaTorneo((Empleado) jug2, turnosClub)) {
            return false;
        }
        if (!"Libre".equalsIgnoreCase(categoriaONivel) && !categoriaONivel.equalsIgnoreCase(nivelPareja)) {
            return false;
        }
        if (buscarParticipante(nombrePareja) != null) {
            return false;
        }
        boolean elegiblePref = (jug1 instanceof Socio || jug1 instanceof Empleado || jug2 instanceof Socio || jug2 instanceof Empleado);
        if (!puedeTomarCupo(elegiblePref)) {
            return false;
        }
        boolean usaPref = asignarComoCupoPreferencial(elegiblePref);
        boolean esEmp = (jug1 instanceof Empleado) || (jug2 instanceof Empleado);
        double costo = calcularCostoInscripcionPara(esEmp);
        String tipo = esEmp ? "Empleado" : (elegiblePref ? "Socio" : "Externo");
        ParticipanteTorneo nuevo = new ParticipanteTorneo(
                nombrePareja.trim(), tipo, nivelPareja, null, jug1, usaPref, costo);
        nuevo.setSegundoUsuarioPareja(jug2);
        registrarIngresoInscripcion(costo);
        participantes.add(nuevo);
        tablaPosiciones.put(nuevo.getNombreParticipante(), 0);
        return true;
    }

    /**
     * Inscribe un participante externo o estudiante de la universidad en un torneo individual.
     * Solo puede usar cupos generales a menos que los cupos preferenciales hayan sido liberados.
     */
    public boolean inscribirParticipanteExterno(String nombreParticipante, String tipoExterno, String nivelDeclarado) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || nombreParticipante == null || nombreParticipante.trim().isEmpty()) {
            return false;
        }
        if (!(deporte instanceof DisciplinaIndividual)) {
            return false;
        }
        if (!"Libre".equalsIgnoreCase(categoriaONivel) && !categoriaONivel.equalsIgnoreCase(nivelDeclarado)) {
            return false;
        }
        if (buscarParticipante(nombreParticipante) != null) {
            return false;
        }
        if (!puedeTomarCupo(false)) {
            return false;
        }
        double costo = calcularCostoInscripcionPara(false);
        String tipoFinal = (tipoExterno == null || tipoExterno.trim().isEmpty()) ? "Estudiante Universidad" : tipoExterno.trim();
        ParticipanteTorneo nuevo = new ParticipanteTorneo(
                nombreParticipante.trim(), tipoFinal, nivelDeclarado, null, null, false, costo);
        registrarIngresoInscripcion(costo);
        participantes.add(nuevo);
        tablaPosiciones.put(nuevo.getNombreParticipante(), 0);
        return true;
    }

    /**
     * Permite a cualquier participante o equipo retirarse libremente antes de que el torneo inicie,
     * liberando el cupo asignado (preferencial o general) y reversando su pago si aplica.
     */
    public boolean retirarParticipante(String nombreParticipante) {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado)) {
            return false;
        }
        for (int i = 0; i < participantes.size(); i++) {
            ParticipanteTorneo p = participantes.get(i);
            if (p.getNombreParticipante().equalsIgnoreCase(nombreParticipante)) {
                reversarIngresoInscripcion(p.getMontoPagado());
                participantes.remove(i);
                tablaPosiciones.remove(p.getNombreParticipante());
                return true;
            }
        }
        return false;
    }

    public boolean iniciarTorneo() {
        if (!"Inscripciones Abiertas".equalsIgnoreCase(estado) || participantes.size() < 2) {
            return false;
        }
        this.cuposPreferencialesLiberados = true;
        this.estado = "En curso";
        return true;
    }

    public EncuentroTorneo registrarEncuentro(String rondaOFase, String nombrePartA, String nombrePartB,
                                              String marcador, String nombreGanador) {
        if (!"En curso".equalsIgnoreCase(estado)) {
            return null;
        }
        ParticipanteTorneo pA = buscarParticipante(nombrePartA);
        ParticipanteTorneo pB = buscarParticipante(nombrePartB);
        if (pA == null || pB == null || pA == pB) {
            return null;
        }
        ParticipanteTorneo ganador = null;
        if (nombreGanador != null && !nombreGanador.trim().isEmpty() && !"Empate".equalsIgnoreCase(nombreGanador.trim())) {
            ganador = buscarParticipante(nombreGanador);
            if (ganador != pA && ganador != pB) {
                return null;
            }
            int ptsActuales = tablaPosiciones.containsKey(ganador.getNombreParticipante())
                    ? tablaPosiciones.get(ganador.getNombreParticipante()) : 0;
            tablaPosiciones.put(ganador.getNombreParticipante(), ptsActuales + 3);
        } else {
            int ptsA = tablaPosiciones.containsKey(pA.getNombreParticipante()) ? tablaPosiciones.get(pA.getNombreParticipante()) : 0;
            int ptsB = tablaPosiciones.containsKey(pB.getNombreParticipante()) ? tablaPosiciones.get(pB.getNombreParticipante()) : 0;
            tablaPosiciones.put(pA.getNombreParticipante(), ptsA + 1);
            tablaPosiciones.put(pB.getNombreParticipante(), ptsB + 1);
        }
        EncuentroTorneo enc = new EncuentroTorneo(rondaOFase, pA, pB, marcador, ganador);
        encuentros.add(enc);
        return enc;
    }

    public boolean finalizarTorneo(String nombreCampeon) {
        if (!"En curso".equalsIgnoreCase(estado)) {
            return false;
        }
        ParticipanteTorneo gan = buscarParticipante(nombreCampeon);
        if (gan == null) {
            return false;
        }
        this.campeon = gan;
        this.estado = "Finalizado";
        otorgarPremioAlGanador(gan);
        return true;
    }

    protected abstract double calcularCostoInscripcionPara(boolean esEmpleado);

    protected abstract void registrarIngresoInscripcion(double monto);

    protected abstract void reversarIngresoInscripcion(double monto);

    protected abstract void otorgarPremioAlGanador(ParticipanteTorneo ganador);

    public abstract String getTipoTorneo();

    public abstract String obtenerResumenPremio();

    public String consultarHistorialDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TORNEO INTERNO: ").append(nombre).append(" (").append(getTipoTorneo()).append(") ===\n");
        sb.append("Deporte: ").append(deporte.getNombre())
          .append(" | Categoria/Nivel: ").append(categoriaONivel)
          .append(" | Modalidad: ").append(modalidad)
          .append(" | Dia: ").append(diaTorneo).append("\n");
        sb.append("Estado actual: ").append(estado)
          .append(" | Cupos ocupados: ").append(participantes.size()).append("/").append(maxCupos)
          .append(" (Preferenciales 20%: ").append(contarCuposPreferencialesOcupados()).append("/").append(cuposPreferencialesReservados)
          .append(cuposPreferencialesLiberados ? " [Liberados]" : " [Reservados]").append(")\n");

        sb.append("\n-- Participantes / Equipos Inscritos (").append(participantes.size()).append(") --\n");
        for (int i = 0; i < participantes.size(); i++) {
            ParticipanteTorneo p = participantes.get(i);
            sb.append("  ").append(i + 1).append(". ").append(p.getNombreParticipante())
              .append(" [").append(p.getTipoParticipante()).append("]")
              .append(p.isEsCupoPreferencial() ? " (Cupo Preferencial)" : " (Cupo General)")
              .append(" - Pago: $").append(Math.round(p.getMontoPagado())).append("\n");
        }

        if ("En curso".equalsIgnoreCase(estado) || "Finalizado".equalsIgnoreCase(estado)) {
            sb.append("\n-- Encuentros Disputados (").append(encuentros.size()).append(") --\n");
            if (encuentros.isEmpty()) {
                sb.append("  Sin encuentros registrados aun.\n");
            } else {
                for (int i = 0; i < encuentros.size(); i++) {
                    sb.append("  * ").append(encuentros.get(i).obtenerResumenEncuentro()).append("\n");
                }
            }
            sb.append("\n-- Tabla de Clasificacion / Puntos --\n");
            for (Map.Entry<String, Integer> entry : tablaPosiciones.entrySet()) {
                sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" pts\n");
            }
        }
        if ("Finalizado".equalsIgnoreCase(estado) && campeon != null) {
            sb.append("\n-- Resultado Final y Premiacion --\n");
            sb.append("  Campeon: ").append(campeon.getNombreParticipante())
              .append(" (").append(campeon.getTipoParticipante()).append(")\n");
            sb.append("  Premio: ").append(obtenerResumenPremio()).append("\n");
        }
        return sb.toString();
    }

    public String getNombre() {
        return nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public String getCategoriaONivel() {
        return categoriaONivel;
    }

    public String getModalidad() {
        return modalidad;
    }

    public int getMaxCupos() {
        return maxCupos;
    }

    public int getCuposPreferencialesReservados() {
        return cuposPreferencialesReservados;
    }

    public boolean isCuposPreferencialesLiberados() {
        return cuposPreferencialesLiberados;
    }

    public String getDiaTorneo() {
        return diaTorneo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ParticipanteTorneo> getParticipantes() {
        return participantes;
    }

    public List<EncuentroTorneo> getEncuentros() {
        return encuentros;
    }

    public Map<String, Integer> getTablaPosiciones() {
        return tablaPosiciones;
    }

    public ParticipanteTorneo getCampeon() {
        return campeon;
    }
}
