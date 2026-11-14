package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public class InscripcionEquipo {
    private Socio socio;
    private Equipo equipo;
    private String posicion;
    private int numeroCamisetaUnico;
    private int tarjetasAmarillasAcumuladas;
    private List<SancionDisciplinaria> sanciones;

    public InscripcionEquipo(Socio socio, Equipo equipo, String posicion, int numeroCamisetaUnico) {
        this.socio = socio;
        this.equipo = equipo;
        this.posicion = posicion;
        this.numeroCamisetaUnico = numeroCamisetaUnico;
        this.tarjetasAmarillasAcumuladas = 0;
        this.sanciones = new ArrayList<SancionDisciplinaria>();
    }

    public Socio getSocio() {
        return socio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public String getPosicion() {
        return posicion;
    }

    public int getNumeroCamisetaUnico() {
        return numeroCamisetaUnico;
    }

    public int getTarjetasAmarillasAcumuladas() {
        return tarjetasAmarillasAcumuladas;
    }

    public List<SancionDisciplinaria> getSanciones() {
        return sanciones;
    }

    public void agregarSancion(SancionDisciplinaria s) {
        this.sanciones.add(s);
    }

    /**
     * Registra las tarjetas recibidas en un partido de futbol, acumulando las tarjetas amarillas
     * a lo largo de los partidos y generando automaticamente la suspension para el siguiente partido
     * cuando acumula 2 amarillas (en el mismo partido o en partidos distintos) o recibe 1 roja.
     */
    public void acumularTarjetasYVerificarSuspension(int amarillas, int rojas) {
        if (amarillas > 0) {
            this.tarjetasAmarillasAcumuladas += amarillas;
        }
        if (rojas >= 1) {
            this.sanciones.add(new SancionDisciplinaria("1 Roja"));
        }
        while (this.tarjetasAmarillasAcumuladas >= 2) {
            this.tarjetasAmarillasAcumuladas -= 2;
            this.sanciones.add(new SancionDisciplinaria("Acumulación de 2 Amarillas"));
        }
    }

    public boolean tieneSuspensionActiva() {
        for (int i = 0; i < sanciones.size(); i++) {
            if (sanciones.get(i).isSuspendidoSiguientePartido()) {
                return true;
            }
        }
        return false;
    }

    public void cumplirSuspensionesActivas() {
        for (int i = 0; i < sanciones.size(); i++) {
            if (sanciones.get(i).isSuspendidoSiguientePartido()) {
                sanciones.get(i).cumplirSuspensionSiguientePartido();
            }
        }
    }
}
