package com.unicordoba.FinalProject.controller;

import com.unicordoba.FinalProject.dto.DashboardDTO;
import com.unicordoba.FinalProject.entity.*;
import com.unicordoba.FinalProject.repository.*;
import com.unicordoba.FinalProject.service.*;
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

    // --- SERVICIOS ---
    @Autowired private ProductoService productoService;
    @Autowired private ClienteService clienteService;
    @Autowired private SedeService sedeService;
    @Autowired private CompraService compraService;
    @Autowired private PromocionService promocionService;
    @Autowired private UsuarioService usuarioService;

    // --- REPOSITORIOS (Para consultas directas y filtros) ---
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

    // ================= INICIO Y DASHBOARD =================
    @GetMapping("/")
    public String index() {
        return "redirect:/web/dashboard"; // Redirige siempre al dashboard
    }

    @GetMapping("/web/dashboard")
    public String dashboard(Model model) {
        DashboardDTO stats = new DashboardDTO();
        stats.setTotalVentasDia(pedidoRepository.contarVentasHoy());
        stats.setIngresosDia(pedidoRepository.sumarIngresosHoy());
        stats.setProductosBajoStock(inventarioRepository.contarProductosBajoStock());
        stats.setClientesRegistrados(clienteRepository.count());
        model.addAttribute("stats", stats);

        // Datos para Gráficos
        model.addAttribute("graficoVentas", pedidoRepository.obtenerVentasUltimosDias());
        model.addAttribute("graficoProductos", orderItemRepository.obtenerProductosMasVendidos());
        return "dashboard";
    }

    // ================= 1. GESTIÓN DE USUARIOS =================
    @GetMapping("/web/usuarios")
    public String listarUsuarios(@RequestParam(required = false) String username, @RequestParam(required = false) String rol, Model model) {
        List<Usuario> lista = (username == null && rol == null) ? usuarioService.obtenerTodos() : usuarioRepository.filtrar(username, rol);
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
        model.addAttribute("usuario", usuarioService.obtenerPorId(id).orElse(null));
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
            ra.addFlashAttribute("error", "Error inesperado al eliminar usuario.");
        }
        return "redirect:/web/usuarios";
    }

    @PostMapping("/web/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes ra) {
        usuarioService.guardar(usuario);
        ra.addFlashAttribute("mensaje", "Usuario guardado exitosamente.");
        return "redirect:/web/usuarios";
    }

    // ================= 2. GESTIÓN DE CLIENTES =================
    @GetMapping("/web/clientes")
    public String listarClientes(@RequestParam(required = false) String nombre, @RequestParam(required = false) String email, @RequestParam(required = false) String telefono, Model model) {
        List<Cliente> lista = (nombre == null && email == null && telefono == null) ? clienteService.obtenerTodos() : clienteRepository.buscarConFiltros(nombre, email, telefono);
        model.addAttribute("listaClientes", lista);
        return "clientes";
    }

    // ESTE ERA EL MÉTODO QUE TE FALTABA:
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
            ra.addFlashAttribute("error", "No se puede eliminar: El cliente tiene facturas asociadas.");
        }
        return "redirect:/web/clientes";
    }

    @PostMapping("/web/clientes/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes ra) {
        clienteService.guardarCliente(cliente);
        ra.addFlashAttribute("mensaje", "Cliente guardado.");
        return "redirect:/web/clientes";
    }

    // ================= 3. GESTIÓN DE PRODUCTOS =================
    @GetMapping("/web/productos")
    public String listarProductos(@RequestParam(required = false) String nombre, @RequestParam(required = false) BigDecimal minPrecio, @RequestParam(required = false) BigDecimal maxPrecio, @RequestParam(required = false) Boolean activo, Model model) {
        List<Producto> resultados = (nombre == null && minPrecio == null && maxPrecio == null && activo == null) ?
                productoService.obtenerTodos() : productoRepository.buscarProductosAvanzado(nombre, minPrecio, maxPrecio, activo);
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
            ra.addFlashAttribute("error", "No se puede eliminar: Hay inventario o ventas de este producto.");
        }
        return "redirect:/web/productos";
    }

    @PostMapping("/web/productos/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, RedirectAttributes ra) {
        productoService.guardarProducto(producto);
        ra.addFlashAttribute("mensaje", "Producto guardado.");
        return "redirect:/web/productos";
    }

    // ================= 4. SEDES, PROVEEDORES Y PROMOCIONES =================
    // (Simplificado para brevedad, sigue el mismo patrón que los anteriores)
    @GetMapping("/web/sedes")
    public String listarSedes(@RequestParam(required = false) String ciudad, @RequestParam(required = false) String nombre, Model model) {
        model.addAttribute("listaSedes", (ciudad == null && nombre == null) ? sedeService.obtenerTodas() : sedeRepository.buscarPorCiudadYNombre(ciudad, nombre));
        return "sedes";
    }
    @GetMapping("/web/sedes/nuevo") public String formSede(Model m) { m.addAttribute("sede", new Sede()); return "nueva_sede"; }
    @PostMapping("/web/sedes/guardar") public String guardSede(Sede s) { sedeService.guardarSede(s); return "redirect:/web/sedes"; }
    @GetMapping("/web/sedes/eliminar/{id}") public String delSede(@PathVariable Integer id, RedirectAttributes ra) { try{sedeService.eliminarSede(id);}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar sede con historial");} return "redirect:/web/sedes";}
    @GetMapping("/web/sedes/editar/{id}") public String edSede(@PathVariable Integer id, Model m) { m.addAttribute("sede", sedeService.obtenerPorId(id).orElse(null)); return "nueva_sede"; }

    @GetMapping("/web/proveedores")
    public String listarProv(@RequestParam(required = false) String nombre, @RequestParam(required = false) String contacto, Model model) {
        model.addAttribute("listaProveedores", (nombre == null && contacto == null) ? proveedorRepository.findAll() : proveedorRepository.buscarAvanzado(nombre, contacto));
        return "proveedores";
    }
    @GetMapping("/web/proveedores/nuevo") public String formProv(Model m) { m.addAttribute("proveedor", new Proveedor()); return "nuevo_proveedor"; }
    @PostMapping("/web/proveedores/guardar") public String guardProv(Proveedor p) { proveedorRepository.save(p); return "redirect:/web/proveedores"; }
    @GetMapping("/web/proveedores/eliminar/{id}") public String delProv(@PathVariable Integer id, RedirectAttributes ra) { try{proveedorRepository.deleteById(id);}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar proveedor con compras");} return "redirect:/web/proveedores";}
    @GetMapping("/web/proveedores/editar/{id}") public String edProv(@PathVariable Integer id, Model m) { m.addAttribute("proveedor", proveedorRepository.findById(id).orElse(null)); return "nuevo_proveedor"; }

    @GetMapping("/web/promociones")
    public String listarPromociones(@RequestParam(required = false) String codigo, @RequestParam(required = false) String tipo, Model model) {
        model.addAttribute("listaPromociones", (codigo == null && tipo == null) ? promocionService.obtenerTodas() : promocionRepository.filtrar(codigo, tipo));
        return "promociones";
    }
    @GetMapping("/web/promociones/nuevo") public String formPromo(Model m) { m.addAttribute("promocion", new Promocion()); return "nueva_promocion"; }
    @PostMapping("/web/promociones/guardar") public String guardPromo(Promocion p) { promocionService.guardar(p); return "redirect:/web/promociones"; }
    @GetMapping("/web/promociones/eliminar/{id}") public String delPromo(@PathVariable Integer id) { promocionService.eliminar(id); return "redirect:/web/promociones"; }
    @GetMapping("/web/promociones/editar/{id}") public String edPromo(@PathVariable Integer id, Model m) { m.addAttribute("promocion", promocionService.obtenerPorId(id).orElse(null)); return "nueva_promocion"; }

    // ================= 5. OPERACIONES (POS, COMPRAS, INVENTARIO) =================
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
    public String listarInventario(@RequestParam(required = false) Integer sedeId, @RequestParam(required = false) String producto, Model model) {
        model.addAttribute("listaInventario", (sedeId == null && producto == null) ? inventarioRepository.findAll() : inventarioRepository.filtrarInventario(sedeId, producto));
        model.addAttribute("sedes", sedeService.obtenerTodas());
        return "inventario";
    }
    @GetMapping("/web/inventario/editar/{id}") public String edInv(@PathVariable Integer id, Model m) { m.addAttribute("inventario", inventarioRepository.findById(id).orElse(null)); return "editar_inventario"; }
    @PostMapping("/web/inventario/guardar") public String guardInv(Inventario i, RedirectAttributes ra) {
        Inventario ex = inventarioRepository.findById(i.getInventarioId()).orElse(null);
        if(ex!=null){ ex.setCantidad(i.getCantidad()); ex.setUnidad(i.getUnidad()); inventarioRepository.save(ex); ra.addFlashAttribute("mensaje", "Stock actualizado"); }
        return "redirect:/web/inventario";
    }
    @GetMapping("/web/inventario/eliminar/{id}") public String delInv(@PathVariable Integer id, RedirectAttributes ra) { try{inventarioRepository.deleteById(id);}catch(Exception e){ra.addFlashAttribute("error","Error al eliminar");} return "redirect:/web/inventario"; }


    // ================= 6. HISTORIALES (CON FILTROS) =================
    @GetMapping("/web/historial/ventas")
    public String historialVentas(@RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fin, @RequestParam(required = false) String estado, Model model) {
        model.addAttribute("listaFacturas", (inicio == null && fin == null && estado == null) ? facturaRepository.findAll() : facturaRepository.filtrarFacturas(inicio, fin, estado));
        return "historial_ventas";
    }
    @GetMapping("/web/historial/ventas/eliminar/{id}") public String delFac(@PathVariable Integer id, RedirectAttributes ra) { try{facturaRepository.deleteById(id); ra.addFlashAttribute("mensaje","Factura eliminada");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar factura");} return "redirect:/web/historial/ventas"; }

    @GetMapping("/web/historial/compras")
    public String historialCompras(@RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fin, @RequestParam(required = false) Integer proveedorId, Model model) {
        model.addAttribute("listaCompras", (inicio == null && fin == null && proveedorId == null) ? compraRepository.findAll() : compraRepository.filtrarCompras(inicio, fin, proveedorId));
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "historial_compras";
    }
    @GetMapping("/web/historial/compras/eliminar/{id}") public String delCom(@PathVariable Integer id, RedirectAttributes ra) { try{compraRepository.deleteById(id); ra.addFlashAttribute("mensaje","Compra eliminada");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar compra");} return "redirect:/web/historial/compras"; }

    @GetMapping("/web/historial/pagos")
    public String historialPagos(@RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fin, @RequestParam(required = false) String metodo, Model model) {
        model.addAttribute("listaPagos", (inicio == null && fin == null && metodo == null) ? pagoRepository.findAll() : pagoRepository.filtrarPagos(inicio, fin, metodo));
        return "historial_pagos";
    }
    @GetMapping("/web/historial/pagos/eliminar/{id}") public String delPag(@PathVariable Integer id, RedirectAttributes ra) { try{pagoRepository.deleteById(id); ra.addFlashAttribute("mensaje","Pago eliminado");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar pago");} return "redirect:/web/historial/pagos"; }
}