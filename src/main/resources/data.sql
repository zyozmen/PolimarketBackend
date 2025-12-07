-- Script de inicialización de datos para H2
-- Se ejecuta automáticamente al iniciar la aplicación
-- Crea roles base y un empleado administrador de prueba

-- ============================================
-- INSERTAR ROLES BASE
-- ============================================
INSERT INTO rol (rol_id, nombre, descripcion) VALUES 
(1, 'ADMIN', 'Administrador del sistema con acceso completo'),
(2, 'RRHH', 'Recursos Humanos - Gestión de personal'),
(3, 'VENTAS', 'Departamento de ventas'),
(4, 'BODEGA', 'Gestión de bodega e inventario'),
(5, 'PROVEEDORES', 'Gestión de proveedores'),
(6, 'ENTREGAS', 'Gestión de entregas y logística');

-- ============================================
-- INSERTAR EMPLEADO ADMINISTRADOR DE PRUEBA
-- ============================================
-- Identificación: 1234567890
-- Usuario: admin
-- Password: admin123 (cifrada con AES-256)
-- Estado: ACTIVO
-- Roles: ADMIN, RRHH

INSERT INTO persona (identificacion, tipo_identificacion, nombre, apellido, usuario, password, estado, fecha_creacion, fecha_actualizacion)
VALUES (
    1234567890,
    'EMPLEADO',
    'Admin',
    'Sistema',
    'admin',
    'w2Qd5CtcPGVRmnnpjV1ORg==', -- Password cifrado: admin123
    'ACTIVO',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- ============================================
-- ASIGNAR ROLES AL EMPLEADO ADMIN
-- ============================================
INSERT INTO empleado_rol (empleado_id, rol_id) VALUES
(1234567890, 1), -- ADMIN
(1234567890, 2); -- RRHH

-- ============================================
-- INSERTAR EMPLEADO DE RRHH DE PRUEBA
-- ============================================
-- Identificación: 9876543210
-- Usuario: jperez
-- Password: password123 (cifrada con AES-256)
-- Estado: ACTIVO
-- Roles: RRHH

INSERT INTO persona (identificacion, tipo_identificacion, nombre, apellido, usuario, password, estado, fecha_creacion, fecha_actualizacion)
VALUES (
    9876543210,
    'EMPLEADO',
    'Juan',
    'Pérez',
    'jperez',
    'Z2ai9mu645ETPfT4zyJQoA==', -- Password cifrado: password123
    'ACTIVO',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Asignar rol RRHH
INSERT INTO empleado_rol (empleado_id, rol_id) VALUES
(9876543210, 2); -- RRHH

-- ============================================
-- INSERTAR EMPLEADO DE VENTAS DE PRUEBA
-- ============================================
-- Identificación: 1122334455
-- Usuario: mlopez
-- Password: ventas123 (cifrada con AES-256)
-- Estado: ACTIVO
-- Roles: VENTAS

INSERT INTO persona (identificacion, tipo_identificacion, nombre, apellido, usuario, password, estado, fecha_creacion, fecha_actualizacion)
VALUES (
    1122334455,
    'EMPLEADO',
    'María',
    'López',
    'mlopez',
    '38kAwDgUWcEcY3YrhGyShA==', -- Password cifrado: ventas123
    'ACTIVO',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Asignar rol VENTAS
INSERT INTO empleado_rol (empleado_id, rol_id) VALUES
(1122334455, 3); -- VENTAS

-- ============================================
-- INSERTAR EMPLEADO INACTIVO DE PRUEBA
-- ============================================
-- Útil para probar validaciones de estado
-- Identificación: 5544332211
-- Usuario: inactivo
-- Password: test123 (cifrada con AES-256)
-- Estado: INACTIVO

INSERT INTO persona (identificacion, tipo_identificacion, nombre, apellido, usuario, password, estado, fecha_creacion, fecha_actualizacion)
VALUES (
    5544332211,
    'EMPLEADO',
    'Usuario',
    'Inactivo',
    'inactivo',
    '6BdlLHJb4JgymaEFst0Tqw==', -- Password cifrado: test123
    'INACTIVO',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Asignar rol BODEGA
INSERT INTO empleado_rol (empleado_id, rol_id) VALUES
(5544332211, 4); -- BODEGA

-- ============================================
-- RESUMEN DE EMPLEADOS CREADOS
-- ============================================
-- 1. admin / admin123 - ADMIN, RRHH (ACTIVO)
-- 2. jperez / password123 - RRHH (ACTIVO)
-- 3. mlopez / ventas123 - VENTAS (ACTIVO)
-- 4. inactivo / test123 - BODEGA (INACTIVO)
--
-- IMPORTANTE: Las contraseñas mostradas son de ejemplo.
-- Para obtener contraseñas cifradas reales, ejecutar:
-- PasswordEncryptionUtil.encrypt("tu_password")
