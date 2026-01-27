<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reservas | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f9fafb; --text: #1f2937; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); padding: 40px; margin: 0; }
        .container { max-width: 1000px; margin: auto; background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        h2 { border-bottom: 2px solid #f3f4f6; padding-bottom: 1rem; margin-bottom: 1.5rem; }
        table { width: 100%; border-collapse: collapse; }
        th { text-align: left; background: #f3f4f6; padding: 12px; font-size: 0.8rem; text-transform: uppercase; color: #6b7280; }
        td { padding: 14px 12px; border-bottom: 1px solid #f0f0f0; font-size: 0.95rem; }
        .status-badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; background: #dcfce7; color: #166534; }
        .btn-back { display: inline-block; margin-top: 20px; text-decoration: none; color: var(--primary); font-weight: 500; }
    </style>
</head>
<body>
<div class="container">
    <h2>📋 Control de Reservas</h2>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Vehículo</th>
                <th>Periodo</th>
                <th>Total</th>
                <th>Estado</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${reservations}" var="r">
                <tr>
                    <td>#${r.id}</td>
                    <td><strong>${r.user.firstName} ${r.user.lastName}</strong></td>
                    <td>${r.car.brand} ${r.car.model}</td>
                    <td>${r.startDate} al ${r.endDate}</td>
                    <td>$${r.totalAmount}</td>
                    <td><span class="status-badge">${r.status}</span></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <a href="${pageContext.request.contextPath}/" class="btn-back">← Volver al Dashboard</a>
</div>
</body>
</html>