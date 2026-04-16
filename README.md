# 🌾 Sena Empresa Backend - Spring Boot Migration

Este proyecto es la migración en Java (Spring Boot) del sistema heredado MVC de Sena Empresa (originalmente en PHP). Se encarga de automatizar y estandarizar los procesos de la empresa que produce y comercializa productos agrícolas y avícolas.

## 🏗️ Arquitectura y Principios
Diseñado bajo una **Arquitectura N-Tier** aplicando **SOLID** y **SLAP (Single Level of Abstraction Principle)**:
- **Controladores (@RestController)**: Enrutamiento HTTP, validación inicial y retorno de DTOs. Las entidades JPA puras nunca se exponen al cliente.
- **Servicios (@Service)**: Lógica de negocio. Métodos insert/update están anotados con `@Transactional`, lecturas con `@Transactional(readOnly=true)`.
- **Repositorios (@Repository)**: Uso exclusivo de JPQL, Entity Graphs y Proyecciones vía `JpaRepository`. Sin native queries.
- **Mappers (MapStruct)**: Centralizado con una configuración global (`@MapperConfig`) y una interface `BaseMapper<D, E>` para conversiones.
- **Helpers**: Métodos de negocio complejos extraídos de los Servicios hacia `@Component` subyacentes.

## 📂 Estructura de Carpetas

```text
src
└── main
    └── java
        └── com
            └── yapps
                └── senaempresa
                    ├── config/             # Componentes transversales: ExceptionHandler, MapperBase, ResponseAdvice
                    ├── controller/         # Endpoints HTTP
                    ├── model/
                    │   ├── entity/         # Clases JPA (@Entity)
                    │   └── dto/            # Data Transfer Objects
                    ├── repository/         # Interfaces de acceso a base de datos
                    ├── service/            # Lógica principal e interacciones modulares
                    │   └── impl/           
                    └── utils/
                        ├── helper/         # Lógica compleja delegada (SLAP)
                        └── response/       # Estructura del Response Global
```

## 🛠 Normas y Reglas (Codeline Standard)
1. **Idioma**: Clases, métodos, variables en **Inglés**.
2. **Respuesta Global (ResponseBodyAdvice)**: Intercepta todas las respuestas mapeándolas en el Wrapper único:
   ```json
   {
       "response": {
           "length": 1,
           "statusCode": 200,
           "body": { ... }
       }
   }
   ```
3. **Manejo de Errores**: `GlobalExceptionHandler` intercepta fallas y provee respuestas JSON predecibles, las cuales **NO** son re-envueltas en el Wrapper normalizado de arriba.
