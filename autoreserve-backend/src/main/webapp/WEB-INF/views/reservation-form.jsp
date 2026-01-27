<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nueva Reserva | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f3f4f6; --text-main: #111827; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .card { background: white; padding: 2rem; border-radius: 1rem; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 500px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
        .full { grid-column: span 2; }
        label { display: block; font-size: 0.875rem; font-weight: 500; margin-bottom: 0.4rem; color: var(--text-main); }
        input, select { width: 100%; padding: 0.625rem; border: 1px solid #d1d5db; border-radius: 0.5rem; box-sizing: border-box; }
        button { width: 100%; padding: 0.75rem; background: var(--primary); color: white; border: none; border-radius: 0.5rem; font-weight: 600; cursor: pointer; margin-top: 1rem; }
        button:hover { background: #4338ca; }
    </style>
</head>
<body>
<div class="card">
    <h2 style="text-align: center; margin-bottom: 1.5rem;">Crear Nueva Reserva</h2>
    <form method="post" action="${pageContext.request.contextPath}/reservations">
        <div class="form-grid">
            <div class="full">
                <label>Cliente</label>
                <select name="userId" required>
                    <c:forEach items="${users}" var="u">
                        <option value="${u.id}">${u.firstName} ${u.lastName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="full">
                <label>Vehículo</label>
                <select name="carId" required>
                    <c:forEach items="${cars}" var="c">
                        <option value="${c.id}">${c.brand} ${c.model}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label>Fecha Inicio</label>
                <input type="date" name="startDate" required>
            </div>
            <div>
                <label>Fecha Fin</label>
                <input type="date" name="endDate" required>
            </div>
            <div class="full">
                <label>Monto Total ($)</label>
                <input type="number" name="totalAmount" step="0.01" placeholder="0.00" required>
            </div>
        </div>
        <button type="submit">Confirmar Reserva</button>
    </form>
    <p style="text-align: center;"><a href="${pageContext.request.contextPath}/" style="color: #6b7280; text-decoration: none; font-size: 0.875rem;">← Cancelar</a></p>
</div>
</body>
</html>