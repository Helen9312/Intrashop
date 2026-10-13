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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchivo), "UTF-8"))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    if (linea.toLowerCase().contains("cedula") || linea.toLowerCase().contains("nombre")) {
                        continue;
                    }
                }
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchivo), "UTF-8"))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    if (linea.toLowerCase().contains("codigo") || linea.toLowerCase().contains("precio")) {
                        continue; 
                    }
                }
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    String codigo = datos[0].trim().toUpperCase();
                    String nombre = datos[1].trim();
                    double precio = Double.parseDouble(datos[2].trim());
                    productos.put(codigo, new Producto(codigo, nombre, precio));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }
    }

    public Empleado buscarEmpleado(String cedula) { 
        if (cedula == null) return null;
        return empleados.get(cedula.trim()); 
    }
    
    public Producto buscarProducto(String codigo) { 
        if (codigo == null) return null;
        return productos.get(codigo.trim().toUpperCase()); 
    }
    
    public Collection<Producto> getListaProductos() { 
        return productos.values(); 
    }

    public void generarFacturaCSV(Empleado empleado, List<DetalleVenta> carrito, double total) {
        String nombreArchivo = "factura_" + empleado.getCedula() + "_" + System.currentTimeMillis() + ".csv";
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(nombreArchivo), "UTF-8"))) {
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