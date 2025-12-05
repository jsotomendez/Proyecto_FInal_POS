-- ==================================================================
-- DATA.SQL - DATOS SEMILLA MASIVOS PARA POS (Con INSERT IGNORE)
-- ==================================================================

-- 1. USUARIOS (Admin, Gerentes, Cajeros)
INSERT IGNORE INTO usuario (usuario_id, username, password_hash, email, rol, activo, fecha_creacion) VALUES
(1, 'admin', '1234', 'admin@pos.com', 'ADMIN', 1, NOW()),
(2, 'gerente_general', '1234', 'gerencia@pos.com', 'GERENTE', 1, NOW()),
(3, 'cajero_norte', '1234', 'norte@pos.com', 'CAJERO', 1, NOW()),
(4, 'cajero_centro', '1234', 'centro@pos.com', 'CAJERO', 1, NOW()),
(5, 'cajero_sur', '1234', 'sur@pos.com', 'CAJERO', 1, NOW()),
(6, 'cajero_cerete', '1234', 'cerete@pos.com', 'CAJERO', 1, NOW()),
(7, 'auditor', '1234', 'auditor@pos.com', 'GERENTE', 0, NOW());

-- 2. SEDES (Más ubicaciones)
INSERT IGNORE INTO sede (sede_id, nombre, direccion, ciudad, telefono, latitud, longitud, horario) VALUES
(1, 'Sede Norte - Recreo', 'Calle 68 con Circunvalar', 'Montería', '3001112233', 8.7605, -75.8850, '10:00 AM - 10:00 PM'),
(2, 'Sede Centro', 'Calle 27 con Carrera 3', 'Montería', '3004445566', 8.7500, -75.8800, '08:00 AM - 09:00 PM'),
(3, 'Sede Sur - Granja', 'Diag 14 Transversal 5', 'Montería', '3005556677', 8.7400, -75.8900, '09:00 AM - 10:00 PM'),
(4, 'Sede Cereté Plaza', 'Parque Principal', 'Cereté', '3008889900', 8.8800, -75.7900, '09:00 AM - 08:00 PM'),
(5, 'Sede C.C. Nuestro', 'Local 204 - Plazoleta', 'Montería', '3001230000', 8.7700, -75.8700, '11:00 AM - 10:00 PM');

-- 3. PROVEEDORES (Diversos)
INSERT IGNORE INTO proveedor (proveedor_id, nombre, contacto, telefono, email) VALUES
(1, 'Coca-Cola FEMSA', 'Roberto Distribuidor', '3100001111', 'pedidos@femsa.com.co'),
(2, 'Carnes del Sinú', 'Ganadero Jose', '3120002222', 'ventas@carnessinu.com'),
(3, 'Panadería La Mejor', 'Doña Rosa', '3130003333', 'rosa@panaderia.com'),
(4, 'Salsas y Aderezos SAS', 'Camilo Ventas', '3140004444', 'contacto@salsas.com'),
(5, 'Distribuidora de Lácteos', 'Pedro Leche', '3150005555', 'pedidos@lacteos.com'),
(6, 'Verduras del Campo', 'Maria Huerto', '3160006666', 'campo@fresco.com'),
(7, 'Empaques y Desechables', 'Luis Plástico', '3170007777', 'ventas@empaques.com');

