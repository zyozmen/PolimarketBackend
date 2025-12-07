# Script de Inicialización de Datos - data.sql

## Descripción

Este archivo SQL se ejecuta automáticamente al iniciar la aplicación Spring Boot y carga datos iniciales en la base de datos H2 en memoria.

## Configuración

### application.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create  # Crea las tablas desde cero en cada inicio
    defer-datasource-initialization: true  # Ejecuta data.sql después de crear las tablas
```

## Datos Cargados Automáticamente

### 🔐 Roles del Sistema
Se crean 6 roles base:
- **ADMIN**: Administrador del sistema con acceso completo
- **RRHH**: Recursos Humanos - Gestión de personal
- **VENTAS**: Departamento de ventas
- **BODEGA**: Gestión de bodega e inventario
- **PROVEEDORES**: Gestión de proveedores
- **ENTREGAS**: Gestión de entregas y logística

### 👥 Empleados de Prueba

#### 1. Administrador Principal
- **Usuario**: `admin`
- **Password**: `admin123`
- **Identificación**: 1234567890
- **Estado**: ACTIVO
- **Roles**: ADMIN, RRHH
- **Descripción**: Usuario administrador con acceso completo al sistema

#### 2. Empleado RRHH
- **Usuario**: `jperez`
- **Password**: `password123`
- **Identificación**: 9876543210
- **Estado**: ACTIVO
- **Roles**: RRHH
- **Descripción**: Juan Pérez - Empleado del departamento de Recursos Humanos

#### 3. Empleado de Ventas
- **Usuario**: `mlopez`
- **Password**: `ventas123`
- **Identificación**: 1122334455
- **Estado**: ACTIVO
- **Roles**: VENTAS
- **Descripción**: María López - Empleado del departamento de Ventas

#### 4. Usuario Inactivo (Para Pruebas)
- **Usuario**: `inactivo`
- **Password**: `test123`
- **Identificación**: 5544332211
- **Estado**: INACTIVO
- **Roles**: BODEGA
- **Descripción**: Usuario de prueba para validar restricciones de acceso

## Uso de los Datos

### Login con Usuario Admin
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "admin",
    "password": "w2Qd5CtcPGVRmnnpjV1ORg=="
  }'
```

**Nota**: La contraseña debe estar cifrada con AES-256. Los valores mostrados en data.sql ya están cifrados.

### Verificar Datos en H2 Console

1. Acceder a: http://localhost:8081/api/h2-console
2. Configuración:
   - **JDBC URL**: `jdbc:h2:mem:polimarketdb`
   - **User Name**: `sa`
   - **Password**: `sa`
3. Ejecutar queries:
```sql
-- Ver todos los empleados
SELECT * FROM PERSONA;

-- Ver todos los roles
SELECT * FROM ROL;

-- Ver asignación de roles
SELECT * FROM EMPLEADO_ROL;

-- Ver empleados con sus roles
SELECT p.usuario, p.nombre, p.apellido, p.estado, r.nombre as rol
FROM PERSONA p
JOIN EMPLEADO_ROL er ON p.identificacion = er.empleado_id
JOIN ROL r ON er.rol_id = r.rol_id;
```

## Generación de Contraseñas Cifradas

Si necesitas agregar más empleados o cambiar contraseñas, usa el generador:

```bash
mvn exec:java -Dexec.mainClass="com.polimarket.util.GeneradorPasswordsCifradas"
```

Este programa te mostrará las contraseñas cifradas que puedes copiar directamente en el archivo `data.sql`.

## Consideraciones Importantes

### ⚠️ Entorno de Desarrollo vs Producción

**Desarrollo (H2 en memoria)**:
- Los datos se cargan en cada inicio
- La base de datos se reinicia al apagar la aplicación
- Ideal para pruebas y desarrollo

**Producción (PostgreSQL, MySQL, etc.)**:
- NO usar `ddl-auto: create` (se borrarían todos los datos)
- Usar herramientas de migración como Flyway o Liquibase
- Cambiar a `ddl-auto: validate` o `none`
- Los datos iniciales solo deben cargarse una vez

### 🔒 Seguridad

1. **Contraseñas**: Todas las contraseñas están cifradas con AES-256
2. **Usuarios de Prueba**: En producción, eliminar o cambiar estos usuarios
3. **Clave de Cifrado**: La clave en `PasswordEncryptionUtil` debe ser única y segura en producción
4. **JWT Secret**: Cambiar el secreto JWT en `application.yml` para producción

## Modificar el Script

Para agregar más datos iniciales, edita el archivo:
```
src/main/resources/data.sql
```

### Ejemplo - Agregar un Nuevo Empleado:

```sql
-- 1. Generar contraseña cifrada primero con GeneradorPasswordsCifradas
-- 2. Insertar el empleado
INSERT INTO persona (identificacion, tipo_identificacion, nombre, apellido, usuario, password, estado, fecha_creacion, fecha_actualizacion)
VALUES (
    9988776655,
    'EMPLEADO',
    'Carlos',
    'Ramírez',
    'cramirez',
    'TU_PASSWORD_CIFRADO_AQUI==',
    'ACTIVO',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 3. Asignar rol(es)
INSERT INTO empleado_rol (empleado_id, rol_id) VALUES
(9988776655, 5); -- PROVEEDORES
```

## Troubleshooting

### El script no se ejecuta

**Problema**: Los datos no se cargan al iniciar.

**Solución**: Verificar que `defer-datasource-initialization: true` esté en `application.yml`

### Error de clave duplicada

**Problema**: `Unique index or primary key violation`

**Solución**: 
- Verificar que no haya IDs duplicados
- Limpiar la base de datos reiniciando la aplicación
- Si usas `ddl-auto: update`, cambiar a `create` temporalmente

### Contraseñas no funcionan

**Problema**: Login falla con credenciales correctas

**Solución**:
1. Verificar que la contraseña en data.sql esté cifrada
2. Generar nuevamente con `GeneradorPasswordsCifradas`
3. Verificar que la clave en `PasswordEncryptionUtil` no haya cambiado

## Archivos Relacionados

- **data.sql**: Este archivo - datos iniciales
- **GeneradorPasswordsCifradas.java**: Generador de contraseñas cifradas
- **PasswordEncryptionUtil.java**: Utilidad de cifrado AES-256
- **application.yml**: Configuración de la base de datos

## Logs

Al iniciar la aplicación, verás en los logs:

```
INFO  o.s.b.a.h.H2ConsoleAutoConfiguration : H2 console available at '/h2-console'
INFO  o.h.e.t.j.p.i.JtaPlatformInitiator  : HHH000489: No JTA platform available
INFO  j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory
```

Si hay errores en data.sql, aparecerán aquí claramente.

---

**Última actualización**: Diciembre 2025  
**Autor**: Sistema PoliMarket Backend
