# MANUAL DE DESARROLLADOR - AUTORESERVE

## Información del Documento
- **Versión:** 1.0
- **Fecha:** Enero 2025
- **Proyecto:** AutoReserve - Sistema de Reserva de Vehículos

---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este manual está dirigido a desarrolladores que trabajarán en el proyecto AutoReserve. Incluye:
- Arquitectura del sistema
- Estructura del código
- Guías de estilo
- Proceso de contribución
- Mejores prácticas

### 1.2 Stack Tecnológico

**Backend:**
- Java 21
- Spring Boot 3.4.1
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Maven 3.9

**Frontend:**
- React 19
- Vite 7.1.2
- React Router DOM 7.8
- TailwindCSS 3.4
- Headless UI 2.2

---

## 2. ARQUITECTURA DEL SISTEMA

### 2.1 Arquitectura General

```
┌─────────────────────────────────────┐
│         FRONTEND (React)            │
│  - Components                       │
│  - Pages                            │
│  - API Clients                      │
│  - State Management (Context)       │
└──────────────┬──────────────────────┘
               │ HTTP/REST
               │ JSON
┌──────────────▼──────────────────────┐
│         BACKEND (Spring Boot)       │
│  ┌────────────────────────────┐    │
│  │   Controllers (REST API)   │    │
│  └──────────┬─────────────────┘    │
│             │                       │
│  ┌──────────▼─────────────────┐    │
│  │   Services (Business Logic)│    │
│  └──────────┬─────────────────┘    │
│             │                       │
│  ┌──────────▼─────────────────┐    │
│  │   Repositories (Data Access)│   │
│  └──────────┬─────────────────┘    │
└─────────────┼───────────────────────┘
              │ JDBC
┌─────────────▼───────────────────────┐
│         MySQL Database              │
└─────────────────────────────────────┘
```

### 2.2 Patrón de Arquitectura

**Backend:** Arquitectura en capas (Layered Architecture)

```
┌─────────────────────────────────────┐
│  Web Layer (Controllers)            │  ← REST endpoints
├─────────────────────────────────────┤
│  Service Layer (Business Logic)     │  ← Lógica de negocio
├─────────────────────────────────────┤
│  Repository Layer (Data Access)     │  ← Acceso a datos
├─────────────────────────────────────┤
│  Domain Layer (Entities)            │  ← Modelos de dominio
└─────────────────────────────────────┘
```

**Frontend:** Component-Based Architecture

```
┌─────────────────────────────────────┐
│  Pages (Route Components)           │  ← Páginas completas
├─────────────────────────────────────┤
│  Components (Reusable UI)           │  ← Componentes reutilizables
├─────────────────────────────────────┤
│  API Layer (HTTP Clients)           │  ← Comunicación con backend
├─────────────────────────────────────┤
│  State Management (Context)         │  ← Estado global
└─────────────────────────────────────┘
```

---

## 3. ESTRUCTURA DEL PROYECTO

### 3.1 Estructura del Backend

```
autoreserve-backend/
├── src/
│   ├── main/
│   │   ├── java/com/autoreserve/backend/
│   │   │   ├── bootstrap/              # Inicialización de datos
│   │   │   │   └── AdminBootstrap.java
│   │   │   ├── config/                 # Configuraciones
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── DataInitializer.java
│   │   │   ├── domain/                 # Capa de dominio
│   │   │   │   ├── entity/            # Entidades JPA
│   │   │   │   ├── repository/        # Repositorios
│   │   │   │   └── service/           # Servicios de negocio
│   │   │   ├── dto/                    # Data Transfer Objects
│   │   │   │   ├── auth/
│   │   │   │   ├── car/
│   │   │   │   ├── category/
│   │   │   │   └── reservation/
│   │   │   ├── security/               # Seguridad y JWT
│   │   │   │   ├── jwt/
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── web/                    # Capa web
│   │   │   │   ├── controller/        # REST Controllers
│   │   │   │   └── exception/         # Manejo de excepciones
│   │   │   └── AutoreserveBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/                           # Tests
├── pom.xml                             # Dependencias Maven
└── API_DOCUMENTATION.md                # Documentación API
```

### 3.2 Estructura del Frontend

