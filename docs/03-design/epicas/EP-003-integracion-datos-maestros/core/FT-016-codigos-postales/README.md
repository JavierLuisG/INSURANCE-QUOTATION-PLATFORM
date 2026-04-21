## FT-016: Integración de Catálogo de Códigos Postales y Zonas

### 1. Descripción

Esta feature permite la consulta, validación y mapeo de códigos postales hacia su correspondiente información de zona (CAT y nivel técnico), mediante la integración con servicios externos (`Plataforma-core-ohs` o su mock). Esta información es clave para la correcta clasificación del riesgo por ubicación.

---

### 2. Objetivo de Negocio

Garantizar que cada ubicación de riesgo esté correctamente clasificada geográficamente, permitiendo aplicar tarifas y factores técnicos adecuados en el cálculo de primas.

---

### 3. Alcance Funcional

Incluye:

* Consulta de código postal contra servicio externo
* Validación de formato y existencia del código postal
* Obtención de zona CAT y nivel técnico
* Mapeo de datos externos al modelo interno
* Persistencia de la información de zona en la ubicación

No incluye:

* Cálculo de primas (FT-012)
* Gestión de ubicaciones de riesgo (FT-002)

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta                          |
| ------ | --------------------- | ------------------------------------------ |
| HU-074 | Consultar CP y zona   | Obtiene zona CAT y nivel técnico           |
| HU-075 | Validar código postal | Verifica formato y existencia              |
| HU-076 | Mapear zonas          | Transforma datos externos a modelo interno |

---

### 5. Flujo Funcional

1. Usuario ingresa código postal en formulario (HU-075)
2. Sistema valida formato básico (frontend/backend)
3. Sistema consulta servicio externo (HU-074)
4. Se obtiene información de zona (CAT, nivel técnico)
5. Sistema mapea respuesta al modelo interno (HU-076)
6. Información de zona se asocia y persiste en la ubicación

---

### 6. Dependencias Técnicas

* Servicio externo `Plataforma-core-ohs` (o mock)
* Cliente API REST
* Módulo de mapeo de datos
* Modelo de datos de ubicación de riesgo

---

### 7. Consideraciones Técnicas

* Validación dual: frontend (formato) y backend (existencia)
* Manejo de latencia en consultas externas (timeout/caching opcional)
* Mapeo desacoplado mediante DTOs o adaptadores
* Manejo de valores faltantes (defaults o errores controlados)
* Diseño orientado a alta frecuencia de consultas (optimización de performance)
