# API REST de Autenticación - PoliMarket Backend

## Descripción

API REST completa para autenticación y autorización de empleados en el sistema PoliMarket. Implementa autenticación basada en JWT (JSON Web Tokens) con cifrado AES-256 para contraseñas, proporcionando máxima seguridad.

## Características Principales

- ✅ Autenticación con JWT
- ✅ Cifrado AES-256 para contraseñas
- ✅ Tokens con expiración configurable
- ✅ Revocación de tokens (logout)
- ✅ Validación de tokens en tiempo real
- ✅ Manejo robusto de errores
- ✅ Pruebas unitarias con cobertura completa

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/polimarket/
│   │   ├── model/
│   │   │   └── Token.java              # Entidad Token JWT
│   │   ├── repository/
│   │   │   └── TokenRepository.java    # Repositorio de tokens
│   │   ├── service/
│   │   │   └── AuthService.java        # Lógica de autenticación
│   │   ├── controller/
│   │   │   └── AuthController.java     # API REST de autenticación
│   │   ├── dto/
│   │   │   ├── LoginRequestDTO.java    # DTO para request de login
│   │   │   └── LoginResponseDTO.java   # DTO para response de login
│   │   ├── util/
│   │   │   ├── JwtUtil.java            # Utilidades JWT
│   │   │   └── PasswordEncryptionUtil.java # Cifrado/descifrado
│   │   └── exception/
│   │       └── CredencialesInvalidasException.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/polimarket/
        ├── service/
        │   └── AuthServiceTest.java
        └── controller/
            └── AuthControllerTest.java
```

## Endpoints de la API

### Base URL
```
http://localhost:8081/api/auth
```

### 1. Login (Autenticar Empleado)
**POST** `/auth/login`

Autentica un empleado con sus credenciales y retorna un token JWT junto con el perfil completo del usuario.

**Request Body:**
```json
    {
        "usuario": "jperez",
        "password": "contraseña_cifrada_en_AES"
    }
```

**Notas importantes:**
- La contraseña debe venir **cifrada con AES-256** desde el cliente
- El servidor descifrará la contraseña para validarla
- La contraseña almacenada en BD también está cifrada

**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbXBsZWFkb0lkIjoxMjM0NTY3ODkwLCJyb2xlcyI6WyJBRE1JTiIsIlJSSEgiXSwic3ViIjoianBlcmV6IiwiaWF0IjoxNzA1MDc3MjA4LCJleHAiOjE3MDUxNjM2MDh9.signature",
    "expiracion": "2025-12-07T22:30:00",
    "perfil": {
        "identificacion": 1234567890,
        "tipoIdentificacion": "CEDULA",
        "nombre": "Juan",
        "apellido": "Pérez",
        "usuario": "jperez",
        "estado": "ACTIVO",
        "roles": ["ADMIN", "RRHH"],
        "fechaCreacion": "2025-12-06T19:30:00",
        "fechaActualizacion": "2025-12-06T19:30:00"
    }
}
```

**Errores Posibles:**
- `400 Bad Request`: Datos inválidos (usuario o contraseña vacíos)
- `401 Unauthorized`: Credenciales incorrectas o empleado inactivo
- `500 Internal Server Error`: Error en el servidor

---

### 2. Logout (Revocar Token)
**POST** `/auth/logout`

Revoca un token JWT (cierra sesión). El token queda inactivo y no se puede usar nuevamente.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
Sin contenido

---

### 3. Validar Token
**GET** `/auth/validate`

Valida si un token JWT es válido y está activo.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
true
```

o

```json
false
```

---

### 4. Health Check
**GET** `/auth/health`

Verifica que el servicio de autenticación está disponible.

**Response (200 OK):**
```
Servicio de Autenticación disponible
```

---

## Modelo de Datos

### Token
```java
{
    "id": Long,
    "valor": String,           // Token JWT
    "expiracion": LocalDateTime,
    "empleadoId": Long,
    "activo": Boolean,
    "fechaCreacion": LocalDateTime
}
```

---

## Seguridad

### Cifrado de Contraseñas
- **Algoritmo**: AES-256
- **Implementación**: Simétrica con clave secreta
- Las contraseñas se cifran tanto en cliente como en servidor
- El servidor descifra para comparar en texto plano

### Tokens JWT
- **Algoritmo de firma**: HMAC-SHA256
- **Expiración**: 24 horas (configurable)
- **Claims incluidos**:
  - `empleadoId`: ID del empleado
  - `roles`: Lista de roles del empleado
  - `sub`: Nombre de usuario
  - `iat`: Fecha de emisión
  - `exp`: Fecha de expiración

### Buenas Prácticas Implementadas
1. Tokens almacenados en BD para control de revocación
2. Desactivación de tokens anteriores en cada login
3. Validación de estado del empleado (solo ACTIVO puede autenticarse)
4. Logging de intentos de autenticación
5. Mensajes de error genéricos para evitar enumeración de usuarios

---

## Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Credenciales inválidas |
| 500 | Internal Server Error - Error del servidor |

---

## Formato de Errores

Todos los errores siguen este formato:

```json
{
    "codigo": 401,
    "mensaje": "Credenciales Inválidas",
    "detalles": "Usuario o contraseña incorrectos",
    "timestamp": 1765077208871
}
```

---

## Pruebas Unitarias

El proyecto incluye pruebas unitarias comprehensivas con cobertura completa.

### Ejecutar todas las pruebas:
```bash
mvn test
```

### Ejecutar pruebas específicas:
```bash
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=AuthControllerTest
```

### Cobertura de pruebas:
- **AuthService**: 15 casos de prueba
  - Autenticación exitosa
  - Usuario no encontrado
  - Empleado inactivo
  - Contraseña incorrecta
  - Validaciones de datos de entrada
  - Validación de tokens
  - Revocación de tokens
  - Manejo de errores de cifrado

- **AuthController**: 12 casos de prueba
  - Login exitoso
  - Credenciales inválidas
  - Datos no válidos
  - Logout
  - Validación de tokens
  - Health check
  - Múltiples roles
  - Errores internos

---

## Configuración

### application.yml
```yaml
# Configuración JWT
jwt:
  secret: polimarket-secret-key-super-secure-for-jwt-tokens-2025
  expiration: 86400000 # 24 horas en milisegundos