```
autoreserve-frontend/
├── public/
│   └── images/                         # Imágenes públicas
├── src/
│   ├── api/                           # Clientes HTTP
│   │   ├── http.js                    # Configuración Axios
│   │   ├── carsApi.js
│   │   ├── reservationsApi.js
│   │   └── ...
│   ├── assets/                        # Assets estáticos
│   │   ├── cars/                      # Imágenes de autos
│   │   ├── categories/                # Imágenes de categorías
│   │   └── branches/                  # Imágenes de sedes
│   ├── auth/                          # Autenticación
│   │   ├── AuthContext.js
│   │   ├── AuthProvider.jsx
│   │   └── authApi.js
│   ├── components/                    # Componentes reutilizables
│   │   ├── CarCard.jsx
│   │   ├── Header.jsx
│   │   ├── Footer.jsx
│   │   └── ...
│   ├── layouts/                       # Layouts
│   │   └── MainLayout.jsx
│   ├── pages/                         # Páginas/Rutas
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── AdminDashboard.jsx
│   │   └── ...
│   ├── routes/                        # Configuración de rutas
│   │   ├── AppRoutes.jsx
│   │   ├── ProtectedRoute.jsx
│   │   └── AdminRoute.jsx
│   ├── styles/                        # Estilos globales
│   │   └── index.css
│   ├── utils/                         # Utilidades
│   │   └── imageUtils.js
│   ├── App.jsx                        # Componente raíz
│   └── main.jsx                       # Punto de entrada
├── package.json                       # Dependencias NPM
├── vite.config.js                     # Configuración Vite
└── tailwind.config.js                 # Configuración Tailwind
```

---

## 4. CONVENCIONES DE CÓDIGO

### 4.1 Backend (Java)

**Nomenclatura:**
- **Clases:** PascalCase → `UserService`, `CarController`
- **Métodos:** camelCase → `findById()`, `createReservation()`
- **Constantes:** UPPER_SNAKE_CASE → `MAX_RETRY_ATTEMPTS`
- **Paquetes:** lowercase → `com.autoreserve.backend.domain`

**Ejemplo:**
```java
@Service
public class CarService {
    
    private static final int DEFAULT_PAGE_SIZE = 10;
    
    private final CarRepository carRepository;
    
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
    
    public CarResponse findById(Long id) {
        // Implementación
    }
}
```

**Anotaciones comunes:**
- `@RestController` - Controladores REST
- `@Service` - Servicios de negocio
- `@Repository` - Repositorios
- `@Entity` - Entidades JPA
- `@Transactional` - Transacciones
- `@Valid` - Validación de DTOs

### 4.2 Frontend (JavaScript/React)

**Nomenclatura:**
- **Componentes:** PascalCase → `CarCard.jsx`, `AdminDashboard.jsx`
- **Funciones:** camelCase → `fetchCars()`, `handleSubmit()`
- **Constantes:** UPPER_SNAKE_CASE → `API_BASE_URL`
- **Archivos:** camelCase → `carsApi.js`, `authApi.js`

**Ejemplo:**
```javascript
// CarCard.jsx
import React from 'react';

const CarCard = ({ car, onReserve }) => {
  const handleClick = () => {
    onReserve(car.id);
  };
  
  return (
    <div className="car-card">
      <img src={car.image} alt={car.model} />
      <h3>{car.brand} {car.model}</h3>
      <button onClick={handleClick}>Reservar</button>
    </div>
  );
};

export default CarCard;
```

**Hooks comunes:**
- `useState` - Estado local
- `useEffect` - Efectos secundarios
- `useContext` - Contexto global
- `useNavigate` - Navegación
- `useAuth` - Autenticación (custom hook)

---

## 5. GUÍA DE DESARROLLO

### 5.1 Configurar Entorno de Desarrollo

**Requisitos:**
- Java JDK 21
- Node.js 20+
- MySQL 8.0
- Git
- IDE (IntelliJ IDEA, VS Code)

**Pasos:**

1. **Clonar repositorio:**
```bash
git clone <repository-url>
cd autoreserve-app-rentcar
```

2. **Configurar backend:**
```bash
cd autoreserve-backend
# Editar application.properties con tus credenciales MySQL
mvn clean install
mvn spring-boot:run
```

3. **Configurar frontend:**
```bash
cd autoreserve-frontend
npm install
npm run dev
```

### 5.2 Crear una Nueva Entidad

