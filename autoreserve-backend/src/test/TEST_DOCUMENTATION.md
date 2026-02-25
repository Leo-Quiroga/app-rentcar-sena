# AutoReserve Backend - Tests

## Estructura de Tests

```
src/test/java/com/autoreserve/backend/
├── web/controller/          # Tests de controladores (Unit Tests)
│   ├── AuthControllerTest.java
│   ├── CarControllerTest.java
│   ├── CategoryControllerTest.java
│   ├── BranchControllerTest.java
│   └── SearchControllerTest.java
├── domain/repository/       # Tests de repositorios (Integration Tests con H2)
│   ├── CarRepositoryTest.java
│   ├── UserRepositoryTest.java
│   └── ReservationRepositoryTest.java
└── integration/             # Tests de integración end-to-end
    ├── AuthenticationIntegrationTest.java
    ├── ReservationIntegrationTest.java
    └── CarSearchIntegrationTest.java
```

## Tipos de Tests Implementados

### 1. Tests de Controladores (Unit Tests)
- **AuthControllerTest**: Login, registro, validaciones
- **CarControllerTest**: Listado de autos, filtros, paginación
- **CategoryControllerTest**: CRUD de categorías
- **BranchControllerTest**: CRUD de sedes
- **SearchControllerTest**: Búsqueda de autos por fechas

**Características:**
- Usan `@SpringBootTest` y `@AutoConfigureMockMvc`
- Mockean dependencias con `@MockBean`
- Prueban endpoints HTTP y respuestas JSON

### 2. Tests de Repositorios (Integration Tests)
- **CarRepositoryTest**: Queries de disponibilidad, filtros
- **UserRepositoryTest**: Búsqueda por email, CRUD
- **ReservationRepositoryTest**: Reservas por usuario, ordenamiento

**Características:**
- Usan `@DataJpaTest` con H2 en memoria
- Prueban queries JPA reales
- Usan `TestEntityManager` para setup

### 3. Tests de Integración (End-to-End)
- **AuthenticationIntegrationTest**: Flujo completo de registro y login
- **ReservationIntegrationTest**: Crear y cancelar reservas
- **CarSearchIntegrationTest**: Búsqueda con filtros y disponibilidad

**Características:**
- Prueban flujos completos de usuario
- Usan base de datos H2 real
- Incluyen autenticación JWT

## Ejecutar Tests

### Todos los tests
```bash
mvn test
```

### Tests específicos
```bash
# Solo tests de controladores
mvn test -Dtest=*ControllerTest

# Solo tests de repositorios
mvn test -Dtest=*RepositoryTest

# Solo tests de integración
mvn test -Dtest=*IntegrationTest

# Un test específico
mvn test -Dtest=AuthControllerTest
```

### Con reporte de cobertura
```bash
mvn clean test jacoco:report
```
El reporte se genera en: `target/site/jacoco/index.html`

## Configuración

Los tests usan configuración específica en:
- `src/test/resources/application-test.properties`
- Base de datos H2 en memoria
- JWT con secret de prueba

## Cobertura Actual

- ✅ **Autenticación**: Login, registro, validaciones
- ✅ **Autos**: Listado, filtros, búsqueda por fechas
- ✅ **Categorías**: CRUD básico
- ✅ **Sedes**: CRUD básico
- ✅ **Reservas**: Crear, listar, cancelar
- ✅ **Repositorios**: Queries personalizadas
- ✅ **Integración**: Flujos completos de usuario

## Mejores Prácticas Aplicadas

1. **AAA Pattern**: Arrange, Act, Assert
2. **Nombres descriptivos**: `login_UserNotFound_Returns404()`
3. **Isolation**: Cada test es independiente
4. **Setup/Teardown**: `@BeforeEach` para inicialización
5. **Mocking**: Solo mockear dependencias externas
6. **Assertions claras**: Usar AssertJ y JsonPath

## Próximos Tests a Implementar

- [ ] AdminCarController
- [ ] AdminCategoryController
- [ ] AdminBranchController
- [ ] AdminUserController
- [ ] AdminReservationController
- [ ] FavoriteController
- [ ] Tests de seguridad (RBAC)
- [ ] Tests de validación de DTOs

## Notas

- Los tests usan H2 en memoria, no afectan la BD de desarrollo
- JWT tokens son generados con secret de prueba
- Transacciones se revierten automáticamente después de cada test
- Los tests son independientes y pueden ejecutarse en cualquier orden
