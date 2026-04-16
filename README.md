# 🌱 Sena Empresa Backend

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Dokploy](https://img.shields.io/badge/Dokploy-000000?style=for-the-badge&logo=vercel&logoColor=white)

Este proyecto es la **API REST** principal de Sena Empresa, construida con Spring Boot y Java 21. El sistema nace como una modernización e integración del proyecto anterior (desarrollado en PHP) para estandarizar de forma escalable y segura los procesos del Complejo Tecnológico Agroindustrial, Pecuario y Turístico del SENA.

## 👨‍💻 Información del Proyecto y Desarrollador

| Rol / Atributo | Detalle |
|---|---|
| **Desarrollador** | Yeinz Orrego |
| **Programa** | Tecnología en Análisis y Desarrollo de Software (SENA) |
| **Nivel** | Último semestre |
| **Propósito** | Proyecto desarrollado como requisito de graduación |

## 🎯 Objetivo y Contexto del Sistema

El ecosistema busca conectar y automatizar la gestión administrativa y comercial de la producción agrícola y avícola (huevos, plátanos, gallinas). Este backend provee las bases tecnológicas para soportar el ciclo de vida de los productos: desde su registro inicial en la plantación, pasando por su traslado al inventario, hasta llegar al punto de venta y su posterior facturación.

### 🌟 Funcionalidades Principales y Proyección

1. **Gestión de Personal y Control de Acceso Seguro (Autenticación)**
   - El sistema emplea **JWT (JSON Web Tokens)** para manejar las sesiones de los empleados de Sena Empresa, determinando rigurosamente a qué funcionalidades (rutas protegidas) tiene acceso cada usuario según su rol (Administrador, Punto de Venta, Encargado de Plantación).
   - *Flujo Tecnológico:* Al momento del `Login`, la API verifica credenciales y despacha un token efímero que el cliente (Frontend/Móvil) deberá enviar en cada nueva petición, manteniendo así una comunicación sin estado pero validada y asilada.

2. **Inventario Transaccional (Sistema Dual)**
   - **Producción / Plantación:** Control del insumo base antes de su transporte al canal comercial.
   - **Punto Venta:** Gestión exacta del stock comercial que se actualiza o debita automáticamente a medida que aumentan las ventas, controlando costos unitarios y existencias.

3. **Módulo Operativo de Ventas**
   - El sistema proporciona la lógica transaccional necesaria para que los cajeros o encargados puedan generar e imprimir ventas, modificando automáticamente las cantidades globales de inventario sin intervención manual para mantener una cuadratura financiera impecable.

---

## ⚙️ Integración Técnica y Arquitectura Backend

El proyecto está diseñado bajo un enfoque fuertemente funcional, pero estructurado para soportar altos volúmenes de interacciones (N-Tier Architecture), aislando la lógica de negocio puramente de la recepción de datos web.

### Formato Retornado Estandarizado (JSON)
Para facilitar la integración con aplicaciones frontend web o móviles, y garantizar previsibilidad, la API intercepta **todas las respuestas exitosas** unificándolas bajo un único formato o envoltorio. Esto evita que los desarrolladores cliente manejen estructuras inconsistentes:

```json
{
    "response": {
        "length": 1,
        "statusCode": 200,
        "body": {
            // ... aquí viaja la respuesta esperada
        }
    }
}
```
*Nota: Las excepciones de negocio controladas o errores de validación de los modelos no son envueltos aquí; fluyen en un formato directo a través de un `GlobalExceptionHandler` con el status HTTP real del error y su mensaje descriptivo oportuno.*

### Aislamiento por Capas y Seguridad (DTOs)
Por seguridad y consistencia en el diseño, ninguna entidad directa de base de datos se le presenta al usuario final. Todas las interacciones de los *Controladores (Endpoints)* están aisladas a través de **Data Transfer Objects (DTOs)**, con apoyo de la librería **MapStruct** para las conversiones bidireccionales, protegiendo las vulnerabilidades internas y centralizando la validación estricta de las entradas antes de llegar a la lógica profunda de los *Servicios*.

- **DTOs de Entrada (Requests):** Objetos con validadores (anotaciones `@Valid`, `@NotNull`) que capturan directamente del cuerpo HTTP la información enviada (Ej: `NewUserDto`, `AuthRequestDto`). De esta manera evitamos inyección de data masiva e innecesaria a las entidades.
- **DTOs de Salida (Responses):** Retornan específicamente los datos que requiere el cliente o panel, descartando información sensible de la DB como contraseñas encriptadas (Ej: `UserDetailsDto`, `AuthResponseDto`).

### Estructura y Encarpetado (SLAP Principle)
El mapeo de carpetas persigue el Single Level of Abstraction Principle (SLAP), enfocándose en modularidad:

```text
src
└── main/java/com/yapps/senaempresa
    ├── config/          # Parametrizaciones globales: CORS, GlobalExceptionHandlers y JWT config global.
    ├── controller/      # Capa web, composición de endpoints REST (@GetMapping, @PostMapping) y validación final a nivel de ruta HTTP.
    ├── model/
    │   ├── dto/         # Objetos de tráfico de datos (Entradas/Salidas)
    │   └── entity/      # Representación directa tabla-objeto en base de datos (@Entity)
    ├── repository/      # Interfaces Spring Data JPA (accesos limpios, sin manipulación nativa de queries)
    ├── security/        # Lógica centralizada de validación por Token, autenticación y cadenas de filtros.
    ├── service/         # Core de la lógica, métodos transaccionales que rigen el negocio.
    └── utils/
        ├── helper/      # Clases de soporte, donde decantamos la funcionalidad difícil para no abrumar los Controller/Service
        ├── mapper/      # Translaciones entre Entity <-> DTO automatizadas
        └── response/    # Objetos envoltorio genérico tipo (GenericResponse, ErrorResponse)
```

### Stack Principal
- **Framework Core:** Java 21 + Spring Boot 3
- **Persistencia de Datos:** PostgreSQL a través de Spring Data JPA (apoyándose en JPQL y transacciones gestionadas).
- **Seguridad de Endpoints:** Módulo de Spring Security interceptando JWT Filters.
- **Herramientas para Código Limpio:** Lombok y MapStruct.