**Paso 1:** Crear la entidad en `domain/entity/`

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private BigDecimal price;
    
    // Getters y Setters
}
```

**Paso 2:** Crear el repositorio en `domain/repository/`

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
}
```

**Paso 3:** Crear DTOs en `dto/product/`

```java
public record ProductRequest(
    @NotBlank String name,
    @NotNull BigDecimal price
) {}

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price
) {}
```

**Paso 4:** Crear el servicio en `domain/service/`

```java
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }
    
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    }
}
```

**Paso 5:** Crear el controlador en `web/controller/`

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Producto creado exitosamente",
            "data", response
        ));
    }
}
```

### 5.3 Crear un Nuevo Componente React

**Paso 1:** Crear el archivo en `src/components/`

```javascript
// ProductCard.jsx
import React from 'react';

const ProductCard = ({ product }) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-4">
      <h3 className="text-xl font-bold">{product.name}</h3>
      <p className="text-gray-600">${product.price}</p>
    </div>
  );
};

export default ProductCard;
```

**Paso 2:** Crear el cliente API en `src/api/`

```javascript
// productsApi.js
import http from './http';

export const getProducts = async () => {
  const response = await http.get('/products');
  return response.data;
};

export const createProduct = async (productData) => {
  const response = await http.post('/products', productData);
  return response.data;
};
```

**Paso 3:** Crear la página en `src/pages/`

```javascript
// Products.jsx
import React, { useState, useEffect } from 'react';
import { getProducts } from '../api/productsApi';
import ProductCard from '../components/ProductCard';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    fetchProducts();
  }, []);
  
  const fetchProducts = async () => {
    try {
      const data = await getProducts();
      setProducts(data);
    } catch (error) {
      console.error('Error fetching products:', error);
    } finally {
      setLoading(false);
    }
  };
  
  if (loading) return <div>Cargando...</div>;
  
  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">Productos</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {products.map(product => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
};

export default Products;
```

---

## 6. TESTING

### 6.1 Tests Backend (JUnit)

**Ubicación:** `src/test/java/`

**Ejemplo de test de servicio:**

```java
@SpringBootTest
class CarServiceTest {
    
    @Autowired
    private CarService carService;
    
    @MockBean
    private CarRepository carRepository;
    
    @Test
    void testFindById_Success() {
        // Arrange
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Toyota");
        
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        
        // Act
        CarResponse result = carService.findById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Toyota", result.brand());
    }
}
```

**Ejecutar tests:**
```bash
mvn test
mvn test -Dtest=CarServiceTest
```

### 6.2 Tests Frontend (Jest/React Testing Library)

**Ejemplo de test de componente:**

```javascript
import { render, screen } from '@testing-library/react';
import CarCard from './CarCard';

test('renders car information', () => {
  const car = {
    id: 1,
    brand: 'Toyota',
    model: 'RAV4',
    pricePerDay: 150000
  };
  
  render(<CarCard car={car} />);
  
  expect(screen.getByText('Toyota RAV4')).toBeInTheDocument();
  expect(screen.getByText('$150,000')).toBeInTheDocument();
});
```

---

## 7. GIT WORKFLOW

### 7.1 Branching Strategy

```
main (producción)
  ↑
develop (desarrollo)
  ↑
feature/nueva-funcionalidad
bugfix/correccion-error
hotfix/error-critico
```

### 7.2 Convención de Commits

**Formato:**
```
<tipo>(<alcance>): <descripción>

[cuerpo opcional]

[footer opcional]
```

**Tipos:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Documentación
- `style`: Formato de código
- `refactor`: Refactorización
- `test`: Tests
- `chore`: Tareas de mantenimiento

**Ejemplos:**
```bash
git commit -m "feat(cars): agregar filtro por categoría"
git commit -m "fix(auth): corregir validación de JWT"
git commit -m "docs(api): actualizar documentación de endpoints"
```

### 7.3 Pull Request Process

1. Crear branch desde `develop`
2. Hacer cambios y commits
3. Push a repositorio remoto
4. Crear Pull Request a `develop`
5. Code review por al menos 1 desarrollador
6. Merge después de aprobación

---

## 8. API REST GUIDELINES

### 8.1 Estructura de Respuestas

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { }
}
```

**Respuesta de error:**
```json
{
  "success": false,
  "message": "Descripción del error",
  "errors": ["detalle1", "detalle2"]
}
```

