# Guía de Uso - API REST de Empleados

## Resumen Ejecutivo

Se ha implementado una API REST completa para la gestión de empleados con:

✅ **Modelo de datos** completo basado en tu diagrama  
✅ **5 endpoints CRUD** funcionando correctamente  
✅ **22 pruebas unitarias** pasando al 100%  
✅ **Manejo robusto de errores** con códigos HTTP correctos  
✅ **Base de datos H2** para desarrollo  
✅ **Código bien estructurado y documentado**  

---

## Estructura Creada

```
src/main/java/com/polimarket/
├── model/
│   ├── Persona.java              # Clase abstracta base
│   ├── Empleado.java             # Entidad principal heredando de Persona
│   ├── Rol.java                  # Roles disponibles
│   ├── EstadoEmpleado.java       # Enum de estados
│   └── TipoRol.java              # Enum de tipos de roles
├── controller/
│   └── EmpleadoController.java    # REST endpoints
├── service/
│   └── EmpleadoService.java       # Lógica de negocio
├── repository/
│   ├── EmpleadoRepository.java
│   └── RolRepository.java
├── dto/
│   ├── EmpleadoDTO.java          # DTO para respuestas
│   ├── CrearActualizarEmpleadoDTO.java  # DTO para solicitudes
│   └── ErrorDTO.java             # DTO para errores
├── exception/
│   ├── RecursoNoEncontradoException.java
│   ├── DatosNoValidosException.java
│   └── GlobalExceptionHandler.java
└── config/
    └── SecurityConfig.java       # Configuración de seguridad
```

---

## Compilación y Ejecución

### 1. Compilar el Proyecto
```bash
mvn clean compile
```

### 2. Ejecutar las Pruebas
```bash
mvn test
```

**Resultado esperado:**
```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

### 3. Empaquetar la Aplicación
```bash
mvn clean package
```

### 4. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

O directamente con Java:
```bash
java -jar target/polimarket-backend-1.0.0.jar
```

La aplicación estará disponible en: **http://localhost:8081/api**

---

## Endpoints Disponibles

### BASE URL
```
http://localhost:8081/api/rrhh
```

### 1️⃣ CREAR EMPLEADO
```
POST /api/rrhh
Content-Type: application/json

{
  "identificacion": 1234567890,
  "tipoIdentificacion": "CEDULA",
  "nombre": "Juan",
  "apellido": "Pérez",
  "usuario": "jperez",
  "password": "password123",
  "estado": "ACTIVO",
  "roles": ["RRHH", "VENTAS"]
}
```

**Respuesta (201 Created):**
```json
{
  "identificacion": 1234567890,
  "tipoIdentificacion": "CEDULA",
  "nombre": "Juan",
  "apellido": "Pérez",
  "usuario": "jperez",
  "estado": "ACTIVO",
  "roles": ["RRHH", "VENTAS"],
  "fechaCreacion": "2025-12-06T19:30:00",
  "fechaActualizacion": "2025-12-06T19:30:00"
}
```

---

### 2️⃣ OBTENER TODOS LOS EMPLEADOS
```
GET /api/rrhh
```

**Respuesta (200 OK):**
```json
[
  {
    "identificacion": 1234567890,
    "tipoIdentificacion": "CEDULA",
    "nombre": "Juan",
    "apellido": "Pérez",
    "usuario": "jperez",
    "estado": "ACTIVO",
    "roles": ["RRHH", "VENTAS"],
    "fechaCreacion": "2025-12-06T19:30:00",
    "fechaActualizacion": "2025-12-06T19:30:00"
  },
  {
    "identificacion": 9876543210,
    "tipoIdentificacion": "CEDULA",
    "nombre": "María",
    "apellido": "García",
    "usuario": "mgarcia",
    "estado": "ACTIVO",
    "roles": ["VENTAS"],
    "fechaCreacion": "2025-12-06T19:31:00",
    "fechaActualizacion": "2025-12-06T19:31:00"
  }
]
```

---

### 3️⃣ OBTENER EMPLEADO POR ID
```
GET /api/rrhh/:id
```

**Ejemplo:**
```
GET /api/rrhh/1234567890
```

**Respuesta (200 OK):**
```json
{
  "identificacion": 1234567890,
  "tipoIdentificacion": "CEDULA",
  "nombre": "Juan",
  "apellido": "Pérez",
  "usuario": "jperez",
  "estado": "ACTIVO",
  "roles": ["RRHH", "VENTAS"],
  "fechaCreacion": "2025-12-06T19:30:00",
  "fechaActualizacion": "2025-12-06T19:30:00"
}
```

**Error (404 Not Found):**
```json
{
  "codigo": 404,
  "mensaje": "Recurso No Encontrado",
  "detalles": "Empleado con identificación 9999 no encontrado",
  "timestamp": 1702935000000
}
```

---

### 4️⃣ ACTUALIZAR EMPLEADO
```
PUT /api/rrhh/:username
Content-Type: application/json

