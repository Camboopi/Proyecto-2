package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Competencia {
    protected String nombre;
    protected String entidadOrganizadora;
    protected String anioTemporada;
    protected String categoriaHabilitada;
    protected int nominaMinimaHabilitados;
    protected DeporteConjunto deporte;
    protected Equipo equipoInscrito;
    protected List<Socio> nominaOficial;
    protected List<PartidoCompetencia> programacion;

    public Competencia(String nombre, String entidadOrganizadora, String anioTemporada, int nominaMinimaHabilitados) {
        this(nombre, entidadOrganizadora, anioTemporada, nominaMinimaHabilitados, null);
    }

    public Competencia(String nombre, String entidadOrganizadora, String anioTemporada, int nominaMinimaHabilitados, DeporteConjunto deporte) {
        this.nombre = nombre;
        this.entidadOrganizadora = entidadOrganizadora;
        this.anioTemporada = anioTemporada;
        this.categoriaHabilitada = "Mayores";
        this.nominaMinimaHabilitados = nominaMinimaHabilitados;
        this.deporte = deporte;
        this.equipoInscrito = null;
        this.nominaOficial = new ArrayList<Socio>();
        this.programacion = new ArrayList<PartidoCompetencia>();
    }

    public boolean cumpleRequisitosInscripcion(Equipo eq) {
        if (eq == null) {
            return false;
        }
        if (!categoriaHabilitada.equalsIgnoreCase(eq.getCategoria())) {
            return false;
        }
        if (this.deporte != null && !this.deporte.getNombre().equalsIgnoreCase(eq.getDeporte().getNombre())) {
            return false;
        }
        return eq.obtenerJugadoresHabilitados().size() >= nominaMinimaHabilitados;
    }

    /**
     * Registra un unico equipo representante del club (no una lista de equipos) y su nomina oficial.
     */
    public boolean registrarNominaOficial(Equipo eq, List<Socio> jugadores) {
        if (!cumpleRequisitosInscripcion(eq) || jugadores == null || jugadores.size() < nominaMinimaHabilitados) {
            return false;
        }
        List<Socio> habilitadosEquipo = eq.obtenerJugadoresHabilitados();
        for (int i = 0; i < jugadores.size(); i++) {
            if (!habilitadosEquipo.contains(jugadores.get(i))) {
                return false;
            }
        }
        this.equipoInscrito = eq;
        if (this.deporte == null) {
            this.deporte = eq.getDeporte();
        }
        this.nominaOficial = new ArrayList<Socio>(jugadores);
        return true;
    }

    public void agregarPartidoCompetencia(PartidoCompetencia p) {
        this.programacion.add(p);
    }

    public Socio obtenerGoleadorODestacado() {
        Map<Socio, Integer> acumulado = new HashMap<Socio, Integer>();
        Socio mejor = null;
        int maxVal = -1;
        for (int i = 0; i < programacion.size(); i++) {
            PartidoCompetencia p = programacion.get(i);
            for (int j = 0; j < p.getEstadisticas().size(); j++) {
                EstadisticaJugadorPartido est = p.getEstadisticas().get(j);
                Socio s = est.getSocio();
                int anterior = 0;
                if (acumulado.containsKey(s)) {
                    anterior = acumulado.get(s);
                }
                int total = anterior + est.obtenerValorDestacado();
                acumulado.put(s, total);
                if (total > maxVal) {
                    maxVal = total;
                    mejor = s;
                }
            }
        }
        return mejor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEntidadOrganizadora() {
        return entidadOrganizadora;
    }

    public String getAnioTemporada() {
        return anioTemporada;
    }

    public String getCategoriaHabilitada() {
        return categoriaHabilitada;
    }

    public int getNominaMinimaHabilitados() {
        return nominaMinimaHabilitados;
    }

    public DeporteConjunto getDeporte() {
        return deporte;
    }

    public Equipo getEquipoInscrito() {
        return equipoInscrito;
    }

    public List<Socio> getNominaOficial() {
        return nominaOficial;
    }

    public List<PartidoCompetencia> getProgramacion() {
        return programacion;
    }
}
