<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f3f4f6; --text-main: #111827; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .card { background: white; padding: 2.5rem; border-radius: 1rem; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); width: 100%; max-width: 450px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.2rem; }
        .full { grid-column: span 2; }
        label { display: block; font-size: 0.875rem; font-weight: 600; margin-bottom: 0.5rem; color: #374151; }
        input, select { width: 100%; padding: 0.75rem; border: 1px solid #d1d5db; border-radius: 0.5rem; box-sizing: border-box; font-size: 0.9rem; }
        input:focus { outline: none; border-color: var(--primary); ring: 2px var(--primary); }
        button { width: 100%; padding: 0.8rem; background: var(--primary); color: white; border: none; border-radius: 0.5rem; font-weight: 600; cursor: pointer; margin-top: 1.5rem; transition: background 0.2s; }
        button:hover { background: #4338ca; }
        .back-link { display: block; text-align: center; margin-top: 1rem; color: #6b7280; text-decoration: none; font-size: 0.85rem; }
    </style>
</head>
<body>
<div class="card">
    <h2 style="text-align: center; margin-top: 0;">Crear Cuenta</h2>
    <form method="post" action="${pageContext.request.contextPath}/users">
        <div class="form-grid">
            <div>
                <label>Nombre</label>
                <input type="text" name="firstName" required>
            </div>
            <div>
                <label>Apellido</label>
                <input type="text" name="lastName">
            </div>
            <div class="full">
                <label>Correo Electrónico</label>
                <input type="email" name="email" required>
            </div>
            <div class="full">
                <label>Contraseña</label>
                <input type="password" name="passwordHash" required>
            </div>
            <div>
                <label>Teléfono</label>
                <input type="tel" name="phone">
            </div>
            <div>
                <label>Rol de Usuario</label>
                <select name="roleId" required>
                    <option value="1">Cliente</option>
                    <option value="2">Administrador</option>
                </select>
            </div>
        </div>
        <button type="submit">Registrar Usuario</button>
    </form>
    <a href="${pageContext.request.contextPath}/" class="back-link">← Cancelar y volver</a>
</div>
</body>
</html>