<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Auto</title>
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
<div class="card">
    <h2>Registrar Vehículo</h2>
    <form method="post" action="${pageContext.request.contextPath}/cars">
        <div class="form-grid">
            <input type="text" name="brand" placeholder="Marca" required>
            <input type="text" name="model" placeholder="Modelo" required>
            <input type="number" name="year" placeholder="Año" required>
            <input type="text" name="plate" placeholder="Placa" required>
            <input type="number" name="pricePerDay" step="0.01" placeholder="Precio por día" required>
            
            <select name="status">
                <option value="AVAILABLE">Disponible</option>
                <option value="MAINTENANCE">Mantenimiento</option>
            </select>

            <select name="categoryId" required>
                <option value="" disabled selected>Categoría...</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.id}">${cat.name}</option>
                </c:forEach>
            </select>

            <select name="branchId" required>
                <option value="" disabled selected>Sucursal...</option>
                <c:forEach items="${branches}" var="b">
                    <option value="${b.id}">${b.name}</option>
                </c:forEach>
            </select>
        </div>
        <button type="submit">Guardar Auto</button>
    </form>
</div>
</body>