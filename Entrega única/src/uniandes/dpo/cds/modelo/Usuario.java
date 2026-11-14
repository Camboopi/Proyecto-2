package uniandes.dpo.cds.modelo;

public abstract class Usuario {
    protected String login;
    protected String password;
    protected String nombre;
    protected int puntosFidelidad;

    public Usuario(String login, String password, String nombre) {
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.puntosFidelidad = 0;
    }

    public boolean autenticar(String pass) {
        if (this.password != null && this.password.equals(pass)) {
            return true;
        }
        return false;
    }

    public abstract boolean puedeReservarUsoPersonal();

    public boolean puedeUsarPuntosFidelidad() {
        return true;
    }

    public void acumularPuntos2Porciento(double valorCompra) {
        if (puedeUsarPuntosFidelidad() && valorCompra > 0) {
            int puntos = (int) (valorCompra * 0.02);
            this.puntosFidelidad = this.puntosFidelidad + puntos;
        }
    }

    public boolean redimirPuntos(int pts) {
        if (!puedeUsarPuntosFidelidad() || pts <= 0 || pts > this.puntosFidelidad) {
            return false;
        }
        this.puntosFidelidad = this.puntosFidelidad - pts;
        return true;
    }

    public int getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(int puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }
}