-- 4. PRODUCTOS (Menú amplio)
INSERT IGNORE INTO producto (producto_id, codigo, nombre, descripcion, precio_base, tipo, activo) VALUES
-- Comidas
(1, 'HAM-001', 'Hamburguesa Clásica', 'Carne 150g, queso, vegetales', 18000.00, 'COMIDA', 1),
(2, 'HAM-002', 'Hamburguesa Doble', 'Doble carne, doble queso, tocineta', 26000.00, 'COMIDA', 1),
(3, 'HAM-003', 'Hamburguesa de Pollo', 'Pechuga apanada, queso, miel mostaza', 20000.00, 'COMIDA', 1),
(4, 'PER-001', 'Perro Caliente Sencillo', 'Salchicha americana, ripio, salsas', 12000.00, 'COMIDA', 1),
(5, 'PER-002', 'Perro Suizo', 'Salchicha suiza, queso fundido, tocineta', 16000.00, 'COMIDA', 1),
(6, 'PZ-001', 'Pizza Personal Peperoni', 'Masa delgada, peperoni', 14000.00, 'COMIDA', 1),
(7, 'PZ-002', 'Pizza Personal Hawaiana', 'Jamón, piña, queso', 13000.00, 'COMIDA', 1),
(8, 'SAL-001', 'Salchipapa Tradicional', 'Papas, salchicha, queso costeño', 15000.00, 'COMIDA', 1),
(9, 'SAL-002', 'Salchipapa Especial', 'Papas, salchicha, butifarra, chorizo, queso', 22000.00, 'COMIDA', 1),
-- Bebidas
(10, 'BEB-001', 'Coca-Cola 400ml', 'Gaseosa fría', 5000.00, 'BEBIDA', 1),
(11, 'BEB-002', 'Manzana Postobón', 'Gaseosa fría', 5000.00, 'BEBIDA', 1),
(12, 'BEB-003', 'Limonada Natural', 'Jugo en agua', 6000.00, 'BEBIDA', 1),
(13, 'BEB-004', 'Jugo de Corozo', 'Jugo típico costeño', 7000.00, 'BEBIDA', 1),
(14, 'BEB-005', 'Cerveza Águila', 'Bien fría', 6000.00, 'BEBIDA', 1),
(15, 'BEB-006', 'Cerveza Club Colombia', 'Dorada o Roja', 8000.00, 'BEBIDA', 1),
(16, 'BEB-007', 'Agua Manantial', 'Sin gas', 3000.00, 'BEBIDA', 1),
-- Otros
(17, 'POS-001', 'Brownie con Helado', 'Postre caliente', 10000.00, 'OTRO', 1),
(18, 'ADI-001', 'Porción de Papas', '150g papas a la francesa', 6000.00, 'OTRO', 1),
(19, 'ADI-002', 'Adición de Queso', 'Lonja de queso extra', 3000.00, 'OTRO', 1),
(20, 'ADI-003', 'Adición de Tocineta', '2 tiras de tocineta', 4000.00, 'OTRO', 1);

-- 5. CLIENTES
INSERT IGNORE INTO cliente (cliente_id, nombre, telefono, email, direccion_default, fecha_registro) VALUES
(1, 'Cliente Mostrador', '0000000000', 'anonimo@pos.com', 'Local', NOW()),
(2, 'Juan Pueblo', '3001002030', 'juan@gmail.com', 'Barrio La Granja', NOW()),
(3, 'Luisa Lane', '3005006070', 'luisa@hotmail.com', 'Barrio El Recreo', NOW()),
(4, 'Pedro Picapiedra', '3009008070', 'pedro@yahoo.com', 'Barrio Cantaclaro', NOW()),
(5, 'Maria Antonieta', '3112223344', 'maria@gmail.com', 'Edificio Sexta Avenida', NOW()),
(6, 'Bruce Wayne', '3225550000', 'batman@gotham.com', 'Mansion Wayne', NOW()),
(7, 'Tony Stark', '3009998877', 'ironman@avengers.com', 'Torre Stark', NOW()),
(8, 'Natasha Romanoff', '3101112222', 'blackwidow@shield.com', 'Centro', NOW());

-- 6. EMPLEADOS
INSERT IGNORE INTO empleado (empleado_id, nombre, documento, telefono, email, rol, fecha_ingreso, activo, sede_id) VALUES
(1, 'Carlos Mesero', '1001', '300111', 'carlos@pos.com', 'MESERO', NOW(), 1, 1),
(2, 'Ana Cajera', '1002', '300222', 'ana@pos.com', 'CAJERO', NOW(), 1, 1),
(3, 'Pedro Cocina', '1003', '300333', 'pedro@pos.com', 'COCINERO', NOW(), 1, 2);

-- 7. PROMOCIONES
INSERT IGNORE INTO promocion (promo_id, codigo, descripcion, tipo_descuento, valor, inicio, fin, condiciones) VALUES
(1, 'BIENVENIDA10', '10% Descuento', 'PORCENTAJE', 10.00, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 'Primera compra'),
(2, 'FINDE', 'Bono $5.000', 'MONTO_FIJO', 5000.00, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 'Compras > 50k');

-- 8. INVENTARIO (Llenamos las sedes con stock variado)
-- Sede 1: Bien surtida
INSERT IGNORE INTO inventario (sede_id, producto_id, cantidad, unidad, fecha_ultimo_movimiento) SELECT 1, producto_id, 100, 'UNIDAD', NOW() FROM producto;
-- Sede 2: Stock normal
INSERT IGNORE INTO inventario (sede_id, producto_id, cantidad, unidad, fecha_ultimo_movimiento) SELECT 2, producto_id, 50, 'UNIDAD', NOW() FROM producto;
-- Sede 3: Poca mercancía (Para probar alertas de Bajo Stock en Dashboard)
INSERT IGNORE INTO inventario (sede_id, producto_id, cantidad, unidad, fecha_ultimo_movimiento) SELECT 3, producto_id, 5, 'UNIDAD', NOW() FROM producto;

