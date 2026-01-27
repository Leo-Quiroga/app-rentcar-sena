<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f9fafb; --card-bg: #ffffff; --text-main: #111827; --text-muted: #4b5563; }
        body { font-family: 'Inter', system-ui, sans-serif; background-color: var(--bg); margin: 0; color: var(--text-main); }
        .navbar { background: white; padding: 1rem 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); display: flex; justify-content: space-between; align-items: center; }
        .container { max-width: 1000px; margin: 3rem auto; padding: 0 1rem; }
        .welcome-section { text-align: center; margin-bottom: 3rem; }
        .grid-menu { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem; }
        .menu-card { background: var(--card-bg); padding: 1.5rem; border-radius: 0.75rem; text-decoration: none; color: inherit; border: 1px solid #e5e7eb; transition: all 0.3s ease; display: flex; flex-direction: column; gap: 0.5rem; }
        .menu-card:hover { transform: translateY(-5px); box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); border-color: var(--primary); }
        .menu-card h3 { margin: 0; color: var(--primary); font-size: 1.25rem; }
        .menu-card p { margin: 0; color: var(--text-muted); font-size: 0.9rem; }
        .badge { display: inline-block; padding: 0.25rem 0.75rem; background: #eef2ff; color: var(--primary); border-radius: 999px; font-size: 0.75rem; font-weight: 600; width: fit-content; }
    </style>
</head>
<body>
<nav class="navbar">
    <strong style="font-size: 1.5rem; color: var(--primary);">🚗 AutoReserve</strong>
    <span style="color: var(--text-muted);">Panel de Gestión</span>
</nav>
<div class="container">
    <div class="welcome-section">
        <h1>Bienvenido al Sistema</h1>
        <p>Selecciona una opción para administrar la plataforma</p>
    </div>
    <div class="grid-menu">
        <a href="${pageContext.request.contextPath}/users/new" class="menu-card">
            <span class="badge">Gestión</span>
            <h3>Registrar Usuario</h3>
            <p>Añadir nuevos clientes o administradores.</p>
        </a>
        <a href="${pageContext.request.contextPath}/users" class="menu-card">
            <span class="badge">Reportes</span>
            <h3>Listar Usuarios</h3>
            <p>Ver y administrar usuarios registrados.</p>
        </a>

        <a href="${pageContext.request.contextPath}/reservations/new" class="menu-card">
            <span class="badge">Operaciones</span>
            <h3>Crear Reserva</h3>
            <p>Asignar un vehículo a un cliente.</p>
        </a>
        <a href="${pageContext.request.contextPath}/reservations" class="menu-card">
            <span class="badge">Reportes</span>
            <h3>Listar Reservas</h3>
            <p>Control de alquileres activos y pasados.</p>
        </a>

        <a href="${pageContext.request.contextPath}/cars/new" class="menu-card">
            <span class="badge">Inventario</span>
            <h3>Registrar Auto</h3>
            <p>Añadir nuevos vehículos a la flota.</p>
        </a>

        <a href="${pageContext.request.contextPath}/cars" class="menu-card">
            <span class="badge">Inventario</span>
            <h3>Listar Autos</h3>
            <p>Administrar y ver la flota de vehículos.</p>
        </a>
    </div>
</div>
</body>
</html>