```

### Variables de Entorno (Recomendado para producción)
```bash
JWT_SECRET=tu-clave-secreta-super-segura
JWT_EXPIRATION=86400000
```

---

## Ejemplo de Uso con cURL

### 1. Login:
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "jperez",
    "password": "7x3K9mP2qL..."
  }'
```

### 2. Validar Token:
```bash
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Logout:
```bash
curl -X POST http://localhost:8081/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Ejemplo de Uso con JavaScript (Cliente)

### Cifrar contraseña en cliente:
```javascript
// Usando CryptoJS
const CryptoJS = require('crypto-js');

function encryptPassword(password) {
    const key = 'PoliMarket2025SecurePasswordKey!';
    const encrypted = CryptoJS.AES.encrypt(password, key);
    return encrypted.toString();
}

// Login
async function login(usuario, password) {
    const encryptedPassword = encryptPassword(password);
    
    const response = await fetch('http://localhost:8081/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            usuario: usuario,
            password: encryptedPassword
        })
    });
    
    const data = await response.json();
    
    if (response.ok) {
        // Guardar token
        localStorage.setItem('token', data.token);
        localStorage.setItem('perfil', JSON.stringify(data.perfil));
        return data;
    } else {
        throw new Error(data.detalles);
    }
}

// Usar token en requests subsecuentes
async function apiCallWithAuth(url) {
    const token = localStorage.getItem('token');
    
    const response = await fetch(url, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    return response.json();
}
```

---

## Flujo de Autenticación

```
1. Cliente cifra la contraseña con AES-256
2. Cliente envía usuario + contraseña cifrada al endpoint /auth/login
3. Servidor busca el empleado por usuario
4. Servidor verifica que el empleado esté ACTIVO
5. Servidor descifra ambas contraseñas y las compara
6. Si coinciden:
   a. Servidor desactiva tokens anteriores del empleado
   b. Servidor genera un nuevo token JWT
   c. Servidor guarda el token en BD con estado activo
   d. Servidor retorna token + perfil completo del empleado
7. Cliente guarda el token
8. Cliente incluye el token en header Authorization de requests subsecuentes
9. Para logout, cliente envía token al endpoint /auth/logout
10. Servidor marca el token como inactivo en BD
```

---

## Integración con EmpleadoService

El servicio de autenticación se integra perfectamente con el sistema de gestión de empleados existente:

- Usa el mismo `EmpleadoRepository` para buscar usuarios
- Valida el estado del empleado antes de autenticar
- Retorna el perfil completo del empleado con todos sus roles
- Mantiene coherencia en el manejo de excepciones

---

## Tecnologías Utilizadas

- **Java 21 LTS**
- **Spring Boot 3.4.1**
- **Spring Data JPA**
- **H2 Database**
- **JWT (jjwt 0.12.5)**
- **Lombok**
- **JUnit 5**
- **Mockito**
- **Maven**

---

## Consideraciones de Producción

### Seguridad
1. Cambiar `jwt.secret` por una clave robusta única
2. Usar variables de entorno para secretos
3. Implementar HTTPS en producción
4. Considerar refresh tokens para sesiones largas
5. Implementar rate limiting en endpoints de auth

### Monitoreo
1. Log de intentos de login fallidos
2. Alertas por múltiples intentos fallidos
3. Métricas de tokens generados/revocados
4. Monitoreo de tokens expirados

### Mantenimiento
1. Tarea programada para limpiar tokens expirados de BD
2. Rotación periódica de claves JWT
3. Auditoría de accesos

---

## Autor

Generado automáticamente para PoliMarket Backend - Diciembre 2025

---

## Licencia

Proyecto propietario de PoliMarket
