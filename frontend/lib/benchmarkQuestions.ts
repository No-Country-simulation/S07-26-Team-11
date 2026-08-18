/**
 * Banco de preguntas del Maturity Benchmark: 4 dimensiones x 2 preguntas, calcadas del
 * diseno en Figma ("Flujo 1 y 2 - Panel publico", pantallas "Maturity Benchmark").
 *
 * Las preguntas de Visibilidad (pregunta 1) y Automatizacion (pregunta 2) son texto real
 * capturado del diseno. Las otras 6 (Visibilidad pregunta 2, Eficiencia energetica,
 * Gobernanza de datos, Automatizacion pregunta 1) no estaban en las pantallas exportadas:
 * se escribieron ad-hoc siguiendo el mismo estilo y estructura (4 opciones, de la practica
 * mas inmadura a la mas madura). Reemplazar aca si el equipo tiene el texto definitivo.
 *
 * `score` por opcion (0/33/66/100) es una escala propia para este MVP: el diseno no
 * especifica puntajes numericos, solo el texto de las opciones.
 */

export interface BenchmarkOption {
  label: string;
  score: 0 | 33 | 66 | 100;
}

export interface BenchmarkQuestion {
  id: string;
  dimensionCode: DimensionCode;
  /** "Pregunta N de 2" dentro de su dimension. */
  orderInDimension: 1 | 2;
  text: string;
  options: BenchmarkOption[];
}

export type DimensionCode = "VISIBILIDAD" | "EFICIENCIA" | "GOBERNANZA" | "AUTOMATIZACION";

export interface Dimension {
  code: DimensionCode;
  /** Como aparece en el stepper de 4 pasos. */
  stepLabel: string;
  /** Como aparece como eyebrow arriba de la pregunta. */
  categoryLabel: string;
}

export const DIMENSIONS: Dimension[] = [
  { code: "VISIBILIDAD", stepLabel: "Visibilidad", categoryLabel: "VISIBILIDAD" },
  { code: "EFICIENCIA", stepLabel: "Eficiencia energética", categoryLabel: "EFICIENCIA ENERGÉTICA" },
  { code: "GOBERNANZA", stepLabel: "Gobernanza de datos", categoryLabel: "GOBERNANZA DE DATOS" },
  { code: "AUTOMATIZACION", stepLabel: "Automatización", categoryLabel: "AUTOMATIZACIÓN" },
];

export const BENCHMARK_QUESTIONS: BenchmarkQuestion[] = [
  {
    id: "visibilidad-1",
    dimensionCode: "VISIBILIDAD",
    orderInDimension: 1,
    text: "¿Con qué granularidad podés ver la utilización real de cómputo por carga de trabajo?",
    options: [
      { label: "No tenemos visibilidad más alla del consumo eléctrico total del sitio.", score: 0 },
      { label: "Vemos utilización agregada por rack o fila, sin detalle por carga.", score: 33 },
      { label: "Tenemos dashboards por servidor, actualizados diariamente.", score: 66 },
      { label: "Monitoreo en tiempo real por carga de trabajo, con alertas automáticas.", score: 100 },
    ],
  },
  {
    id: "visibilidad-2",
    dimensionCode: "VISIBILIDAD",
    orderInDimension: 2,
    text: "¿Cómo se enteran los responsables del sitio de que hay capacidad ociosa?",
    options: [
      { label: "No hay un mecanismo: se descubre por casualidad o al recibir la factura.", score: 0 },
      { label: "Un informe manual, armado a pedido cada tanto.", score: 33 },
      { label: "Un reporte periódico automático, revisado por el equipo.", score: 66 },
      { label: "Un dashboard vivo con alertas cuando la subutilización supera un umbral.", score: 100 },
    ],
  },
  {
    id: "eficiencia-1",
    dimensionCode: "EFICIENCIA",
    orderInDimension: 1,
    text: "¿Cómo se dimensiona la capacidad instalada frente a la carga real esperada?",
    options: [
      { label: "Sobredimensionamos con un margen grande, sin un criterio formal.", score: 0 },
      { label: "Hay un margen estándar aplicado a todos los sitios por igual.", score: 33 },
      { label: "El margen se ajusta por tipo de carga, con revisión anual.", score: 66 },
      { label: "El dimensionamiento se recalcula con datos de uso real de cada sitio.", score: 100 },
    ],
  },
  {
    id: "eficiencia-2",
    dimensionCode: "EFICIENCIA",
    orderInDimension: 2,
    text: "¿Qué tan seguido se revisa el PUE (eficiencia energética) del sitio?",
    options: [
      { label: "No lo medimos.", score: 0 },
      { label: "Se mide una vez al año, para el reporte anual.", score: 33 },
      { label: "Se mide mensualmente y se compara contra el mes anterior.", score: 66 },
      { label: "Se monitorea en tiempo real y dispara acciones correctivas.", score: 100 },
    ],
  },
  {
    id: "gobernanza-1",
    dimensionCode: "GOBERNANZA",
    orderInDimension: 1,
    text: "¿Quién es responsable de dar de baja una carga que ya no se usa?",
    options: [
      { label: "Nadie en particular: queda encendida hasta que alguien pregunta.", score: 0 },
      { label: "El equipo de infraestructura, cuando tiene tiempo.", score: 33 },
      { label: "Cada carga tiene un dueño, pero la baja no tiene un proceso formal.", score: 66 },
      { label: "Cada carga tiene un dueño formal y una política de baja con revisión periódica.", score: 100 },
    ],
  },
  {
    id: "gobernanza-2",
    dimensionCode: "GOBERNANZA",
    orderInDimension: 2,
    text: "¿Existe un inventario actualizado de qué carga corre en cada rack?",
    options: [
      { label: "No, o está desactualizado.", score: 0 },
      { label: "Existe una planilla que se actualiza manualmente cada tanto.", score: 33 },
      { label: "Hay un sistema de inventario, aunque con datos parcialmente desactualizados.", score: 66 },
      { label: "El inventario se sincroniza automáticamente con la infraestructura real.", score: 100 },
    ],
  },
  {
    id: "automatizacion-1",
    dimensionCode: "AUTOMATIZACION",
    orderInDimension: 1,
    text: "¿Cómo se apagan o suspenden los ambientes que no están en uso (desarrollo, pruebas, etc.)?",
    options: [
      { label: "No se apagan nunca: quedan encendidos 24/7.", score: 0 },
      { label: "Se apagan manualmente, cuando alguien se acuerda.", score: 33 },
      { label: "Hay horarios programados para algunos ambientes.", score: 66 },
      { label: "El apagado programado cubre la mayoría de los ambientes no productivos.", score: 100 },
    ],
  },
  {
    id: "automatizacion-2",
    dimensionCode: "AUTOMATIZACION",
    orderInDimension: 2,
    text: "¿Existen procesos automáticos para identificar y recuperar capacidad fantasma?",
    options: [
      { label: "Todo el escalado es manual.", score: 0 },
      { label: "Hay reglas básicas de alertas, pero la acción es manual.", score: 33 },
      { label: "Auto escalado parcial en algunos ambientes críticos", score: 66 },
      { label: "Auto escalado extendido a la mayoría de las cargas, con políticas definidas.", score: 100 },
    ],
  },
];