{
  "identificacion": 1234567890,
  "tipoIdentificacion": "CEDULA",
  "nombre": "Juan Carlos",
  "apellido": "Pérez López",
  "usuario": "jperez",
  "password": "nuevoPassword123",
  "estado": "ACTIVO",
  "roles": ["RRHH"]
}
```

**Ejemplo:**
```
PUT /api/rrhh/jperez
```

**Respuesta (200 OK):**
```json
{
  "identificacion": 1234567890,
  "tipoIdentificacion": "CEDULA",
  "nombre": "Juan Carlos",
  "apellido": "Pérez López",
  "usuario": "jperez",
  "estado": "ACTIVO",
  "roles": ["RRHH"],
  "fechaCreacion": "2025-12-06T19:30:00",
  "fechaActualizacion": "2025-12-06T19:35:00"
}
```

---

### 5️⃣ ELIMINAR EMPLEADO
```
DELETE /api/rrhh/:id
```

**Ejemplo:**
```
DELETE /api/rrhh/1234567890
```

**Respuesta (204 No Content)** - Sin cuerpo

---

## Ejemplos con cURL

### Crear un empleado
```bash
curl -X POST http://localhost:8081/api/rrhh \
  -H "Content-Type: application/json" \
  -d '{
    "identificacion": 1111111111,
    "tipoIdentificacion": "CEDULA",
    "nombre": "Carlos",
    "apellido": "López",
    "usuario": "clopez",
    "password": "password456",
    "estado": "ACTIVO",
    "roles": ["ADMIN"]
  }'
```

### Obtener todos los empleados
```bash
curl http://localhost:8081/api/rrhh
```

### Obtener empleado específico
```bash
curl http://localhost:8081/api/rrhh/1111111111
```

### Actualizar empleado
```bash
curl -X PUT http://localhost:8081/api/rrhh/clopez \
  -H "Content-Type: application/json" \
  -d '{
    "identificacion": 1111111111,
    "tipoIdentificacion": "CEDULA",
    "nombre": "Carlos Andrés",
    "apellido": "López García",
    "usuario": "clopez",
    "password": "password456",
    "estado": "ACTIVO",
    "roles": ["ADMIN", "RRHH"]
  }'
