<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Flota | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f9fafb; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); padding: 40px; }
        .container { max-width: 800px; margin: auto; background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .car-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem; margin-top: 1rem; }
        .car-card { border: 1px solid #e5e7eb; padding: 1rem; border-radius: 8px; text-align: center; }
        .car-card h4 { margin: 0; color: var(--primary); }
    </style>
</head>
<body>
<div class="container">
    <h2>Nuestros Vehículos</h2>
    <div class="car-grid">
        <c:forEach items="${cars}" var="c">
            <div class="car-card">
                <h4>${c.brand}</h4>
                <p>${c.model}</p>
            </div>
        </c:forEach>
    </div>
    <br><a href="${pageContext.request.contextPath}/" style="color: var(--primary); text-decoration: none;">← Volver</a>
</div>
</body>
</html>