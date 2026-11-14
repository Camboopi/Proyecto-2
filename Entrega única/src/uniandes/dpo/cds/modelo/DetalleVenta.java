package uniandes.dpo.cds.modelo;

public class DetalleVenta {
    private ArticuloTienda articuloTienda;
    private int cantidad;
    private double precioUnitario;
    private double subtotalRubro;

    public DetalleVenta(int cantidad, double precioUnitario) {
        this(null, cantidad, precioUnitario);
    }

    public DetalleVenta(ArticuloTienda articuloTienda, int cantidad, double precioUnitario) {
        this.articuloTienda = articuloTienda;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotalRubro = cantidad * precioUnitario;
    }

    public double calcularSubtotal() {
        return subtotalRubro;
    }

    public ArticuloTienda getArticuloTienda() {
        return articuloTienda;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
