<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo Auto | AutoReserve</title>
    <style>
        :root { --primary: #4f46e5; --bg: #f3f4f6; --text-main: #111827; }
        body { font-family: 'Inter', sans-serif; background: var(--bg); display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; padding: 20px; box-sizing: border-box; }

        .card {
            background: white;
            padding: 2.5rem;
            border-radius: 1rem;
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 600px;
        }

        h2 { text-align: center; color: var(--text-main); margin-top: 0; margin-bottom: 1.5rem; }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.2rem;
        }

        /* Para que ciertos campos ocupen todo el ancho si es necesario */
        .full-width { grid-column: span 2; }

        label { display: block; font-size: 0.875rem; font-weight: 600; margin-bottom: 0.5rem; color: #374151; }

        input, select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #d1d5db;
            border-radius: 0.5rem;
            box-sizing: border-box;
            font-size: 0.95rem;
            background-color: #fff;
            transition: border-color 0.2s;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
        }

        button {
            grid-column: span 2;
            width: 100%;
            padding: 0.9rem;
            background: var(--primary);
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 1rem;
            transition: background 0.2s;
        }

        button:hover { background: #4338ca; }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 1.5rem;
            color: #6b7280;
            text-decoration: none;
            font-size: 0.9rem;
        }
        .back-link:hover { color: var(--text-main); }

        /* Ajuste para móviles */
        @media (max-width: 500px) {
            .form-grid { grid-template-columns: 1fr; }
            button { grid-column: span 1; }
        }
    </style>
</head>
<body>

<div class="card">
    <h2>Registrar Vehículo</h2>
    <form method="post" action="${pageContext.request.contextPath}/cars">
        <div class="form-grid">
            <div>
                <label>Marca</label>
                <input type="text" name="brand" placeholder="Ej: Toyota" required>
            </div>
            <div>
                <label>Modelo</label>
                <input type="text" name="model" placeholder="Ej: Corolla" required>
            </div>

            <div>
                <label>Año</label>
                <input type="number" name="year" placeholder="2024" required>
            </div>
            <div>
                <label>Placa (Matrícula)</label>
                <input type="text" name="plate" placeholder="ABC-123" required>
            </div>

            <div>
                <label>Precio por día ($)</label>
                <input type="number" name="pricePerDay" step="0.01" placeholder="0.00" required>
            </div>

            <div>
                <label>Estado Inicial</label>
                <select name="status">
                    <option value="AVAILABLE">Disponible</option>
                    <option value="MAINTENANCE">Mantenimiento</option>
                </select>
            </div>

            <div class="full-width">
                <label>Categoría</label>
                <select name="categoryId" required>
                    <option value="" disabled selected>Seleccione una categoría...</option>
                    <c:forEach items="${categories}" var="cat">
                        <option value="${cat.id}">${cat.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="full-width">
                <label>Sucursal</label>
                <select name="branchId" required>
                    <option value="" disabled selected>Seleccione una sucursal...</option>
                    <c:forEach items="${branches}" var="b">
                        <option value="${b.id}">${b.name}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit">Guardar Auto</button>
        </div>
    </form>
    <a href="${pageContext.request.contextPath}/" class="back-link">← Cancelar y volver al inicio</a>
</div>

</body>
</html>