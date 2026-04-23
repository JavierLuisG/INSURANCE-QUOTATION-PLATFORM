# TECH_STACK.md
# Referencia centralizada del stack tecnológico — Cotizador de Seguros de Daños
# Todos los documentos de arquitectura apuntan a este archivo para versiones y herramientas específicas.

---

## Módulos del Sistema

| Módulo | Carpeta | Rol |
|---|---|---|
| Backend principal | `plataformas-danos-back/` | API REST, motor de cálculo, versionado optimista, auth |
| Frontend | `cotizador-danos-web/` | SPA Next.js, rutas del reto, UI de cotización |
| Simulador | `plataforma-core-ohs/` | Mock server de Plataforma-core-ohs para dev/test |

---

## Backend — `plataformas-danos-back`

**Build**: Maven · **Empaquetado**: JAR · **Java**: 21 LTS


| Dependencia | Versión | Uso |
|---|---|---|
| `spring-boot-starter-parent` | `4.0.5` | BOM y gestión de dependencias |
| `spring-boot-starter-web` | (BOM) | API REST |
| `spring-boot-starter-data-mongodb` | (BOM) | Repositorios NoSQL de documentos |
| `spring-boot-starter-security` | (BOM) | Autenticación y autorización |
| `spring-boot-starter-validation` | (BOM) | Validaciones de entrada con Bean Validation |
| `spring-boot-starter-cache` | (BOM) | Abstracción de caché (Caffeine) |
| `spring-boot-starter-aop` | (BOM) | AOP para resiliencia y auditoría |
| `spring-cloud-starter-gateway` | compatible con Boot 4.x | API Gateway de integración |
| `io.github.resilience4j:resilience4j-spring-boot3` | `2.3.0` | Circuit Breaker y Retry |
| `com.github.ben-manes.caffeine:caffeine` | `3.2.0` | Caché en memoria (TTL configurable) |
| `io.jsonwebtoken:jjwt-api` | `0.12.6` | Generación y validación JWT |
| `io.jsonwebtoken:jjwt-impl` | `0.12.6` | Implementación JWT |
| `io.jsonwebtoken:jjwt-jackson` | `0.12.6` | Serialización JWT |
| `org.springframework.retry:spring-retry` | `2.0.11` | Retry declarativo |
| `org.projectlombok:lombok` | (BOM) | Reducción de boilerplate |
| `spring-boot-starter-test` | (BOM) | JUnit 5 + Mockito + AssertJ |
| `org.testcontainers:testcontainers-bom` | `1.20.4` | Tests de integración con BD real |
| `org.testcontainers:mongodb` | (BOM) | Contenedor MongoDB para tests |

**Cobertura**: JaCoCo Maven plugin · Umbral: ≥80% global, ≥90% módulo `calc-engine`

---

## Frontend — `cotizador-danos-web`

**Build**: npm · **Renderizado**: App Router (Next.js) · **Lenguaje**: TypeScript 5

| Dependencia | Versión | Uso |
|---|---|---|
| `next` | `14.2.15` | Framework React con App Router |
| `react` / `react-dom` | `^18` | UI library |
| `typescript` | `^5` | Tipado estático |
| `tailwindcss` | `^3.4.1` | Estilos utilitarios |
| `autoprefixer` / `postcss` | `^10` / `^8` | Procesamiento CSS |
| `axios` | `^1.7.x` | Cliente HTTP para API REST del backend |
| `zustand` | `^5.x` | Estado global de cotización (ubicaciones, coberturas, estado máquina) |
| `zod` | `^3.x` | Validación de formularios y esquemas |

**Testing**

| Dependencia | Versión | Uso |
|---|---|---|
| `vitest` | `^3.1.1` | Runner de pruebas unitarias |
| `@vitest/coverage-v8` | `^3.1.1` | Cobertura de código |
| `@testing-library/react` | `^16.3.0` | Renderizado y queries de componentes |
| `@testing-library/user-event` | `^14.6.1` | Simulación de interacciones de usuario |
| `@testing-library/jest-dom` | `^6.9.1` | Matchers de DOM |
| `msw` | `^2.13.2` | Mock de API REST en tests (Mock Service Worker) |
| `jsdom` | `^26.1.0` | Entorno DOM para Vitest |

**Cobertura**: `@vitest/coverage-v8` · Umbral: ≥80%

---

## Simulador — `plataforma-core-ohs`

**Runtime**: Node.js 20 LTS · **Build**: npm

| Dependencia | Versión | Uso |
|---|---|---|
| `express` | `^4.21.x` | Servidor HTTP para mock endpoints |
| `mongoose` | `^8.x` | ODM para base de datos de documentos |
| `migrate-mongo` | `^11.x` | Migraciones de datos de prueba versionadas |
| `cors` | `^2.8.x` | CORS para dev local |
| `express-validator` | `^7.x` | Validación de request params |
| `dotenv` | `^16.x` | Variables de entorno por ambiente |

---

## Restricciones Transversales

| Restricción | Valor | Alcance |
|---|---|---|
| Java mínimo | 21 LTS | Backend |
| Node.js mínimo | 20 LTS | Frontend, Simulador |
| Gestor de paquetes JS | npm | Frontend, Simulador |
| Protocolo de comunicación | HTTPS + TLS 1.2+ | Todos los módulos |
| Cifrado en reposo | AES-256 | Base de datos (campos sensibles) |
| Formato de logs | JSON estructurado con `correlation-id` | Backend |
| Cobertura mínima global | ≥80% | Backend y Frontend |
| Cobertura motor de cálculo | ≥90% | Backend (`calc-engine`) |
| Prefijo de folio | `COT-AAAA-NNNNNN` | Backend (módulo Folios) |
| Valores válidos estadoValidacion | `COMPLETA` \| `INCOMPLETA` \| `INACTIVA` | Backend, Frontend |