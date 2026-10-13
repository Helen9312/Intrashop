package intrashop.main;

import intrashop.controller.GestorTienda;
import intrashop.model.*;
import java.util.*;

/**
 * Clase principal que ejecuta el menú interactivo de la consola para IntraShop.
 * Maneja el flujo de la aplicación, identificación de empleados y procesamiento de pedidos.
 */
public class Main {
    public static void main(String[] args) {
        GestorTienda gestor = new GestorTienda();
        
        // Carga inicial de los archivos CSV externos
        gestor.cargarEmpleados("empleados.csv");
        gestor.cargarProductos("productos.csv");

        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("  SISTEMA INTRASHOP - CAPA 8 HG SAS  ");
        System.out.println("==========================================");

        // Identificación del empleado por cédula
        Empleado empleado = null;
        while (empleado == null) {
            System.out.print("Ingrese número de cédula del empleado: ");
            String cedula = scanner.nextLine().trim();
            empleado = gestor.buscarEmpleado(cedula);
            if (empleado == null) {
                System.out.println("Empleado no encontrado. Intente de nuevo.\n");
            }
        }

        System.out.println("\n¡Bienvenido(a), " + empleado.getNombre() + "!");

        List<DetalleVenta> carrito = new ArrayList<>();
        boolean salir = false;
        
        // Menú interactivo de opciones
        while (!salir) {
            System.out.println("\n--- MENÚ DE OPCIONES ---");
            System.out.println("1. Consultar catálogo/precios");
            System.out.println("2. Agregar producto al pedido");
            System.out.println("3. Consultar pedido actual");
            System.out.println("4. Calcular total y finalizar compra");
            System.out.println("5. Cancelar y salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.println("\n--- CATÁLOGO DE PRODUCTOS ---");
                    Collection<Producto> productos = gestor.getListaProductos();
                    if (productos.isEmpty()) {
                        System.out.println("No hay productos cargados en el sistema.");
                    } else {
                        for (Producto p : productos) {
                            System.out.println(p);
                        }
                    }
                    break;

                case "2":
                    System.out.print("Ingrese el código del producto (ej: P1): ");
                    String codigo = scanner.nextLine().trim();
                    Producto producto = gestor.buscarProducto(codigo);

                    if (producto == null) {
                        System.out.println("Código de producto inválido o no existe.");
                        break;
                    }

                    System.out.println("Producto seleccionado: " + producto.getNombre() + " ($" + producto.getPrecio() + ")");
                    System.out.print("Ingrese la cantidad a comprar: ");
                    try {
                        int cantidad = Integer.parseInt(scanner.nextLine().trim());
                        if (cantidad <= 0) {
                            System.out.println("La cantidad debe ser mayor a 0.");
                        } else {
                            carrito.add(new DetalleVenta(producto, cantidad));
                            System.out.println("¡Producto agregado correctamente al pedido!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Cantidad inválida. Debe ingresar un número entero.");
                    }
                    break;

                case "3":
                    if (carrito.isEmpty()) {
                        System.out.println("\nEl carrito de compras está vacío.");
                    } else {
                        mostrarResumenPedido(empleado, carrito);
                    }
                    break;

                case "4":
                    if (carrito.isEmpty()) {
                        System.out.println("\nNo se puede finalizar la compra sin productos en el carrito.");
                    } else {
                        double total = mostrarResumenPedido(empleado, carrito);
                        gestor.generarFacturaCSV(empleado, carrito, total);
                        salir = true;
                    }
                    break;

                case "5":
                    System.out.println("Operación cancelada. Saliendo del sistema...");
                    salir = true;
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione un número entre 1 y 5.");
            }
        }
        scanner.close();
    }

    /**
     * Muestra el resumen de los productos en el carrito y calcula el total a pagar.
     */
    private static double mostrarResumenPedido(Empleado empleado, List<DetalleVenta> carrito) {
        System.out.println("\n==========================================");
        System.out.println("          RESUMEN DE LA COMPRA            ");
        System.out.println("==========================================");
        System.out.println("Empleado: " + empleado.getNombre());
        System.out.println("Cédula:   " + empleado.getCedula());
        System.out.println("------------------------------------------");
        System.out.printf("%-8s %-22s %-6s %-10s%n", "Código", "Producto", "Cant.", "Subtotal");
        System.out.println("------------------------------------------");

        double total = 0;
        for (DetalleVenta dv : carrito) {
            System.out.printf("%-8s %-22s %-6d $%-10.2f%n",
                    dv.getProducto().getCodigo(),
                    dv.getProducto().getNombre(),
                    dv.getCantidad(),
                    dv.getSubtotal());
            total += dv.getSubtotal();
        }

        System.out.println("------------------------------------------");
        System.out.printf("TOTAL A PAGAR: $%.2f%n", total);
        System.out.println("==========================================");
        return total;
    }
}