<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Lista de Usuarios | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f9fafb; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); padding: 40px; margin: 0; }
        .container { max-width: 1000px; margin: auto; background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }
        h2 { color: #111827; margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between; }
        table { width: 100%; border-collapse: collapse; }
        th { text-align: left; background: #f3f4f6; padding: 12px; font-size: 0.85rem; color: #4b5563; text-transform: uppercase; }
        td { padding: 14px 12px; border-bottom: 1px solid #e5e7eb; color: #374151; font-size: 0.95rem; }
        .role-badge { padding: 4px 12px; border-radius: 999px; font-size: 0.75rem; font-weight: 600; }
        .role-1 { background: #dcfce7; color: #166534; } /* Cliente */
        .role-2 { background: #fee2e2; color: #991b1b; } /* Admin */
        .btn-add { background: var(--primary); color: white; padding: 8px 16px; border-radius: 6px; text-decoration: none; font-size: 0.9rem; }
    </style>
</head>
<body>
<div class="container">
    <h2>
        Usuarios Registrados
        <a href="${pageContext.request.contextPath}/users/new" class="btn-add">+ Nuevo Usuario</a>
    </h2>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre Completo</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Rol</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${users}" var="u">
                <tr>
                    <td>#${u.id}</td>
                    <td><strong>${u.firstName} ${u.lastName}</strong></td>
                    <td>${u.email}</td>
                    <td>${u.phone != null ? u.phone : '---'}</td>
                    <td>
                        <span class="role-badge role-${u.role.id}">
                            ${u.role.name}
                        </span>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>
    <a href="${pageContext.request.contextPath}/" style="color: var(--primary); text-decoration: none; font-weight: 500;">← Volver al Dashboard</a>
</div>
</body>
</html>