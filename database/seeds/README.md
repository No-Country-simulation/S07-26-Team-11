# seeds/ — Datos semilla

Datos necesarios para que el sistema funcione y datos sinteticos para desarrollo.

| Archivo | Que contiene | Cuando se carga |
|---|---|---|
| `benchmark_instrument_v1.sql` | Dimensiones, preguntas, opciones y pesos del benchmark | Siempre. Sin esto el benchmark no existe |
| `admin_user_dev.sql` | Un usuario interno para desarrollo local | Solo en local |
| `sample_responses.sql` | Respuestas sinteticas para probar percentiles y el PDF | Solo en local |

**Reglas:**

1. **Nunca datos reales de personas.** Ni siquiera correos de los integrantes del equipo.
2. **Nunca contrasenas reales**, ni siquiera hasheadas de una cuenta que exista en otro lado.
3. El instrumento del benchmark depende de que el cliente entregue las preguntas y los pesos.
   Mientras no lleguen, se usa una version provisional **marcada claramente como provisional**.
4. Para probar percentiles hace falta una cohorte de al menos 20 respuestas sinteticas:
   con menos, la regla anti-desanonimizacion (cohorte menor que 5) oculta el percentil
   y no se puede validar la pantalla de resultados.
