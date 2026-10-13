package intrashop.model;

public class Empleado {
    private String cedula;
    private String nombre;

    public Empleado(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
}