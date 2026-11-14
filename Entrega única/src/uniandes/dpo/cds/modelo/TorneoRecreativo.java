package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public class TorneoRecreativo extends TorneoInterno {
    private double porcentajeBonoDescuento;
    private List<BonoDescuento> bonosEntregados;

    public TorneoRecreativo(String nombre, Deporte deporte, String categoriaONivel, String modalidad,
                            int maxCupos, String diaTorneo, double porcentajeBonoDescuento) {
        super(nombre, deporte, categoriaONivel, modalidad, maxCupos, diaTorneo);
        this.porcentajeBonoDescuento = porcentajeBonoDescuento > 0 ? porcentajeBonoDescuento : 0.20;
        this.bonosEntregados = new ArrayList<BonoDescuento>();
    }

    @Override
    protected double calcularCostoInscripcionPara(boolean esEmpleado) {
        return 0.0;
    }

    @Override
    protected void registrarIngresoInscripcion(double monto) {
        // Sin costo de inscripcion en torneos recreativos
    }

    @Override
    protected void reversarIngresoInscripcion(double monto) {
        // Sin costo de inscripcion en torneos recreativos
    }

    @Override
    protected void otorgarPremioAlGanador(ParticipanteTorneo ganador) {
        if (ganador == null) {
            return;
        }
        String prefijo = "BONO_" + nombre.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (ganador.getUsuarioPrincipal() instanceof Socio) {
            Socio s = (Socio) ganador.getUsuarioPrincipal();
            BonoDescuento b = new BonoDescuento(prefijo + "_" + s.getLogin().toUpperCase(), porcentajeBonoDescuento, nombre);
            s.agregarBonoDescuento(b);
            bonosEntregados.add(b);
        }
        if (ganador.getSegundoUsuarioPareja() instanceof Socio) {
            Socio s2 = (Socio) ganador.getSegundoUsuarioPareja();
            BonoDescuento b2 = new BonoDescuento(prefijo + "_" + s2.getLogin().toUpperCase(), porcentajeBonoDescuento, nombre);
            s2.agregarBonoDescuento(b2);
            bonosEntregados.add(b2);
        }
        if (ganador.getEquipoClub() != null) {
            Equipo eq = ganador.getEquipoClub();
            for (int i = 0; i < eq.getNomina().size(); i++) {
                Socio miembro = eq.getNomina().get(i).getSocio();
                BonoDescuento bm = new BonoDescuento(prefijo + "_" + miembro.getLogin().toUpperCase(), porcentajeBonoDescuento, nombre);
                miembro.agregarBonoDescuento(bm);
                bonosEntregados.add(bm);
            }
        }
        if (bonosEntregados.isEmpty()) {
            BonoDescuento bExt = new BonoDescuento(prefijo + "_GANADOR", porcentajeBonoDescuento, nombre);
            bonosEntregados.add(bExt);
        }
    }

    @Override
    public String getTipoTorneo() {
        return "Recreativo";
    }

    @Override
    public String obtenerResumenPremio() {
        int pct = (int) Math.round(porcentajeBonoDescuento * 100);
        return "Bono de descuento del " + pct + "% en Tienda o Cafeteria (no acumulable con otros descuentos). Bonos emitidos: " + bonosEntregados.size();
    }

    public double getPorcentajeBonoDescuento() {
        return porcentajeBonoDescuento;
    }

    public List<BonoDescuento> getBonosEntregados() {
        return bonosEntregados;
    }
}
