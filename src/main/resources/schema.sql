-- Script para crear/actualizar la tabla de tareas
-- Ejecutar solo si es necesario

-- Crear tabla de tareas si no existe
CREATE TABLE IF NOT EXISTS tareas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_limite TIMESTAMP,
    completada BOOLEAN DEFAULT FALSE,
    prioridad VARCHAR(20) DEFAULT 'MEDIA',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    materia_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE SET NULL
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_tareas_usuario_id ON tareas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_tareas_materia_id ON tareas(materia_id);
CREATE INDEX IF NOT EXISTS idx_tareas_completada ON tareas(completada);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);

-- Verificar que la tabla de materias tenga las columnas necesarias
ALTER TABLE materias ADD COLUMN IF NOT EXISTS activa BOOLEAN DEFAULT TRUE;
ALTER TABLE materias ADD COLUMN IF NOT EXISTS profesor VARCHAR(100);
ALTER TABLE materias ADD COLUMN IF NOT EXISTS horario VARCHAR(200);
