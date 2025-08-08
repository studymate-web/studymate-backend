# 🧠 StudyMate IA - Backend

## 📋 Descripción
StudyMate IA es una plataforma web diseñada para asistir a estudiantes universitarios en su vida académica mediante el uso de herramientas tecnológicas y funciones potenciadas por inteligencia artificial.

## 🎯 Funcionalidades Principales
- 📄 **Resumen de documentos PDF con IA**
- 🧠 **Chat académico (IA)**
- 📅 **Plan de estudio personalizado**
- 📝 **Notas por materia tipo Notion**
- 📆 **Calendario con recordatorios**
- 👤 **Autenticación de usuarios**

## 🛠️ Tecnologías Utilizadas
- **Backend:** Java + Spring Boot
- **Base de Datos:** PostgreSQL
- **Documentación:** Swagger/OpenAPI
- **Seguridad:** Spring Security + BCrypt
- **IA:** OpenAI API (configurable)

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos
- Java 17 o superior
- PostgreSQL instalado y ejecutándose
- Maven (opcional, se incluye el wrapper)

### 1. Configurar Base de Datos
```sql
-- Crear base de datos
CREATE DATABASE studymate;
```

### 2. Configurar Variables de Entorno
Edita `src/main/resources/application.properties`:
```properties
# Configuración de PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/studymate
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# Configuración de OpenAI (opcional)
openai.api.key=tu_api_key_de_openai
```

### 3. Ejecutar el Proyecto
```bash
# Usando Maven wrapper
./mvnw spring-boot:run

# O usando Maven
mvn spring-boot:run
```

### 4. Acceder a la Aplicación
- **API Base:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs

## 📚 Documentación de la API

### Autenticación
```
POST /api/auth/registro
POST /api/auth/login
```

### Materias
```
GET    /api/materias/usuario/{usuarioId}
POST   /api/materias/usuario/{usuarioId}
GET    /api/materias/{id}
PUT    /api/materias/{id}/usuario/{usuarioId}
DELETE /api/materias/{id}/usuario/{usuarioId}
```

### Ejemplo de Uso

#### 1. Registrar Usuario
```bash
curl -X POST "http://localhost:8080/api/auth/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@example.com",
    "password": "123456"
  }'
```

#### 2. Crear Materia
```bash
curl -X POST "http://localhost:8080/api/materias/usuario/1" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Matemáticas",
    "codigo": "MAT101",
    "descripcion": "Matemáticas básicas",
    "creditos": 3,
    "color": "#ff6b6b"
  }'
```

## 🤖 Configuración de IA

### Opción 1: OpenAI (Recomendada para producción)
1. Obtén una API key de [OpenAI](https://platform.openai.com/)
2. Configura en `application.properties`:
```properties
openai.api.key=tu_api_key
openai.api.url=https://api.openai.com/v1/chat/completions
```

### Opción 2: Ollama (Gratuita, local)
1. Instala [Ollama](https://ollama.ai/)
2. Descarga un modelo: `ollama pull llama2`
3. Configura en el código para usar localhost:11434

### Opción 3: Google Gemini (Plan gratuito)
1. Obtén API key de [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Configura en el código para usar Gemini API

## 📁 Estructura del Proyecto
```
src/main/java/com/studymate/
├── config/          # Configuraciones (Security, OpenAI)
├── controller/      # Controladores REST
├── dto/            # Objetos de transferencia de datos
├── exception/      # Manejo de excepciones
├── model/          # Entidades JPA
├── repository/     # Repositorios de datos
├── service/        # Lógica de negocio
└── util/           # Utilidades
```

## 🔧 Endpoints Principales

### Autenticación
- `POST /api/auth/registro` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión

### Materias
- `GET /api/materias/usuario/{id}` - Listar materias del usuario
- `POST /api/materias/usuario/{id}` - Crear nueva materia
- `PUT /api/materias/{id}/usuario/{userId}` - Actualizar materia
- `DELETE /api/materias/{id}/usuario/{userId}` - Eliminar materia

### Notas (Próximamente)
- `GET /api/notas/materia/{materiaId}` - Listar notas de una materia
- `POST /api/notas` - Crear nueva nota
- `PUT /api/notas/{id}` - Actualizar nota
- `DELETE /api/notas/{id}` - Eliminar nota

### Tareas (Próximamente)
- `GET /api/tareas/usuario/{id}` - Listar tareas del usuario
- `POST /api/tareas` - Crear nueva tarea
- `PUT /api/tareas/{id}` - Actualizar tarea
- `DELETE /api/tareas/{id}` - Eliminar tarea

## 🎨 Frontend Recomendado

Para el frontend, te recomiendo estas plantillas gratuitas:

1. **AdminLTE** - Muy completa, gratuita
2. **Bootstrap Admin** - Moderna y responsive
3. **Tabler** - Elegante y gratuita
4. **Material Dashboard** - Diseño Material Design

## 🚀 Próximos Pasos

1. **Implementar JWT** para autenticación completa
2. **Servicios de IA** para resumen de PDFs y chatbot
3. **Subida de archivos** para PDFs
4. **Calendario** con recordatorios
5. **Notas tipo Notion** con editor rico
6. **Frontend** con una de las plantillas recomendadas

## 📞 Soporte

Si tienes dudas o problemas:
1. Revisa la documentación en Swagger UI
2. Verifica los logs en la consola
3. Asegúrate de que PostgreSQL esté ejecutándose
4. Confirma que las variables de entorno estén configuradas

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.
