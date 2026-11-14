package uniandes.dpo.cds.modelo;

public class Administrador extends Usuario {

    public Administrador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    @Override
    public boolean puedeReservarUsoPersonal() {
        return false;
    }

    @Override
    public boolean puedeUsarPuntosFidelidad() {
        return false;
    }

    public boolean aprobarCambioTurno(SolicitudCambioTurno sol) {
        if (sol == null) {
            return false;
        }
        return sol.aprobarSiCumpleQuorum();
    }

    public void aprobarSugerenciaEmpleado(SugerenciaProducto sug) {
        if (sug != null) {
            sug.aprobarPorAdministrador();
        }
    }

    public boolean moverArticulosUbicacionInventario(UbicacionInventario orig, UbicacionInventario dest, ArticuloTienda art, int cant) {
        if (orig == null || dest == null || art == null) {
            return false;
        }
        return orig.moverArticulosA(dest, art, cant);
    }

    public void reabastecerTienda(UbicacionInventario ubic, ArticuloTienda art, int cant) {
        if (ubic != null && art != null && cant > 0) {
            ubic.reabastecerArticulo(art, cant);
        }
    }
}
