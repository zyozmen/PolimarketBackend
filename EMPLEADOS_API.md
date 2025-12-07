# API REST de Gestión de Empleados - PoliMarket Backend

## Descripción

API REST completa para la gestión de empleados en el sistema PoliMarket. Implementa operaciones CRUD sobre la entidad Empleado con validación de datos, manejo robusto de errores y pruebas unitarias.

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/polimarket/
│   │   ├── model/              # Entidades JPA
│   │   │   ├── Persona.java            # Clase abstracta base
│   │   │   ├── Empleado.java           # Entidad principal
│   │   │   ├── Rol.java                # Roles del sistema
│   │   │   ├── EstadoEmpleado.java     # Estados posibles
│   │   │   └── TipoRol.java            # Tipos de roles disponibles
│   │   ├── repository/         # Data Access Layer
│   │   │   ├── EmpleadoRepository.java
│   │   │   └── RolRepository.java
│   │   ├── service/            # Business Logic Layer
│   │   │   └── EmpleadoService.java
│   │   ├── controller/         # REST API Controllers
│   │   │   └── EmpleadoController.java
│   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── EmpleadoDTO.java
│   │   │   ├── CrearActualizarEmpleadoDTO.java
│   │   │   └── ErrorDTO.java
│   │   ├── exception/          # Excepciones personalizadas
│   │   │   ├── RecursoNoEncontradoException.java
│   │   │   ├── DatosNoValidosException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── config/             # Configuración de la aplicación
│   │   │   └── SecurityConfig.java
│   │   └── PolimarketApplication.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/polimarket/
        ├── service/
        │   └── EmpleadoServiceTest.java
        └── controller/
            └── EmpleadoControllerTest.java
```

## Endpoints de la API

### Base URL
```
http://localhost:8081/api/rrhh
```

### 1. Crear Empleado
**POST** `/api/rrhh`

Crea un nuevo empleado en el sistema.

**Request Body:**
```json
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

**Response (201 Created):**
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

**Errores Posibles:**
- `400 Bad Request`: Datos inválidos
- `409 Conflict`: Usuario ya existe

---

### 2. Obtener Todos los Empleados
**GET** `/api/rrhh`

Obtiene la lista completa de todos los empleados.

**Response (200 OK):**
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
    }
]
```

---

### 3. Obtener Empleado por ID
**GET** `/api/rrhh/:id`

Obtiene un empleado específico por su identificación.

**Parámetros:**
- `id` (Long): Identificación del empleado

**Response (200 OK):**
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

**Errores Posibles:**
- `404 Not Found`: Empleado no existe

---

### 4. Actualizar Empleado
**PUT** `/api/rrhh/:username`

Actualiza un empleado existente.

**Parámetros:**
- `username` (String): Nombre de usuario del empleado

**Request Body:**
```json
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

**Response (200 OK):**
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

**Errores Posibles:**
- `404 Not Found`: Empleado no existe
- `400 Bad Request`: Datos inválidos

---

### 5. Eliminar Empleado
**DELETE** `/api/rrhh/:id`

Elimina un empleado del sistema.

**Parámetros:**
- `id` (Long): Identificación del empleado

**Response (204 No Content)**

**Errores Posibles:**
- `404 Not Found`: Empleado no existe

---

### 6. Health Check
**GET** `/api/rrhh/health`

Verifica que el servicio de empleados está disponible.

**Response (200 OK):**
```
Servicio de Empleados disponible
```

---

## Estados de Empleado

Los siguientes estados son válidos:
- `ACTIVO`: Empleado activo en el sistema
- `INACTIVO`: Empleado inactivo
- `SUSPENDIDO`: Empleado suspendido
- `RETIRADO`: Empleado retirado

---

## Roles Disponibles

Los siguientes roles pueden ser asignados a un empleado:
- `ADMIN`: Administrador del sistema
- `RRHH`: Recursos Humanos
- `VENTAS`: Departamento de Ventas
- `BODEGA`: Bodega/Almacén
- `PROVEEDORES`: Gestión de Proveedores
- `ENTREGAS`: Entregas

