package com.unicordoba.FinalProject.controller;

import com.unicordoba.FinalProject.dto.DashboardDTO;
import com.unicordoba.FinalProject.entity.*;
import com.unicordoba.FinalProject.repository.*;
import com.unicordoba.FinalProject.service.*;
import jakarta.servlet.http.HttpServletRequest;
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


    private String clean(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }

    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/")
    public String index() { return "redirect:/web/dashboard"; }

    @GetMapping("/web/dashboard")
    public String dashboard(Model model) {
        DashboardDTO stats = new DashboardDTO();
        stats.setTotalVentasDia(pedidoRepository.contarVentasHoy());
        stats.setIngresosDia(pedidoRepository.sumarIngresosHoy());
        stats.setProductosBajoStock(inventarioRepository.contarProductosBajoStock());
        stats.setClientesRegistrados(clienteRepository.count());
        model.addAttribute("stats", stats);
        model.addAttribute("graficoVentas", pedidoRepository.obtenerVentasUltimosDias());
        model.addAttribute("graficoProductos", orderItemRepository.obtenerProductosMasVendidos());
        return "dashboard";
    }

    // ================= FILTROS CORREGIDOS =================

    @GetMapping("/web/usuarios")
    public String listarUsuarios(@RequestParam(required = false) String username,
                                 @RequestParam(required = false) String rol, Model model) {
        // Limpiamos los parámetros (convertimos "" a null)
        username = clean(username);
        rol = clean(rol);

        List<Usuario> lista = (username == null && rol == null) ?
                usuarioService.obtenerTodos() : usuarioRepository.filtrar(username, rol);
        model.addAttribute("listaUsuarios", lista);
        return "usuarios";
    }

    @GetMapping("/web/clientes")
    public String listarClientes(@RequestParam(required = false) String nombre,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String telefono, Model model) {
        nombre = clean(nombre); email = clean(email); telefono = clean(telefono);

        List<Cliente> lista = (nombre == null && email == null && telefono == null) ?
                clienteService.obtenerTodos() : clienteRepository.buscarConFiltros(nombre, email, telefono);
        model.addAttribute("listaClientes", lista);
        return "clientes";
    }

    @GetMapping("/web/productos")
    public String listarProductos(@RequestParam(required = false) String nombre,
                                  @RequestParam(required = false) BigDecimal minPrecio,
                                  @RequestParam(required = false) BigDecimal maxPrecio,
                                  @RequestParam(required = false) Boolean activo, Model model) {
        nombre = clean(nombre);
        List<Producto> resultados = (nombre == null && minPrecio == null && maxPrecio == null && activo == null) ?
                productoService.obtenerTodos() : productoRepository.buscarProductosAvanzado(nombre, minPrecio, maxPrecio, activo);
        model.addAttribute("listaProductos", resultados);
        return "productos";
    }

    @GetMapping("/web/sedes")
    public String listarSedes(@RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) String nombre, Model model) {
        ciudad = clean(ciudad); nombre = clean(nombre);
        model.addAttribute("listaSedes", (ciudad == null && nombre == null) ?
                sedeService.obtenerTodas() : sedeRepository.buscarPorCiudadYNombre(ciudad, nombre));
        return "sedes";
    }

    @GetMapping("/web/proveedores")
    public String listarProveedores(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false) String contacto, Model model) {
        nombre = clean(nombre); contacto = clean(contacto);
        model.addAttribute("listaProveedores", (nombre == null && contacto == null) ?
                proveedorRepository.findAll() : proveedorRepository.buscarAvanzado(nombre, contacto));
        return "proveedores";
    }

    @GetMapping("/web/promociones")
    public String listarPromociones(@RequestParam(required = false) String codigo,
                                    @RequestParam(required = false) String tipo, Model model) {
        codigo = clean(codigo); tipo = clean(tipo);
        model.addAttribute("listaPromociones", (codigo == null && tipo == null) ?
                promocionService.obtenerTodas() : promocionRepository.filtrar(codigo, tipo));
        return "promociones";
    }

    @GetMapping("/web/inventario")
    public String listarInventario(@RequestParam(required = false) Integer sedeId,
                                   @RequestParam(required = false) String producto, Model model) {
        producto = clean(producto);
        model.addAttribute("listaInventario", (sedeId == null && producto == null) ?
                inventarioRepository.findAll() : inventarioRepository.filtrarInventario(sedeId, producto));
        model.addAttribute("sedes", sedeService.obtenerTodas());
        return "inventario";
    }

    // --- HISTORIALES CON FILTROS ---

    @GetMapping("/web/historial/ventas")
    public String historialVentas(@RequestParam(required = false) LocalDate inicio,
                                  @RequestParam(required = false) LocalDate fin,
                                  @RequestParam(required = false) String estado, Model model) {
        estado = clean(estado);
        List<Factura> lista;
        if (inicio == null && fin == null && estado == null) {
            lista = facturaRepository.findAll();
        } else {
            lista = facturaRepository.filtrarFacturas(inicio, fin, estado);
        }
        model.addAttribute("listaFacturas", lista);
        return "historial_ventas";
    }

    @GetMapping("/web/historial/compras")
    public String historialCompras(@RequestParam(required = false) LocalDate inicio,
                                   @RequestParam(required = false) LocalDate fin,
                                   @RequestParam(required = false) Integer proveedorId, Model model) {
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

    @GetMapping("/web/historial/pagos")
    public String historialPagos(@RequestParam(required = false) LocalDate inicio,
                                 @RequestParam(required = false) LocalDate fin,
                                 @RequestParam(required = false) String metodo, Model model) {
        metodo = clean(metodo);
        List<Pago> lista;
        if (inicio == null && fin == null && metodo == null) {
            lista = pagoRepository.findAll();
        } else {
            lista = pagoRepository.filtrarPagos(inicio, fin, metodo);
        }
        model.addAttribute("listaPagos", lista);
        return "historial_pagos";
    }

    // ... (MANTENER TODOS LOS MÉTODOS DE GUARDAR/ELIMINAR/EDITAR IGUAL QUE ANTES) ...
    // ... (Asegúrate de incluir los métodos formUsuario, guardarUsuario, eliminarUsuario, etc.) ...
    // ... (Por brevedad no los repito, pero NO LOS BORRES, solo reemplaza los métodos de listado con los de arriba) ...

    // ... (Aquí abajo van los métodos de create/save/delete que ya tenías) ...
    // --- PEGAR AQUÍ EL RESTO DEL CONTROLADOR QUE YA FUNCIONABA ---
    @GetMapping("/web/usuarios/nuevo") public String formUsuario(Model m) { m.addAttribute("usuario", new Usuario()); return "nuevo_usuario"; }
    @PostMapping("/web/usuarios/guardar") public String guardarUsuario(@ModelAttribute Usuario u, RedirectAttributes ra) { usuarioService.guardar(u); ra.addFlashAttribute("mensaje", "Usuario guardado."); return "redirect:/web/usuarios"; }
    @GetMapping("/web/usuarios/eliminar/{id}") public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes ra) { try { usuarioService.eliminar(id); ra.addFlashAttribute("mensaje", "Usuario eliminado."); } catch (Exception e) { ra.addFlashAttribute("error", "No se puede eliminar usuario con historial."); } return "redirect:/web/usuarios"; }
    @GetMapping("/web/usuarios/editar/{id}") public String editarUsuario(@PathVariable Integer id, Model m) { m.addAttribute("usuario", usuarioService.obtenerPorId(id).orElse(null)); return "nuevo_usuario"; }

    @GetMapping("/web/clientes/nuevo") public String formCliente(Model m) { m.addAttribute("cliente", new Cliente()); return "nuevo_cliente"; }
    @PostMapping("/web/clientes/guardar") public String guardarCliente(@ModelAttribute Cliente c, RedirectAttributes ra) { clienteService.guardarCliente(c); ra.addFlashAttribute("mensaje", "Cliente guardado."); return "redirect:/web/clientes"; }
    @GetMapping("/web/clientes/eliminar/{id}") public String eliminarCliente(@PathVariable Integer id, RedirectAttributes ra) { try { clienteService.eliminarCliente(id); ra.addFlashAttribute("mensaje", "Cliente eliminado."); } catch (Exception e) { ra.addFlashAttribute("error", "No se puede eliminar cliente con facturas."); } return "redirect:/web/clientes"; }
    @GetMapping("/web/clientes/editar/{id}") public String editarCliente(@PathVariable Integer id, Model m) { m.addAttribute("cliente", clienteService.obtenerPorId(id).orElse(null)); return "nuevo_cliente"; }

    @GetMapping("/web/productos/nuevo") public String formProducto(Model m) { Producto p = new Producto(); p.setActivo(true); m.addAttribute("producto", p); return "nuevo_producto"; }
    @PostMapping("/web/productos/guardar") public String guardarProducto(@ModelAttribute Producto p, RedirectAttributes ra) { productoService.guardarProducto(p); ra.addFlashAttribute("mensaje", "Producto guardado."); return "redirect:/web/productos"; }
    @GetMapping("/web/productos/eliminar/{id}") public String eliminarProducto(@PathVariable Integer id, RedirectAttributes ra) { try { productoService.eliminarProducto(id); ra.addFlashAttribute("mensaje", "Producto eliminado."); } catch (Exception e) { ra.addFlashAttribute("error", "No se puede eliminar producto con ventas."); } return "redirect:/web/productos"; }
    @GetMapping("/web/productos/editar/{id}") public String editarProducto(@PathVariable Integer id, Model m) { m.addAttribute("producto", productoService.obtenerPorId(id).orElse(null)); return "nuevo_producto"; }

    @GetMapping("/web/sedes/nuevo") public String formSede(Model m) { m.addAttribute("sede", new Sede()); return "nueva_sede"; }
    @PostMapping("/web/sedes/guardar") public String guardarSede(@ModelAttribute Sede s, RedirectAttributes ra) { sedeService.guardarSede(s); ra.addFlashAttribute("mensaje", "Sede guardada."); return "redirect:/web/sedes"; }
    @GetMapping("/web/sedes/eliminar/{id}") public String eliminarSede(@PathVariable Integer id, RedirectAttributes ra) { try { sedeService.eliminarSede(id); ra.addFlashAttribute("mensaje", "Sede eliminada."); } catch (Exception e) { ra.addFlashAttribute("error", "No se puede eliminar sede con historial."); } return "redirect:/web/sedes"; }
    @GetMapping("/web/sedes/editar/{id}") public String editarSede(@PathVariable Integer id, Model m) { m.addAttribute("sede", sedeService.obtenerPorId(id).orElse(null)); return "nueva_sede"; }

    @GetMapping("/web/proveedores/nuevo") public String formProv(Model m) { m.addAttribute("proveedor", new Proveedor()); return "nuevo_proveedor"; }
    @PostMapping("/web/proveedores/guardar") public String guardarProv(@ModelAttribute Proveedor p, RedirectAttributes ra) { proveedorRepository.save(p); ra.addFlashAttribute("mensaje", "Proveedor guardado."); return "redirect:/web/proveedores"; }
    @GetMapping("/web/proveedores/eliminar/{id}") public String eliminarProv(@PathVariable Integer id, RedirectAttributes ra) { try { proveedorRepository.deleteById(id); ra.addFlashAttribute("mensaje", "Proveedor eliminado."); } catch (Exception e) { ra.addFlashAttribute("error", "No se puede eliminar proveedor con compras."); } return "redirect:/web/proveedores"; }
    @GetMapping("/web/proveedores/editar/{id}") public String editarProv(@PathVariable Integer id, Model m) { m.addAttribute("proveedor", proveedorRepository.findById(id).orElse(null)); return "nuevo_proveedor"; }

    @GetMapping("/web/promociones/nuevo") public String formPromo(Model m) { m.addAttribute("promocion", new Promocion()); return "nueva_promocion"; }
    @PostMapping("/web/promociones/guardar") public String guardarPromo(@ModelAttribute Promocion p, RedirectAttributes ra) { promocionService.guardar(p); ra.addFlashAttribute("mensaje", "Promoción guardada."); return "redirect:/web/promociones"; }
    @GetMapping("/web/promociones/eliminar/{id}") public String eliminarPromo(@PathVariable Integer id, RedirectAttributes ra) { promocionService.eliminar(id); ra.addFlashAttribute("mensaje", "Promoción eliminada."); return "redirect:/web/promociones"; }
    @GetMapping("/web/promociones/editar/{id}") public String editarPromo(@PathVariable Integer id, Model m) { m.addAttribute("promocion", promocionService.obtenerPorId(id).orElse(null)); return "nueva_promocion"; }

    @GetMapping("/web/inventario/editar/{id}") public String edInv(@PathVariable Integer id, Model m) { m.addAttribute("inventario", inventarioRepository.findById(id).orElse(null)); return "editar_inventario"; }
    @PostMapping("/web/inventario/guardar") public String guardInv(Inventario i, RedirectAttributes ra) { Inventario ex = inventarioRepository.findById(i.getInventarioId()).orElse(null); if(ex!=null){ ex.setCantidad(i.getCantidad()); ex.setUnidad(i.getUnidad()); inventarioRepository.save(ex); ra.addFlashAttribute("mensaje", "Stock actualizado"); } return "redirect:/web/inventario"; }
    @GetMapping("/web/inventario/eliminar/{id}") public String delInv(@PathVariable Integer id, RedirectAttributes ra) { try{inventarioRepository.deleteById(id);}catch(Exception e){ra.addFlashAttribute("error","Error al eliminar");} return "redirect:/web/inventario"; }

    @GetMapping("/web/pos") public String pantallaPos(Model m) { m.addAttribute("listaProductos", productoService.obtenerTodos()); m.addAttribute("listaClientes", clienteService.obtenerTodos()); m.addAttribute("listaSedes", sedeService.obtenerTodas()); m.addAttribute("listaPromociones", promocionService.obtenerTodas()); return "pos"; }
    @GetMapping("/web/compras/nueva") public String formNuevaCompra(Model m) { m.addAttribute("listaProveedores", proveedorRepository.findAll()); m.addAttribute("listaSedes", sedeService.obtenerTodas()); m.addAttribute("listaProductos", productoService.obtenerTodos()); return "nueva_compra"; }

    @GetMapping("/web/historial/ventas/eliminar/{id}") public String delFac(@PathVariable Integer id, RedirectAttributes ra) { try{facturaRepository.deleteById(id); ra.addFlashAttribute("mensaje","Factura eliminada");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar factura");} return "redirect:/web/historial/ventas"; }
    @GetMapping("/web/historial/compras/eliminar/{id}") public String delCom(@PathVariable Integer id, RedirectAttributes ra) { try{compraRepository.deleteById(id); ra.addFlashAttribute("mensaje","Compra eliminada");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar compra");} return "redirect:/web/historial/compras"; }
    @GetMapping("/web/historial/pagos/eliminar/{id}") public String delPag(@PathVariable Integer id, RedirectAttributes ra) { try{pagoRepository.deleteById(id); ra.addFlashAttribute("mensaje","Pago eliminado");}catch(Exception e){ra.addFlashAttribute("error","No se puede eliminar pago");} return "redirect:/web/historial/pagos"; }
}