### 8.2 Códigos HTTP

- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado
- `400 Bad Request` - Error en la solicitud
- `401 Unauthorized` - No autenticado
- `403 Forbidden` - Sin permisos
- `404 Not Found` - Recurso no encontrado
- `409 Conflict` - Conflicto (ej: duplicado)
- `500 Internal Server Error` - Error del servidor

### 8.3 Naming Conventions

**Endpoints:**
- Usar sustantivos en plural
- Usar kebab-case para URLs
- Usar verbos HTTP apropiados

```
GET    /api/cars           - Listar todos
GET    /api/cars/{id}      - Obtener uno
POST   /api/cars           - Crear
PUT    /api/cars/{id}      - Actualizar
DELETE /api/cars/{id}      - Eliminar
```

---

## 9. SEGURIDAD

### 9.1 Autenticación JWT

**Flujo:**
1. Usuario envía credenciales a `/api/auth/login`
2. Backend valida y genera JWT
3. Cliente almacena token en localStorage
4. Cliente envía token en header `Authorization: Bearer <token>`
5. Backend valida token en cada request

**Implementación:**
```java
// Generar token
String token = jwtService.generateToken(user.getEmail(), user.getRole());

// Validar token
String email = jwtService.extractEmail(token);
```

### 9.2 Protección de Endpoints

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 9.3 Validación de Datos

```java
public record CarRequest(
    @NotBlank(message = "La marca es requerida")
    String brand,
    
    @NotBlank(message = "El modelo es requerido")
    String model,
    
    @Min(value = 2000, message = "El año debe ser mayor a 2000")
    Integer year,
    
    @Positive(message = "El precio debe ser positivo")
    BigDecimal pricePerDay
) {}
```

---

## 10. PERFORMANCE

### 10.1 Optimización de Queries

**Usar paginación:**
```java
Page<Car> cars = carRepository.findAll(PageRequest.of(page, size));
```

**Eager vs Lazy Loading:**
```java
@ManyToOne(fetch = FetchType.LAZY)
private Category category;
```

**Índices en base de datos:**
```java
@Table(name = "cars", indexes = {
    @Index(name = "idx_brand", columnList = "brand"),
    @Index(name = "idx_category", columnList = "category_id")
})
```

### 10.2 Caché

```java
@Cacheable("categories")
public List<CategoryResponse> findAll() {
    return categoryRepository.findAll()
        .stream()
        .map(this::mapToResponse)
        .toList();
}
```

---

## 11. DEPLOYMENT

### 11.1 Build para Producción

**Backend:**
```bash
mvn clean package -DskipTests -Pprod
```

**Frontend:**
```bash
npm run build
```

### 11.2 Variables de Entorno

**Backend:**
```properties
AUTORESERVE_DB_URL=jdbc:mysql://...
AUTORESERVE_DB_USER=user
AUTORESERVE_DB_PASSWORD=password
JWT_SECRET=secret
```

**Frontend:**
```bash
VITE_API_URL=https://api.tudominio.com
```

---

## 12. TROUBLESHOOTING

### 12.1 Problemas Comunes Backend

**Error:** `Port 8080 already in use`
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

**Error:** `Could not connect to MySQL`
- Verificar que MySQL esté corriendo
- Verificar credenciales en application.properties
- Verificar que la base de datos exista

### 12.2 Problemas Comunes Frontend

**Error:** `Module not found`
```bash
rm -rf node_modules package-lock.json
npm install
```

**Error:** `CORS policy`
- Verificar configuración de CORS en backend
- Verificar que la URL del backend sea correcta

---

## 13. RECURSOS ADICIONALES

### 13.1 Documentación Oficial

- Spring Boot: https://spring.io/projects/spring-boot
- React: https://react.dev/
- TailwindCSS: https://tailwindcss.com/
- MySQL: https://dev.mysql.com/doc/

### 13.2 Herramientas Recomendadas

- **IDE:** IntelliJ IDEA, VS Code
- **API Testing:** Postman, Insomnia
- **Database:** MySQL Workbench, DBeaver
- **Git GUI:** GitKraken, SourceTree

---

**Documento elaborado por:** Equipo AutoReserve  
**Última actualización:** Enero 2025  
**Versión:** 1.0
