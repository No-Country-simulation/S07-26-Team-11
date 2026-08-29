-- V5__seed_benchmark_data.sql
-- Seeder para el instrumento de benchmark de Data Centers.
-- Se utilizan UUID predefinidos para mantener las relaciones y la idempotencia.

DO
$$
DECLARE
instrument_id uuid := '10000000-0000-0000-0000-000000000001';

    -- Dimensiones
    dim_energy_id
uuid := '20000000-0000-0000-0000-000000000001';
    dim_infra_id
uuid := '20000000-0000-0000-0000-000000000002';
    dim_mgmt_id
uuid := '20000000-0000-0000-0000-000000000003';
    dim_sec_id
uuid := '20000000-0000-0000-0000-000000000004';

    -- Preguntas
    q_cooling_id
uuid := '30000000-0000-0000-0000-000000000001';
    q_pue_id
uuid := '30000000-0000-0000-0000-000000000002';
    q_power_id
uuid := '30000000-0000-0000-0000-000000000003';
    q_maint_id
uuid := '30000000-0000-0000-0000-000000000004';
    q_dcim_id
uuid := '30000000-0000-0000-0000-000000000005';
    q_cap_id
uuid := '30000000-0000-0000-0000-000000000006';
    q_access_id
uuid := '30000000-0000-0000-0000-000000000007';
    q_fire_id
uuid := '30000000-0000-0000-0000-000000000008';
BEGIN

    -- 1. Insertar el Instrumento Activo
INSERT INTO benchmark_instruments (id, version, is_active, published_at)
VALUES (instrument_id, '1.0.0', true, now()) ON CONFLICT (version) DO NOTHING;

-- 2. Insertar las 4 Etapas (Dimensiones)
INSERT INTO benchmark_dimensions (id, instrument_id, code, label, weight, display_order)
VALUES (dim_energy_id, instrument_id, 'ENERGY', 'Eficiencia Energética y Sostenibilidad', 0.25, 1),
       (dim_infra_id, instrument_id, 'INFRASTRUCTURE', 'Infraestructura y Redundancia', 0.25, 2),
       (dim_mgmt_id, instrument_id, 'MANAGEMENT', 'Gestión, Operación y Monitoreo', 0.25, 3),
       (dim_sec_id, instrument_id, 'SECURITY', 'Seguridad Física y Protección', 0.25, 4) ON CONFLICT
ON CONSTRAINT benchmark_dimensions_uk DO NOTHING;

-- 3. Insertar Preguntas (2 por Etapa)
INSERT INTO benchmark_questions (id, dimension_id, text, help_text, display_order)
VALUES
    -- Etapa 1: Energía
    (q_cooling_id, dim_energy_id, '¿Qué tipo de arquitectura de enfriamiento utiliza en la sala de datos?',
     'Considere la tecnología de contención de aire de mayor despliegue actual.', 1),
    (q_pue_id, dim_energy_id, '¿Cuál es la madurez en la medición del PUE (Power Usage Effectiveness)?',
     'El PUE indica la eficiencia con la que el Data Center usa la energía.', 2),
    -- Etapa 2: Infraestructura
    (q_power_id, dim_infra_id,
     '¿Qué nivel de redundancia eléctrica posee en los componentes críticos (UPS, Generadores)?',
     'Evalúe la topología eléctrica general frente a caídas de la red pública.', 1),
    (q_maint_id, dim_infra_id, '¿Cómo impactan las tareas de mantenimiento preventivo en la operación IT?',
     'Defina si requiere apagar cargas IT para mantener la infraestructura base.', 2),
    -- Etapa 3: Gestión
    (q_dcim_id, dim_mgmt_id, '¿Qué nivel de madurez tiene la gestión de su infraestructura (DCIM)?',
     'Herramientas utilizadas para monitorear y administrar recursos físicos.', 1),
    (q_cap_id, dim_mgmt_id, '¿Cómo se gestiona la proyección y planificación de la capacidad (Capacity Planning)?',
     'Enfoque para la previsión de uso de espacio, energía y refrigeración.', 2),
    -- Etapa 4: Seguridad
    (q_access_id, dim_sec_id, '¿Qué mecanismos de control de acceso físico posee en el perímetro y sala blanca?',
     'Múltiples factores reducen el riesgo de accesos no autorizados.', 1),
    (q_fire_id, dim_sec_id, '¿Qué tipo de sistema de supresión de incendios protege las salas IT?',
     'Evalúe la detección temprana y los agentes extintores utilizados.', 2) ON CONFLICT (id) DO NOTHING;

