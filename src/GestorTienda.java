package intrashop.controller;

import intrashop.model.Empleado;
import intrashop.model.Producto;
import intrashop.model.DetalleVenta;
import java.io.*;
import java.util.*;

public class GestorTienda {
    private Map<String, Empleado> empleados = new HashMap<>();
    private Map<String, Producto> productos = new HashMap<>();

    public void cargarEmpleados(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 2) {
                    empleados.put(datos[0].trim(), new Empleado(datos[0].trim(), datos[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
        }
    }

    public void cargarProductos(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    productos.put(datos[0].trim(), new Producto(datos[0].trim(), datos[1].trim(), Double.parseDouble(datos[2].trim())));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }
    }

    public Empleado buscarEmpleado(String cedula) { return empleados.get(cedula); }
    public Producto buscarProducto(String codigo) { return productos.get(codigo); }
    public Collection<Producto> getListaProductos() { return productos.values(); }

    public void generarFacturaCSV(Empleado empleado, List<DetalleVenta> carrito, double total) {
        String nombreArchivo = "factura_" + empleado.getCedula() + "_" + System.currentTimeMillis() + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("INTRASHOP - CAPA 8 HG SAS");
            writer.println("Empleado: " + empleado.getNombre() + " (Cedula: " + empleado.getCedula() + ")");
            writer.println("Codigo,Producto,Cantidad,Precio Unitario,Subtotal");
            for (DetalleVenta dv : carrito) {
                writer.println(dv.getProducto().getCodigo() + "," + dv.getProducto().getNombre() + "," + dv.getCantidad() + "," + dv.getProducto().getPrecio() + "," + dv.getSubtotal());
            }
            writer.println("TOTAL A PAGAR," + total);
            System.out.println("\n¡Factura generada con éxito! Archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al generar factura: " + e.getMessage());
        }
    }
}