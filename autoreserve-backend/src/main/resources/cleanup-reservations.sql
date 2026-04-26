-- Script para limpiar reservas de prueba con car_model_id = 0
-- Ejecutar este script en la base de datos antes de reiniciar la aplicación

-- Eliminar todas las reservas con car_model_id = 0 (datos de prueba incorrectos)
DELETE FROM reservation WHERE car_model_id = 0;

-- Liberar todos los autos que puedan estar marcados como RENTED sin reserva válida
UPDATE car SET status = 'AVAILABLE' WHERE status = 'RENTED' AND id NOT IN (
    SELECT DISTINCT car_id FROM reservation WHERE car_id IS NOT NULL AND status IN ('CONFIRMED', 'IN_PROGRESS')
);

-- Verificar que no queden reservas problemáticas
SELECT COUNT(*) as reservas_restantes FROM reservation;
SELECT COUNT(*) as autos_disponibles FROM car WHERE status = 'AVAILABLE';
SELECT COUNT(*) as autos_rentados FROM car WHERE status = 'RENTED';