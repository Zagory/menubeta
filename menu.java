import java.util.Scanner; // Importa la clase Scanner para leer la entrada del usuario
import java.util.ArrayList; // Importa la clase ArrayList para manejar listas dinámicas
import java.time.LocalDate; // Importa la clase LocalDate para manejar fechas

public class menu {
    // Definir el inventario inicial
    static String[] productos = {"Manzanas", "Naranjas", "Plátanos"}; // Array de productos
    static int[] cantidades = {10, 20, 15}; // Array de cantidades de cada producto
    static double[] precios = {1.0, 0.5, 0.75}; // Array de precios de cada producto
    static String[] categorias = {"Frutas", "Frutas", "Frutas"}; // Array de categorías de cada producto
    static String[] codigos = {"001", "002", "003"}; // Array de códigos de cada producto
    static final double IVA = 19.0; // IVA estándar en Colombia
    static ArrayList<String> historial = new ArrayList<>(); // Lista para almacenar el historial de productos eliminados
    static ArrayList<Descuento> descuentos = new ArrayList<>(); // Lista para almacenar los descuentos

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Crear un objeto Scanner para leer la entrada del usuario
        boolean continuar = true; // Variable para controlar el bucle del menú

        while (continuar) {
            // Mostrar el menú principal
            System.out.println("===================================");
            System.out.println("          MENÚ PRINCIPAL          ");
            System.out.println("===================================");
            System.out.println("1. Ver inventario");
            System.out.println("2. Agregar producto");
            System.out.println("3. Quitar producto");
            System.out.println("4. Realizar venta");
            System.out.println("5. Ver historial de productos eliminados");
            System.out.println("6. Ver lista de descuentos");
            System.out.println("7. Salir");
            System.out.println("===================================");
            System.out.print("Elige una opción: ");
            int opcion = leerOpcion(scanner); // Leer la opción del usuario

            switch (opcion) {
                case 1:
                    mostrarInventario(scanner); // Llamar al método para mostrar el inventario
                    break;
                case 2:
                    agregarProducto(scanner); // Llamar al método para agregar un producto
                    break;
                case 3:
                    quitarProducto(scanner); // Llamar al método para quitar un producto
                    break;
                case 4:
                    realizarVenta(scanner); // Llamar al método para realizar una venta
                    break;
                case 5:
                    verHistorial(scanner); // Llamar al método para ver el historial de productos eliminados
                    break;
                case 6:
                    gestionarDescuentos(scanner); // Llamar al método para ver y gestionar la lista de descuentos
                    break;
                case 7:
                    continuar = false; // Salir del bucle y terminar el programa
                    System.out.println("¡Gracias por usar el sistema de inventario!");
                    break;
                default:
                    System.out.println("Opción no válida."); // Mensaje de error para opción no válida
            }
        }
        scanner.close(); // Cerrar el objeto Scanner
    }

    // Método para leer la opción del usuario y manejar errores de entrada
    public static int leerOpcion(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
            }
        }
    }
    // Mostrar el inventario actual
    public static void mostrarInventario(Scanner scanner) {
        System.out.println("===================================");
        System.out.println("            INVENTARIO             ");
        System.out.println("===================================");
        System.out.println("Categorías disponibles (para retroceder oprime (-1) ):");
        ArrayList<String> categoriasUnicas = new ArrayList<>();
        for (String categoria : categorias) {
            if (!categoriasUnicas.contains(categoria)) {
                categoriasUnicas.add(categoria);
                System.out.println("- " + categoria);
            }
        }
        System.out.print("Elige una categoría: ");
        String categoriaElegida = scanner.nextLine();
        System.out.println("===================================");
        System.out.println("Productos en la categoría " + categoriaElegida + ":");
        for (int i = 0; i < productos.length; i++) {
            if (categorias[i].equalsIgnoreCase(categoriaElegida)) {
                System.out.println("Código: " + codigos[i] + " | Producto: " + productos[i] + " | Cantidad: " + cantidades[i] + " | Precio: $" + precios[i]);
            }
        }
        System.out.println("===================================");
    }

    // Agregar un nuevo producto al inventario
    public static void agregarProducto(Scanner scanner) {
        System.out.println("===================================");
        System.out.println("         AGREGAR PRODUCTO          ");
        System.out.println("===================================");
        System.out.print("Código del producto: ");
        String codigo = scanner.nextLine(); // Leer el código del nuevo producto
        int indice = buscarProductoPorCodigo(codigo);

        if (indice != -1) {
            System.out.print("Cantidad a agregar: ");
            int cantidad = scanner.nextInt(); // Leer la cantidad del nuevo producto
            cantidades[indice] += cantidad; // Agregar la cantidad al stock existente
            System.out.println("Cantidad actualizada.");
        } else {
            System.out.print("Nombre del producto: ");
            String nuevoProducto = scanner.nextLine(); // Leer el nombre del nuevo producto
            System.out.print("Categoría: ");
            String categoria = scanner.nextLine(); // Leer la categoría del nuevo producto
            System.out.print("Cantidad: ");
            int nuevaCantidad = scanner.nextInt(); // Leer la cantidad del nuevo producto
            System.out.print("Precio: ");
            String precioStr = scanner.next().replace(",", "."); // Permitir tanto "," como "."
            double nuevoPrecio = Double.parseDouble(precioStr); // Convertir el precio a double

            // Agregar el nuevo producto al inventario
            productos = agregarElemento(productos, nuevoProducto);
            categorias = agregarElemento(categorias, categoria);
            cantidades = agregarElemento(cantidades, nuevaCantidad);
            precios = agregarElemento(precios, nuevoPrecio);
            codigos = agregarElemento(codigos, codigo);

            System.out.println("Producto agregado.");
        }
        System.out.println("===================================");
    }

    // Quitar un producto del inventario
    public static void quitarProducto(Scanner scanner) {
        System.out.println("===================================");
        System.out.println("         QUITAR PRODUCTO           ");
        System.out.println("===================================");
        System.out.print("Código del producto a quitar (para retroceder oprime (-1)): ");
        String codigo = scanner.nextLine(); // Leer el código del producto a quitar
        int indice = buscarProductoPorCodigo(codigo); // Buscar el índice del producto

        if (indice == -1) {
            System.out.println("Producto no encontrado."); // Mensaje de error si el producto no se encuentra
            System.out.println("===================================");
            return;
        }

        // Agregar el producto al historial antes de eliminarlo
        historial.add("Código: " + codigos[indice] + " | Producto: " + productos[indice] + " | Categoría: " + categorias[indice] + " | Cantidad: " + cantidades[indice] + " | Precio: $" + precios[indice]);

        // Quitar el producto del inventario
        productos = quitarElemento(productos, indice);
        categorias = quitarElemento(categorias, indice);
        cantidades = quitarElemento(cantidades, indice);
        precios = quitarElemento(precios, indice);
        codigos = quitarElemento(codigos, indice);

        System.out.println("Producto quitado.");
        System.out.println("===================================");
    }
    // Realizar una venta
    public static void realizarVenta(Scanner scanner) {
        double total = 0.0; // Variable para almacenar el total de la venta
        int cantidadTotal = 0; // Variable para almacenar la cantidad total de productos vendidos
        boolean continuarVenta = true; // Variable para controlar el bucle de la venta
        ArrayList<String> ticket = new ArrayList<>(); // Lista para almacenar los detalles del ticket de venta

        System.out.println("===================================");
        System.out.println("           REALIZAR VENTA          ");
        System.out.println("===================================");

        while (continuarVenta) {
            System.out.print("Código del producto a vender (para retroceder oprime (-1) : ");
            String codigo = scanner.nextLine(); // Leer el código del producto a vender
            int indice = buscarProductoPorCodigo(codigo); // Buscar el índice del producto

            if (indice == -1) {
                System.out.println("Producto no encontrado."); // Mensaje de error si el producto no se encuentra
                System.out.println("===================================");
                return;
            }

            System.out.print("Cantidad a vender: ");
            int cantidadAVender = scanner.nextInt(); // Leer la cantidad a vender
            scanner.nextLine(); // Consumir la nueva línea

            if (cantidades[indice] < cantidadAVender) {
                System.out.println("Stock insuficiente. Cantidad disponible: " + cantidades[indice]); // Mensaje de error si no hay suficiente stock
                System.out.println("===================================");
                return;
            }

            cantidades[indice] -= cantidadAVender; // Restar la cantidad vendida del stock
            double subtotal = cantidadAVender * precios[indice]; // Calcular el subtotal de la venta
            total += subtotal; // Sumar el subtotal al total de la venta
            cantidadTotal += cantidadAVender; // Sumar la cantidad vendida al total de productos vendidos

            // Agregar los detalles del producto al ticket de venta
            ticket.add("Producto: " + productos[indice] + " | Cantidad: " + cantidadAVender + " | Subtotal: $" + subtotal);

            System.out.print("¿Desea agregar otro producto a la venta? (si/no): ");
            continuarVenta = scanner.nextLine().equalsIgnoreCase("si"); // Preguntar si se desea agregar otro producto a la venta
        }

        // Aplicar descuentos automáticos según la cantidad de productos
        double descuento = calcularDescuento(cantidadTotal);

        total = total - (total * descuento / 100) + (total * IVA / 100); // Calcular el total con descuento e IVA

        // Mostrar el ticket de venta
        System.out.println("===================================");
        System.out.println("           TICKET DE VENTA         ");
        System.out.println("===================================");
        System.out.println("Nombre de la tienda: Mi Tienda");
        System.out.println("Fecha: " + LocalDate.now());
        for (String detalle : ticket) {
            System.out.println(detalle);
        }
        System.out.println("Descuento aplicado: " + descuento + "%");
        System.out.println("IVA: " + IVA + "%");
        System.out.println("Total a pagar: $" + total);
        System.out.println("===================================");
    }

    // Ver historial de productos eliminados
    public static void verHistorial(Scanner scanner) {
        System.out.println("===================================");
        System.out.println("     HISTORIAL DE PRODUCTOS        ");
        System.out.println("===================================");
        for (String item : historial) {
            System.out.println(item);
        }
        System.out.println("===================================");
        System.out.print("¿Desea restaurar algún producto? (si/no): ");
        if (scanner.nextLine().equalsIgnoreCase("si")) {
            System.out.print("Ingrese el código del producto a restaurar: ");
            String codigo = scanner.nextLine();
            for (String item : historial) {
                if (item.contains("Código: " + codigo)) {
                    String[] partes = item.split(" \\| ");
                    productos = agregarElemento(productos, partes[1].split(": ")[1]);
                    categorias = agregarElemento(categorias, partes[2].split(": ")[1]);
                    cantidades = agregarElemento(cantidades, Integer.parseInt(partes[3].split(": ")[1]));
                    precios = agregarElemento(precios, Double.parseDouble(partes[4].split(": ")[1].substring(1)));
                    codigos = agregarElemento(codigos, codigo);
                    historial.remove(item);
                    System.out.println("Producto restaurado.");
                    break;
                }
            }
        }
        System.out.println("===================================");
    }
    // Buscar el índice de un producto en el inventario por código
    public static int buscarProductoPorCodigo(String codigo) {
        for (int i = 0; i < codigos.length; i++) {
            if (codigos[i].equalsIgnoreCase(codigo)) {
                return i; // Devolver el índice si el producto se encuentra
            }
        }
        return -1; // Devolver -1 si el producto no se encuentra
    }

    // Calcular el descuento según la cantidad de productos
    public static double calcularDescuento(int cantidadTotal) {
        double descuento = 0.0;
        for (Descuento descuentoObj : descuentos) {
            if (cantidadTotal >= descuentoObj.getCantidadMinima()) {
                descuento = descuentoObj.getPorcentajeDescuento();
            }
        }
        return descuento;
    }

    // Agregar un elemento a un array
    public static String[] agregarElemento(String[] array, String elemento) {
        String[] nuevoArray = new String[array.length + 1]; // Crear un nuevo array con un tamaño mayor
        System.arraycopy(array, 0, nuevoArray, 0, array.length); // Copiar los elementos del array original al nuevo array
        nuevoArray[array.length] = elemento; // Agregar el nuevo elemento al final del nuevo array
        return nuevoArray; // Devolver el nuevo array
    }

    public static int[] agregarElemento(int[] array, int elemento) {
        int[] nuevoArray = new int[array.length + 1]; // Crear un nuevo array con un tamaño mayor
        System.arraycopy(array, 0, nuevoArray, 0, array.length); // Copiar los elementos del array original al nuevo array
        nuevoArray[array.length] = elemento; // Agregar el nuevo elemento al final del nuevo array
        return nuevoArray; // Devolver el nuevo array
    }

    public static double[] agregarElemento(double[] array, double elemento) {
        double[] nuevoArray = new double[array.length + 1]; // Crear un nuevo array con un tamaño mayor
        System.arraycopy(array, 0, nuevoArray, 0, array.length); // Copiar los elementos del array original al nuevo array
        nuevoArray[array.length] = elemento; // Agregar el nuevo elemento al final del nuevo array
        return nuevoArray; // Devolver el nuevo array
    }
    // Quitar un elemento de un array
    public static String[] quitarElemento(String[] array, int indice) {
        String[] nuevoArray = new String[array.length - 1]; // Crear un nuevo array con un tamaño menor
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != indice) {
                nuevoArray[j++] = array[i]; // Copiar los elementos del array original al nuevo array, omitiendo el elemento a quitar
            }
        }
        return nuevoArray; // Devolver el nuevo array
    }

    public static int[] quitarElemento(int[] array, int indice) {
        int[] nuevoArray = new int[array.length - 1]; // Crear un nuevo array con un tamaño menor
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != indice) {
                nuevoArray[j++] = array[i]; // Copiar los elementos del array original al nuevo array, omitiendo el elemento a quitar
            }
        }
        return nuevoArray; // Devolver el nuevo array
    }

    public static double[] quitarElemento(double[] array, int indice) {
        double[] nuevoArray = new double[array.length - 1]; // Crear un nuevo array con un tamaño menor
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != indice) {
                nuevoArray[j++] = array[i]; // Copiar los elementos del array original al nuevo array, omitiendo el elemento a quitar
            }
        }
        return nuevoArray; // Devolver el nuevo array
    }

    // Ver la lista de descuentos y gestionar descuentos
    public static void gestionarDescuentos(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("===================================");
            System.out.println("         LISTA DE DESCUENTOS       ");
            System.out.println("===================================");
            for (int i = 0; i < descuentos.size(); i++) {
                System.out.println((i + 1) + ". " + descuentos.get(i).getCantidadMinima() + " productos: " + descuentos.get(i).getPorcentajeDescuento() + "% de descuento");
            }
            System.out.println("===================================");
            System.out.println("1. Agregar descuento");
            System.out.println("2. Quitar descuento");
            System.out.println("3. Volver al menú principal");
            System.out.println("===================================");
            System.out.print("Elige una opción: ");
            int opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese la cantidad mínima de productos: ");
                    int cantidadMinima = scanner.nextInt();
                    System.out.print("Ingrese el porcentaje de descuento: ");
                    double porcentajeDescuento = scanner.nextDouble();
                    scanner.nextLine(); // Consumir la nueva línea
                    descuentos.add(new Descuento(cantidadMinima, porcentajeDescuento));
                    System.out.println("Descuento agregado.");
                    break;
                case 2:
                    System.out.print("Ingrese el número del descuento a quitar: ");
                    int indice = leerOpcion(scanner) - 1;
                    if (indice >= 0 && indice < descuentos.size()) {
                        descuentos.remove(indice);
                        System.out.println("Descuento quitado.");
                    } else {
                        System.out.println("Número de descuento no válido.");
                    }
                    break;
                case 3:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}

// Clase para representar un descuento
class Descuento {
    private int cantidadMinima;
    private double porcentajeDescuento;

    public Descuento(int cantidadMinima, double porcentajeDescuento) {
        this.cantidadMinima = cantidadMinima;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }
}