-- 4. Insertar Opciones de Respuesta
INSERT INTO benchmark_options (question_id, label, score, display_order)
VALUES
    -- Q1: Cooling
    (q_cooling_id, 'Enfriamiento perimetral clásico (retorno abierto) sin contención.', 2.50, 1),
    (q_cooling_id, 'Contención de pasillos (frío o caliente) con flujo constante.', 7.50, 2),
    (q_cooling_id, 'Contención total combinada con sistemas In-Row y controles de flujo variable.', 12.50, 3),

    -- Q2: PUE
    (q_pue_id, 'No se mide actualmente o se calcula esporádicamente de forma manual.', 0.00, 1),
    (q_pue_id, 'Medición manual periódica (PUE estimado mayor a 1.8).', 5.00, 2),
    (q_pue_id, 'Monitoreo en tiempo real y automatizado (PUE sostenido menor a 1.5).', 12.50, 3),

    -- Q3: Power Redundancy
    (q_power_id, 'Capacidad básica (N). Una falla en el suministro interrumpe la operación IT.', 2.50, 1),
    (q_power_id, 'Redundancia (N+1). Componentes de respaldo disponibles ante fallos simples.', 8.00, 2),
    (q_power_id, 'Tolerante a fallos (2N o 2N+1). Rutas de distribución activas e independientes.', 12.50, 3),

    -- Q4: Maintenance
    (q_maint_id, 'El mantenimiento requiere paradas programadas y ventanas de desconexión IT.', 2.00, 1),
    (q_maint_id, 'Mantenimiento concurrente parcial; algunos componentes aislados requieren cortes.', 7.00, 2),
    (q_maint_id, 'Mantenimiento concurrente total. Cualquier componente puede aislarse sin impacto.', 12.50, 3),

    -- Q5: DCIM
    (q_dcim_id, 'Control fragmentado mediante hojas de cálculo (Excel) y diagramas manuales.', 2.50, 1),
    (q_dcim_id, 'Sistemas de monitoreo aislados (BMS separado de redes y servidores).', 6.50, 2),
    (q_dcim_id, 'Plataforma DCIM integral con alertas predictivas y sensores distribuidos.', 12.50, 3),

    -- Q6: Capacity
    (q_cap_id, 'Crecimiento reactivo. Se expande únicamente al alcanzar umbrales críticos.', 3.00, 1),
    (q_cap_id, 'Proyecciones anuales estáticas basadas en el consumo histórico.', 7.00, 2),
    (q_cap_id, 'Gestión proactiva continua vinculada directamente al ciclo de vida del hardware IT.', 12.50, 3),

    -- Q7: Access Control
    (q_access_id, 'Cerraduras mecánicas o sistema de tarjetas simple sin registros auditables.', 2.00, 1),
    (q_access_id, 'Tarjetas de proximidad RFID y cámaras de CCTV en el perímetro básico.', 6.50, 2),
    (q_access_id, 'Doble factor biométrico, esclusas (mantrap) y CCTV integral 24/7 con retención.', 12.50, 3),

    -- Q8: Fire
    (q_fire_id, 'Extintores manuales portátiles o sistema de rociadores húmedos (agua).', 2.00, 1),
    (q_fire_id, 'Rociadores de pre-acción (tubería seca) estándar.', 6.00, 2),
    (q_fire_id, 'Detección temprana (VESDA) combinada con gas limpio extintor (ej. Novec/FM-200).', 12.50, 3);

END $$;