-- ==============================================================
-- 9. HISTORIAL TRANSACCIONAL (VENTAS DE LOS ÚLTIMOS 7 DÍAS)
-- Esto alimentará las gráficas del Dashboard
-- ==============================================================

-- DÍA -6 (Hace 6 días)
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(101, 2, 1, 3, DATE_SUB(NOW(), INTERVAL 6 DAY), 45000.00, 'PAGADO', 'MESA');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1001, 101, 2, 1, 26000.00, 26000.00), (1002, 101, 1, 1, 18000.00, 18000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (201, 101, 45000.00, 'EFECTIVO', DATE_SUB(NOW(), INTERVAL 6 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (301, 101, 2, 'FAC-001', DATE_SUB(NOW(), INTERVAL 6 DAY), 45000.00, 'VIGENTE');

-- DÍA -5
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(102, 3, 1, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), 120000.00, 'PAGADO', 'DOMICILIO');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1003, 102, 9, 5, 22000.00, 110000.00), (1004, 102, 10, 2, 5000.00, 10000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (202, 102, 120000.00, 'NEQUI', DATE_SUB(NOW(), INTERVAL 5 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (302, 102, 3, 'FAC-002', DATE_SUB(NOW(), INTERVAL 5 DAY), 120000.00, 'VIGENTE');

-- DÍA -4 (Pocas ventas)
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(103, 1, 2, 4, DATE_SUB(NOW(), INTERVAL 4 DAY), 15000.00, 'PAGADO', 'LLEVAR');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1005, 103, 8, 1, 15000.00, 15000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (203, 103, 15000.00, 'EFECTIVO', DATE_SUB(NOW(), INTERVAL 4 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (303, 103, 1, 'FAC-003', DATE_SUB(NOW(), INTERVAL 4 DAY), 15000.00, 'VIGENTE');

-- DÍA -3 (Día bueno)
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(104, 6, 1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), 200000.00, 'PAGADO', 'MESA');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1006, 104, 2, 5, 26000.00, 130000.00), (1007, 104, 15, 10, 8000.00, 80000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (204, 104, 200000.00, 'TARJETA', DATE_SUB(NOW(), INTERVAL 3 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (304, 104, 6, 'FAC-004', DATE_SUB(NOW(), INTERVAL 3 DAY), 200000.00, 'VIGENTE');

-- DÍA -2
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(105, 5, 2, 4, DATE_SUB(NOW(), INTERVAL 2 DAY), 50000.00, 'PAGADO', 'DOMICILIO');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1008, 105, 3, 2, 20000.00, 40000.00), (1009, 105, 17, 1, 10000.00, 10000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (205, 105, 50000.00, 'NEQUI', DATE_SUB(NOW(), INTERVAL 2 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (305, 105, 5, 'FAC-005', DATE_SUB(NOW(), INTERVAL 2 DAY), 50000.00, 'VIGENTE');

-- DÍA -1 (Ayer)
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(106, 7, 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 85000.00, 'PAGADO', 'MESA');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1010, 106, 1, 3, 18000.00, 54000.00), (1011, 106, 10, 6, 5000.00, 30000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (206, 106, 85000.00, 'TARJETA', DATE_SUB(NOW(), INTERVAL 1 DAY), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (306, 106, 7, 'FAC-006', DATE_SUB(NOW(), INTERVAL 1 DAY), 85000.00, 'VIGENTE');

-- HOY
INSERT IGNORE INTO pedido (pedido_id, cliente_id, sede_id, usuario_caja_id, fecha_hora, total, estado, tipo) VALUES
(107, 8, 1, 3, NOW(), 26000.00, 'PAGADO', 'LLEVAR');
INSERT IGNORE INTO orderitem (orderitem_id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (1012, 107, 2, 1, 26000.00, 26000.00);
INSERT IGNORE INTO pago (pago_id, pedido_id, monto, metodo_pago, fecha_hora, estado_pago) VALUES (207, 107, 26000.00, 'EFECTIVO', NOW(), 'APROBADO');
INSERT IGNORE INTO factura (factura_id, pedido_id, cliente_id, numero_factura, fecha_emision, valor_total, estado) VALUES (307, 107, 8, 'FAC-007', NOW(), 26000.00, 'VIGENTE');