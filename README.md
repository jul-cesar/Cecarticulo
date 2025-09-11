# Cecarticulo

Cecarticulo es una aplicación backend desarrollada con **Spring Boot** que permite buscar, descargar, procesar y almacenar artículos científicos desde arXiv. Extrae metadatos, texto, imágenes y palabras clave de los PDFs, almacenando todo en una base de datos MongoDB. El sistema está optimizado para procesamiento concurrente y utiliza IA para la generación de keywords.

## Características principales

- **Búsqueda de artículos en arXiv** por palabra clave.
- **Descarga automática de PDFs** y extracción de texto e imágenes.
- **Generación de palabras clave** usando modelos LLM (Google Gemini).
- **Almacenamiento de artículos** y metadatos en MongoDB.
- **Procesamiento concurrente** configurable por hilos.
- **API RESTful** para consultar artículos, buscar y monitorear progreso.

## Arquitectura

- **Spring Boot 3.5.5**
- **MongoDB** como base de datos principal.
- **Apache PDFBox** para procesamiento de PDFs.
- **Jackson XML** para parseo de feeds arXiv.
- **Google GenAI** para generación de keywords.
- **Procesamiento multihilo** con `ExecutorService`.

## Endpoints principales

| Método | Endpoint           | Descripción                                                                 |
|--------|--------------------|----------------------------------------------------------------------------|
| GET    | `/articles`        | Lista paginada de artículos (sin texto completo).                           |
| GET    | `/articles/{id}`   | Obtiene el texto completo y detalles de un artículo por ID.                 |
| GET    | `/search`          | Busca artículos en arXiv y los procesa/almacena en MongoDB.                 |
| GET    | `/progress`        | Consulta el progreso del procesamiento de artículos.                        |

## Ejemplo de flujo

1. **Buscar artículos:**  
   `GET /search?query=machine+learning&maxResults=10`  
   Descarga y procesa los primeros 10 artículos de arXiv sobre "machine learning".

2. **Consultar artículos:**  
   `GET /articles?page=1&size=20`  
   Obtiene la lista paginada de artículos almacenados.

3. **Ver detalles de un artículo:**  
   `GET /articles/{id}`  
   Devuelve el texto completo y metadatos del artículo.

4. **Monitorear progreso:**  
   `GET /progress`  
   Muestra el número total y procesados, y el tiempo de procesamiento.

## Configuración

La configuración principal se encuentra en `src/main/resources/application.properties`:

```properties
spring.application.name=Cecarticulo
app.threads=3
spring.data.mongodb.uri=mongodb+srv://cecarticulo:cecar123@cluster0.nsqctje.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0
spring.data.mongodb.database=articulos
```

Puedes ajustar el número de hilos (`app.threads`) y la conexión a MongoDB según tus necesidades.

## Dependencias principales

- `spring-boot-starter-data-mongodb`
- `spring-boot-starter-web`
- `spring-boot-starter-webflux`
- `spring-boot-starter-validation`
- `lombok`
- `pdfbox`
- `jackson-dataformat-xml`
- `google-genai`

Ver el archivo [`pom.xml`](pom.xml) para detalles completos.

## Ejecución local

1. **Clona el repositorio**
2. **Configura MongoDB** (local o Atlas, ajusta el URI en `application.properties`)
3. **Compila y ejecuta:**

```powershell
.\mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

## Estructura de carpetas

- `src/main/java/com/aej/cecarticulo` - Código fuente principal
- `src/main/resources` - Configuración
- `pdfs/` - PDFs descargados
- `downloads/` - Feeds XML descargados

## Ejemplo de respuesta

### GET `/articles`

```json
{
  "content": [
    {
      "id": "...",
      "title": "A Survey of Optimization Methods from a Machine Learning Perspective",
      "summary": "...",
      "publishedDate": "2023-01-01",
      "authors": ["John Doe", "Jane Smith"],
      "categories": ["cs.LG"],
      "pdfUrl": "https://arxiv.org/pdf/1234.5678.pdf",
      "images": ["base64string1", "base64string2"],
      "keywords": ["optimization", "learning", "survey"],
      "text": "..."
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET `/progress`

```json
{
  "total": 10,
  "procesados": 7,
  "tiempoSegundos": 12
}
```


### Agregar variable de entorno GOOGLE_API_KEY 

Usamos la api de gemini y esta nos obliga a usar una variable de entorno, no deja con el .properties