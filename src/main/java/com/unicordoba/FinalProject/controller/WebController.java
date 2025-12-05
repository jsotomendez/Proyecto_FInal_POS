package com.unicordoba.FinalProject.controller;

import com.unicordoba.FinalProject.dto.DashboardDTO;
import com.unicordoba.FinalProject.entity.*;
import com.unicordoba.FinalProject.repository.*;
import com.unicordoba.FinalProject.service.*;
import jakarta.servlet.http.HttpServletRequest; // <--- IMPORTANTE: Nueva importación
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WebController {

    @Autowired private ProductoService productoService;
    @Autowired private ClienteService clienteService;
    @Autowired private SedeService sedeService;
    @Autowired private CompraService compraService;
    @Autowired private PromocionService promocionService;
    @Autowired private UsuarioService usuarioService;

    @Autowired private ProductoRepository productoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private InventarioRepository inventarioRepository;
    @Autowired private SedeRepository sedeRepository;
    @Autowired private FacturaRepository facturaRepository;
    @Autowired private CompraRepository compraRepository;
    @Autowired private PagoRepository pagoRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PromocionRepository promocionRepository;

    // ====================================================================
    // CORRECCIÓN PARA EL ERROR 500 (THYMELEAF #request)
    // Este método se ejecuta antes de cualquier petición y pasa la URL actual a la vista
    // ====================================================================
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // ================= INICIO Y DASHBOARD =================
    @GetMapping("/")
    public String index() {
        return "redirect:/web/dashboard";
    }

    @GetMapping("/web/dashboard")
    public String dashboard(Model model) {
        DashboardDTO stats = new DashboardDTO();
        stats.setTotalVentasDia(pedidoRepository.contarVentasHoy());
        stats.setIngresosDia(pedidoRepository.sumarIngresosHoy());
        stats.setProductosBajoStock(inventarioRepository.contarProductosBajoStock());
        stats.setClientesRegistrados(clienteRepository.count());
        model.addAttribute("stats", stats);

        List<Object[]> ventasData = pedidoRepository.obtenerVentasUltimosDias();
        model.addAttribute("graficoVentas", ventasData);

        List<Object[]> productosTop = orderItemRepository.obtenerProductosMasVendidos();
        model.addAttribute("graficoProductos", productosTop);

        return "dashboard";
    }

    // ================= GESTIÓN DE USUARIOS =================
    @GetMapping("/web/usuarios")
    public String listarUsuarios(@RequestParam(required = false) String username,
                                 @RequestParam(required = false) String rol,
                                 Model model) {
        List<Usuario> lista = (username == null && rol == null) ?
                usuarioService.obtenerTodos() : usuarioRepository.filtrar(username, rol);
        model.addAttribute("listaUsuarios", lista);
        return "usuarios";
    }

    @GetMapping("/web/usuarios/nuevo")
    public String formUsuario(Model model) {
        Usuario u = new Usuario();
        u.setActivo(true);
        model.addAttribute("usuario", u);
        return "nuevo_usuario";
    }

    @GetMapping("/web/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario u = usuarioService.obtenerPorId(id).orElse(null);
        model.addAttribute("usuario", u);
        return "nuevo_usuario";
    }

    @GetMapping("/web/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "🚫 No se puede eliminar: El usuario tiene ventas o registros asociados.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado al eliminar el usuario.");
        }
        return "redirect:/web/usuarios";
    }

    @PostMapping("/web/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes ra) {
        usuarioService.guardar(usuario);
        ra.addFlashAttribute("mensaje", "Usuario guardado exitosamente.");
        return "redirect:/web/usuarios";
    }

    // ================= GESTIÓN DE CLIENTES =================
    @GetMapping("/web/clientes")
    public String listarClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono,
            Model model) {

        List<Cliente> lista;
        if (nombre == null && email == null && telefono == null) {
            lista = clienteService.obtenerTodos();
        } else {
            lista = clienteRepository.buscarConFiltros(nombre, email, telefono);
        }
        model.addAttribute("listaClientes", lista);
        return "clientes";
    }

    @GetMapping("/web/clientes/nuevo")
    public String formCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "nuevo_cliente";
    }

    @GetMapping("/web/clientes/editar/{id}")
    public String editarCliente(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.obtenerPorId(id).orElse(null));
        return "nuevo_cliente";
    }

    @GetMapping("/web/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            clienteService.eliminarCliente(id);
            ra.addFlashAttribute("mensaje", "Cliente eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar el cliente (tiene facturas asociadas).");
        }
        return "redirect:/web/clientes";
    }

    @PostMapping("/web/clientes/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes ra) {
        clienteService.guardarCliente(cliente);
        ra.addFlashAttribute("mensaje", "Cliente guardado.");
        return "redirect:/web/clientes";
    }

    // ================= PRODUCTOS =================
    @GetMapping("/web/productos")
    public String listarProductos(@RequestParam(required = false) String nombre,
                                  @RequestParam(required = false) BigDecimal minPrecio,
                                  @RequestParam(required = false) BigDecimal maxPrecio,
                                  @RequestParam(required = false) Boolean activo,
                                  Model model) {
        List<Producto> resultados;
        if (nombre == null && minPrecio == null && maxPrecio == null && activo == null) {
            resultados = productoService.obtenerTodos();
        } else {
            resultados = productoRepository.buscarProductosAvanzado(nombre, minPrecio, maxPrecio, activo);
        }
        model.addAttribute("listaProductos", resultados);
        return "productos";
    }

    @GetMapping("/web/productos/nuevo")
    public String formProducto(Model model) {
        Producto p = new Producto();
        p.setActivo(true);
        model.addAttribute("producto", p);
        return "nuevo_producto";
    }

    @GetMapping("/web/productos/editar/{id}")
    public String editarProducto(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id).orElse(null));
        return "nuevo_producto";
    }

    @GetMapping("/web/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            productoService.eliminarProducto(id);
            ra.addFlashAttribute("mensaje", "Producto eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar el producto (tiene historial).");
        }
        return "redirect:/web/productos";
    }

    @PostMapping("/web/productos/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, RedirectAttributes ra) {
        productoService.guardarProducto(producto);
        ra.addFlashAttribute("mensaje", "Producto guardado.");
        return "redirect:/web/productos";
    }

    // ================= SEDES =================
    @GetMapping("/web/sedes")
    public String listarSedes(@RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) String nombre,
                              Model model) {
        List<Sede> lista;
        if (ciudad == null && nombre == null) {
            lista = sedeService.obtenerTodas();
        } else {
            lista = sedeRepository.buscarPorCiudadYNombre(ciudad, nombre);
        }
        model.addAttribute("listaSedes", lista);
        return "sedes";
    }

    @GetMapping("/web/sedes/nuevo")
    public String formSede(Model model) {
        model.addAttribute("sede", new Sede());
        return "nueva_sede";
    }

    @GetMapping("/web/sedes/editar/{id}")
    public String editarSede(@PathVariable Integer id, Model model) {
        model.addAttribute("sede", sedeService.obtenerPorId(id).orElse(null));
        return "nueva_sede";
    }

    @GetMapping("/web/sedes/eliminar/{id}")
    public String eliminarSede(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            sedeService.eliminarSede(id);
            ra.addFlashAttribute("mensaje", "Sede eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la sede (tiene historial).");
        }
        return "redirect:/web/sedes";
    }

    @PostMapping("/web/sedes/guardar")
    public String guardarSede(@ModelAttribute("sede") Sede sede, RedirectAttributes ra) {
        sedeService.guardarSede(sede);
        ra.addFlashAttribute("mensaje", "Sede guardada.");
        return "redirect:/web/sedes";
    }

    // ================= PROVEEDORES =================
    @GetMapping("/web/proveedores")
    public String listarProveedores(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false) String contacto,
                                    Model model) {
        List<Proveedor> lista;
        if (nombre == null && contacto == null) {
            lista = proveedorRepository.findAll();
        } else {
            lista = proveedorRepository.buscarAvanzado(nombre, contacto);
        }
        model.addAttribute("listaProveedores", lista);
        return "proveedores";
    }

    @GetMapping("/web/proveedores/nuevo")
    public String formProveedor(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "nuevo_proveedor";
    }

    @GetMapping("/web/proveedores/editar/{id}")
    public String editarProveedor(@PathVariable Integer id, Model model) {
        model.addAttribute("proveedor", proveedorRepository.findById(id).orElse(null));
        return "nuevo_proveedor";
    }

    @GetMapping("/web/proveedores/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            proveedorRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Proveedor eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar el proveedor (tiene compras asociadas).");
        }
        return "redirect:/web/proveedores";
    }

    @PostMapping("/web/proveedores/guardar")
    public String guardarProveedor(@ModelAttribute("proveedor") Proveedor proveedor, RedirectAttributes ra) {
        proveedorRepository.save(proveedor);
        ra.addFlashAttribute("mensaje", "Proveedor guardado.");
        return "redirect:/web/proveedores";
    }

    // ================= PROMOCIONES =================
    @GetMapping("/web/promociones")
    public String listarPromociones(@RequestParam(required = false) String codigo,
                                    @RequestParam(required = false) String tipo,
                                    Model model) {
        List<Promocion> lista = (codigo == null && tipo == null) ?
                promocionService.obtenerTodas() : promocionRepository.filtrar(codigo, tipo);
        model.addAttribute("listaPromociones", lista);
        return "promociones";
    }

    @GetMapping("/web/promociones/nuevo")
    public String formPromocion(Model model) {
        model.addAttribute("promocion", new Promocion());
        return "nueva_promocion";
    }

    @GetMapping("/web/promociones/editar/{id}")
    public String editarPromocion(@PathVariable Integer id, Model model) {
        model.addAttribute("promocion", promocionService.obtenerPorId(id).orElse(null));
        return "nueva_promocion";
    }

    @GetMapping("/web/promociones/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Integer id, RedirectAttributes ra) {
        promocionService.eliminar(id);
        ra.addFlashAttribute("mensaje", "Promoción eliminada.");
        return "redirect:/web/promociones";
    }

    @PostMapping("/web/promociones/guardar")
    public String guardarPromocion(@ModelAttribute("promocion") Promocion promocion, RedirectAttributes ra) {
        promocionService.guardar(promocion);
        ra.addFlashAttribute("mensaje", "Promoción guardada.");
        return "redirect:/web/promociones";
    }

    // ================= OPERACIONES (POS, COMPRAS, INVENTARIO) =================
    @GetMapping("/web/pos")
    public String pantallaPos(Model model) {
        model.addAttribute("listaProductos", productoService.obtenerTodos());
        model.addAttribute("listaClientes", clienteService.obtenerTodos());
        model.addAttribute("listaSedes", sedeService.obtenerTodas());
        model.addAttribute("listaPromociones", promocionService.obtenerTodas());
        return "pos";
    }

    @GetMapping("/web/compras/nueva")
    public String formNuevaCompra(Model model) {
        model.addAttribute("listaProveedores", proveedorRepository.findAll());
        model.addAttribute("listaSedes", sedeService.obtenerTodas());
        model.addAttribute("listaProductos", productoService.obtenerTodos());
        return "nueva_compra";
    }

    @GetMapping("/web/inventario")
    public String listarInventario(@RequestParam(required = false) Integer sedeId,
                                   @RequestParam(required = false) String producto,
                                   Model model) {
        List<Inventario> lista = (sedeId == null && producto == null) ?
                inventarioRepository.findAll() : inventarioRepository.filtrarInventario(sedeId, producto);
        model.addAttribute("listaInventario", lista);
        model.addAttribute("sedes", sedeService.obtenerTodas());
        return "inventario";
    }

    @GetMapping("/web/inventario/editar/{id}")
    public String editarInventario(@PathVariable Integer id, Model model) {
        Inventario inv = inventarioRepository.findById(id).orElse(null);
        model.addAttribute("inventario", inv);
        return "editar_inventario";
    }

    @PostMapping("/web/inventario/guardar")
    public String guardarInventario(@ModelAttribute("inventario") Inventario inventario, RedirectAttributes ra) {
        Inventario existente = inventarioRepository.findById(inventario.getInventarioId()).orElse(null);
        if(existente != null) {
            existente.setCantidad(inventario.getCantidad());
            existente.setUnidad(inventario.getUnidad());
            inventarioRepository.save(existente);
            ra.addFlashAttribute("mensaje", "Inventario actualizado manualmente.");
        }
        return "redirect:/web/inventario";
    }

    @GetMapping("/web/inventario/eliminar/{id}")
    public String eliminarInventario(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            inventarioRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Registro de inventario eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar.");
        }
        return "redirect:/web/inventario";
    }

    // ================= HISTORIALES =================
    @GetMapping("/web/historial/ventas")
    public String historialVentas(@RequestParam(required = false) LocalDate inicio,
                                  @RequestParam(required = false) LocalDate fin,
                                  @RequestParam(required = false) String estado,
                                  Model model) {
        List<Factura> lista;
        if (inicio == null && fin == null && estado == null) {
            lista = facturaRepository.findAll();
        } else {
            lista = facturaRepository.filtrarFacturas(inicio, fin, estado);
        }
        model.addAttribute("listaFacturas", lista);
        return "historial_ventas";
    }

    @GetMapping("/web/historial/ventas/eliminar/{id}")
    public String eliminarFactura(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            facturaRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Factura eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la factura.");
        }
        return "redirect:/web/historial/ventas";
    }

    @GetMapping("/web/historial/compras")
    public String historialCompras(@RequestParam(required = false) LocalDate inicio,
                                   @RequestParam(required = false) LocalDate fin,
                                   @RequestParam(required = false) Integer proveedorId,
                                   Model model) {
        List<Compra> lista;
        if (inicio == null && fin == null && proveedorId == null) {
            lista = compraRepository.findAll();
        } else {
            lista = compraRepository.filtrarCompras(inicio, fin, proveedorId);
        }
        model.addAttribute("listaCompras", lista);
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "historial_compras";
    }

    @GetMapping("/web/historial/compras/eliminar/{id}")
    public String eliminarCompra(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            compraRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Compra eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la compra.");
        }
        return "redirect:/web/historial/compras";
    }

    @GetMapping("/web/historial/pagos")
    public String historialPagos(@RequestParam(required = false) LocalDate inicio,
                                 @RequestParam(required = false) LocalDate fin,
                                 @RequestParam(required = false) String metodo,
                                 Model model) {
        List<Pago> lista;
        if (inicio == null && fin == null && metodo == null) {
            lista = pagoRepository.findAll();
        } else {
            lista = pagoRepository.filtrarPagos(inicio, fin, metodo);
        }
        model.addAttribute("listaPagos", lista);
        return "historial_pagos";
    }

    @GetMapping("/web/historial/pagos/eliminar/{id}")
    public String eliminarPago(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            pagoRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Pago eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar el pago.");
        }
        return "redirect:/web/historial/pagos";
    }
}