---

## Validaciones

### Empleado
- **Identificación**: Debe ser un número positivo
- **Tipo de Identificación**: No puede estar vacío
- **Nombre**: No puede estar vacío
- **Apellido**: No puede estar vacío
- **Usuario**: No puede estar vacío y debe ser único
- **Contraseña**: Mínimo 6 caracteres
- **Estado**: Debe ser válido (ACTIVO, INACTIVO, SUSPENDIDO, RETIRADO)
- **Roles**: Deben ser válidos

---

## Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Solicitud exitosa sin contenido |
| 400 | Bad Request - Datos inválidos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## Formato de Errores

Todos los errores siguen este formato:

```json
{
    "codigo": 404,
    "mensaje": "Recurso No Encontrado",
    "detalles": "Empleado con identificación 9999 no encontrado",
    "timestamp": 1702935000000
}
```

---

## Pruebas Unitarias

El proyecto incluye pruebas unitarias comprehensivas usando JUnit 5 y Mockito.

### Ejecutar todas las pruebas:
```bash
mvn test
```

### Ejecutar pruebas específicas:
```bash
mvn test -Dtest=EmpleadoServiceTest
mvn test -Dtest=EmpleadoControllerTest
```

### Cobertura de pruebas:
- **EmpleadoService**: 12 casos de prueba
- **EmpleadoController**: 9 casos de prueba

Pruebas incluidas:
- Creación exitosa de empleados
- Validación de datos
- Búsqueda por ID
- Búsqueda por usuario
- Actualización de empleados
- Eliminación de empleados
- Manejo de excepciones
- Respuestas HTTP correctas

---

## Seguridad

- Las contraseñas se codifican con BCrypt
- Acceso CORS habilitado para desarrollo
- Endpoints públicos: `/api/rrhh/**`, `/api/health`
- CSRF deshabilitado para APIs REST

---

## Configuración de Base de Datos

El proyecto utiliza **H2 Database** en memoria para desarrollo:

- **URL**: `jdbc:h2:mem:polimarketdb`
- **Usuario**: `sa`
- **Contraseña**: (vacía)
- **Consola**: `http://localhost:8081/api/h2-console`

---

## Ejecución

### Compilar:
```bash
mvn clean compile
```

### Ejecutar:
```bash
mvn spring-boot:run
```

### Empaquetar:
```bash
mvn clean package
```

---

## Ejemplo de Uso con cURL

### Crear empleado:
```bash
curl -X POST http://localhost:8081/api/rrhh \
  -H "Content-Type: application/json" \
  -d '{
    "identificacion": 1234567890,
    "tipoIdentificacion": "CEDULA",
    "nombre": "Juan",
    "apellido": "Pérez",
    "usuario": "jperez",
    "password": "password123",
    "estado": "ACTIVO",
    "roles": ["RRHH"]
  }'
```

### Obtener todos los empleados:
```bash
curl http://localhost:8081/api/rrhh
```

### Obtener empleado por ID:
```bash
curl http://localhost:8081/api/rrhh/1234567890
```

### Actualizar empleado:
```bash
curl -X PUT http://localhost:8081/api/rrhh/jperez \
  -H "Content-Type: application/json" \
  -d '{
    "identificacion": 1234567890,
    "tipoIdentificacion": "CEDULA",
    "nombre": "Juan Carlos",
    "apellido": "Pérez López",
    "usuario": "jperez",
    "password": "password123",
    "estado": "ACTIVO",
    "roles": ["RRHH", "VENTAS"]
  }'
```

### Eliminar empleado:
```bash
curl -X DELETE http://localhost:8081/api/rrhh/1234567890
```

---

## Tecnologías Utilizadas

- **Java 21 LTS**
- **Spring Boot 3.4.1**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database**
- **Lombok**
- **JUnit 5**
- **Mockito**
- **Maven**

---

## Autor

Generado automáticamente para PoliMarket Backend - Diciembre 2025

---

## Licencia

Proyecto propietario de PoliMarket