```

### Eliminar empleado
```bash
curl -X DELETE http://localhost:8081/api/rrhh/1111111111
```

---

## Validaciones Implementadas

### Validación de Datos (400 Bad Request)

```json
{
  "codigo": 400,
  "mensaje": "Datos No Válidos",
  "detalles": "La identificación debe ser un número positivo",
  "timestamp": 1702935000000
}
```

**Campos validados:**

| Campo | Regla |
|-------|-------|
| Identificación | Debe ser > 0 |
| Tipo de Identificación | No puede estar vacío |
| Nombre | No puede estar vacío |
| Apellido | No puede estar vacío |
| Usuario | No puede estar vacío, debe ser único |
| Contraseña | Mínimo 6 caracteres |
| Estado | ACTIVO, INACTIVO, SUSPENDIDO o RETIRADO |
| Roles | Deben ser válidos |

---

## Estados y Roles Disponibles

### Estados de Empleado
- `ACTIVO`
- `INACTIVO`
- `SUSPENDIDO`
- `RETIRADO`

### Roles Disponibles
- `ADMIN` - Administrador del sistema
- `RRHH` - Recursos Humanos
- `VENTAS` - Departamento de Ventas
- `BODEGA` - Bodega/Almacén
- `PROVEEDORES` - Gestión de Proveedores
- `ENTREGAS` - Entregas

---

## Pruebas Unitarias

### Pruebas del Servicio (13 tests)
- ✅ Crear empleado exitosamente
- ✅ Crear empleado con usuario duplicado
- ✅ Crear empleado con datos inválidos
- ✅ Validación de identificación
- ✅ Validación de contraseña
- ✅ Validación de estado
- ✅ Obtener todos los empleados
- ✅ Obtener empleado por ID
- ✅ Obtener empleado no encontrado
- ✅ Actualizar empleado
- ✅ Actualizar empleado no encontrado
- ✅ Eliminar empleado
- ✅ Eliminar empleado no encontrado

### Pruebas del Controlador (9 tests)
- ✅ Crear empleado (201 Created)
- ✅ Obtener todos (200 OK)
- ✅ Obtener por ID (200 OK)
- ✅ Obtener ID no encontrado (404 Not Found)
- ✅ Actualizar empleado (200 OK)
- ✅ Actualizar no encontrado (404 Not Found)
- ✅ Eliminar empleado (204 No Content)
- ✅ Eliminar no encontrado (404 Not Found)
- ✅ Health endpoint (200 OK)

**Resultado: 22/22 tests pasando ✅**

---

## Códigos de Respuesta HTTP

| Código | Significado | Caso de Uso |
|--------|-------------|------------|
| 200 | OK | GET, PUT exitosos |
| 201 | Created | POST exitoso |
| 204 | No Content | DELETE exitoso |
| 400 | Bad Request | Datos inválidos |
| 404 | Not Found | Recurso no existe |
| 500 | Server Error | Error del servidor |

---

## Configuración de Base de Datos

**H2 Database (En Memoria)**
- URL: `jdbc:h2:mem:polimarketdb`
- Usuario: `sa`
- Contraseña: (vacía)
- Consola: http://localhost:8081/api/h2-console

---

## Seguridad

- ✅ Contraseñas codificadas con BCrypt
- ✅ CORS habilitado para desarrollo
- ✅ Endpoints `/api/rrhh/**` son públicos
- ✅ CSRF deshabilitado para APIs REST

---

## Características Principales

### 1. Herencia de Entidades
```
Persona (clase abstracta)
    ↑
    |
    Empleado (hereda de Persona)
```

### 2. Relaciones
```
Empleado ←→ Rol (Relación Many-to-Many)
```

### 3. Timestamps Automáticos
- `fechaCreacion`: Se establece automáticamente al crear
- `fechaActualizacion`: Se actualiza automáticamente en cada cambio

### 4. DTOs para Separación de Responsabilidades
- `EmpleadoDTO`: Para respuestas de la API
- `CrearActualizarEmpleadoDTO`: Para solicitudes POST/PUT

---

## Próximos Pasos Opcionales

1. **Implementar autenticación real** (JWT)
2. **Agregar paginación** a GET /api/rrhh
3. **Implementar búsqueda/filtrado** por nombre, usuario, rol
4. **Hashear contraseñas** en la base de datos
5. **Agregar auditoría** de cambios
6. **Migrar a PostgreSQL** para producción
7. **Agregar documentación Swagger/OpenAPI**

---

## Soporte

Para cualquier duda sobre la estructura o implementación, todos los archivos están bien documentados con comentarios detallados.

**Documentación completa:** Ver `EMPLEADOS_API.md`

---

Última actualización: Diciembre 6, 2025
