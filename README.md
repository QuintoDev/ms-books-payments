# Book Catalogue API

Una API REST desarrollada en Java usando Spring Boot, que permite la gestión de un catálogo de libros. Esta API soporta operaciones CRUD (Crear, Leer, Actualizar y Eliminar) con funcionalidades adicionales como búsquedas filtradas.

## Características principales

- **Gestión de libros:**
  - Crear, obtener, actualizar y eliminar libros.
- **Búsquedas avanzadas:**
  - Filtro por título, autor, fecha de publicación, categoría, ISBN, valoración y visibilidad.
- **Logs técnicos:**
  - Implementación de logs claros y detallados para facilitar la depuración.
- **Diseño modular:**
   - Separación de responsabilidades entre el controlador y el servicio.

## Requisitos

- **Java: 17 o superior**
- **Maven: 3.8 o superior**
- **Spring Boot: 3.x**
- **Base de datos (opcional): H2, MySQL, PostgreSQL, etc.**

## Instalación
### 1. Clona el repositorio:
```bash
git clone https://github.com/tuusuario/book-catalogue.git
cd book-catalogue
```

### 2. Construye el proyecto con Maven:
```bash
mvn clean install
```

### 3. Configura las propiedades de la aplicación en src/main/resources/application.properties:
```properties
server.port=8080
logging.level.root=INFO
```

### 4. Ejecuta la aplicación:
```bash
mvn spring-boot:run
```