# Core Bank Project

Este proyecto es un sistema de **Core Bancario** desarrollado con Spring Boot, diseñado para simular operaciones financieras reales bajo una arquitectura **Monolítica Modular** limpia y escalable.

## 🏗 Arquitectura

El sistema sigue el patrón de **Capas (Layered Architecture)** para separar responsabilidades:

1.  **Controller (API Layer)**: Recibe las peticiones HTTP (JSON) y usa **DTOs** (Request/Response) para comunicarse con el exterior.
2.  **Service (Business Layer)**: Contiene la lógica de negocio (validaciones, cálculos, orquestación). Usa **Mappers** para convertir DTOs a Entidades.
3.  **Repository (Data Layer)**: Interactúa con la base de datos (PostgreSQL) usando Spring Data JPA.
4.  **Entity (Domain Layer)**: Representa las tablas de la base de datos.

## 🚀 Módulos Actuales

### 1. Customer (Clientes)
Gestiona la información personal de los dueños de las cuentas.
*   **Entidad**: `Customer` (Nombre, Email, Fechas).
*   **Funcionalidad**: Registro y Consulta de perfil.
*   **DTOs**: `CreateCustomerRequest`, `CustomerResponse`.

### 2. Account (Cuentas)
Gestiona los productos financieros del cliente.
*   **Entidad**: `Account` (Número de cuenta, Saldo, Relación con Customer).
*   **Estado**: *En desarrollo (Entidad creada).*

## 🔐 Plan de Seguridad y Flujo de Usuario

El sistema implementará un flujo seguro de autenticación y autorización:

1.  **Registro (`/api/auth/register`)**:
    *   Crea el **Usuario** (Auth) y el **Cliente** (Datos personales) en una sola transacción.

2.  **Login (`/api/auth/login`)**:
    *   Valida credenciales (Email/Password) y emite un **Token JWT**.

3.  **Dashboard de Usuario**:
    *   El usuario consulta sus productos (`/api/customers/me/accounts`).
    *   **Lógica Inteligente**:
        *   Si tiene 1 cuenta ➔ Muestra saldo y movimientos.
        *   Si tiene varias ➔ Muestra lista para seleccionar.

## 🛠 Tecnologías
*   **Java 17**
*   **Spring Boot 3**
*   **Spring Data JPA**
*   **PostgreSQL**
*   **Lombok** & **Records** (Java 14+)
