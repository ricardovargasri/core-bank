# Core Bank Project

Este proyecto es un sistema de **Core Bancario** desarrollado con Spring Boot, diseñado para simular operaciones financieras reales bajo una arquitectura **Monolítica Modular** limpia y escalable.

## 🏗 Arquitectura

El sistema sigue el patrón de **Capas (Layered Architecture)** para separar responsabilidades:

1.  **Controller (API Layer)**: Recibe las peticiones HTTP (JSON) y usa **DTOs** (Request/Response) para comunicarse con el exterior.
2.  **Service (Business Layer)**: Contiene la lógica de negocio (validaciones, cálculos, orquestación). Usa **Mappers** para convertir DTOs a Entidades.
3.  **Repository (Data Layer)**: Interactúa con la base de datos (PostgreSQL) usando Spring Data JPA.
4.  **Entity (Domain Layer)**: Representa las tablas de la base de datos.
5.  **DTO (Data Transfer Object)**: Objetos para el transporte de datos entre capas.

## 📊 Flujo de Datos y Arquitectura

```mermaid
graph TD
    User((Usuario/API Client)) -->|JSON| Controller[Controllers]
    Controller -->|DTO| Service[Services]
    Service -->|Entities| Repository[Repositories]
    Repository -->|SQL| DB[(PostgreSQL)]
    
    Service -.->|Usa| Mapper[Mappers]
    CustomerService -->|Orquesta| AccountService[AccountService]
```

## 📖 Documentación Interactiva (Swagger)

El proyecto cuenta con **Swagger UI** para explorar y probar los endpoints de forma interactiva.

*   **URL**: `http://localhost:8080/swagger-ui.html`
*   **Vistazo rápido**: Podrás ver los modelos de datos (DTOs) y ejecutar peticiones directamente desde el navegador.

## 🚀 Módulos y Funcionalidades Logradas

### 1. Customer (Clientes)
*   **Identidad**: Gestiona información personal con campos obligatorios y únicos (`name`, `email`, `documentId`).
*   **API**: Implementación total con DTOs y Mappers.
*   **Integración**: Al registrar un cliente, se dispara automáticamente la creación de su primera cuenta.

### 2. Account (Cuentas)
*   **Tipos**: Soporte para `SAVINGS` y `CHECKING` mediante Enums.
*   **Seguridad y Reglas**:
    *   Generación de números de cuenta únicos de **4 dígitos**.
    *   **Restricción de tiempo**: 5 días para Ahorros y 24 horas para Corrientes.
*   **Transaccionalidad**: Uso de `@Transactional` para asegurar la integridad registro-cuenta.

### 3. User & Auth (Seguridad)
*   **RBAC (Role Based Access Control)**: Diferenciación entre `ADMIN`, `TELLER` y `USER`.
*   **JWT (JSON Web Token)**: Implementación completa de autenticación stateless con Access y Refresh Tokens.
*   **BCrypt**: Encriptación profesional de contraseñas.
*   **Relación User-Customer**: Separación de credenciales (User) y datos de negocio (Customer) con vinculación `1:1`.

### 4. Admin & Teller Dashboard (Nuevo 🚀)
*   **Búsqueda Global**: Localización de clientes por email, nombre o número de cuenta.
*   **Operaciones de Caja**:
    *   **Depósitos**: Interfaz exclusiva para Cajeros/Admins para realizar ingresos.
    *   **Historial**: Visualización completa de movimientos de cualquier cuenta.
*   **Gestión de Estados**:
    *   **Bloqueo/Desbloqueo**: Funcionalidad para congelar cuentas instantáneamente.
    *   **Seguridad Reforzada**: El backend rechaza *cualquier* transacción (entrante o saliente) en cuentas bloqueadas.
*   **UI Reactiva**:
    *   Indicadores visuales de estado (Grayscale para cuentas bloqueadas).
    *   Botones de acción contextuales.

## 🛡 Roadmap y Próximos Pasos

1.  **Reportes Avanzados**: Exportación de extractos en PDF.
2.  **Notificaciones**: Alertas por email ante movimientos sospechosos.

## 🛠 Tecnologías
*   **Java 17**
*   **Spring Boot 3**
*   **Spring Security**
*   **JJWT (JSON Web Token)**
*   **PostgreSQL**
*   **Lombok** & **Records**
