# Historial de commits del proyecto

El repositorio Git local incluido en el ZIP conserva **15 commits** con mensajes descriptivos en español. Para revisarlos, abrir una terminal en la carpeta del proyecto y ejecutar:

```bash
git log --oneline
```

| N.º | Mensaje del commit | Cambio incluido |
|---:|---|---|
| 1 | `Configura proyecto Maven para Java 17 y Apache NetBeans` | Configuración inicial de Maven, Java 17, scripts y JUnit 5. |
| 2 | `Implementa entidades y reglas de negocio del Centro de Salud Santa Rosa` | Modelo de pacientes, citas, atenciones, medicamentos, fábrica, servicio y persistencia cifrada. |
| 3 | `Construye ventana principal con pestañas de gestión` | Ventana Swing con navegación por pestañas y estilo claro de referencia. |
| 4 | `Añade registro y búsqueda segura de pacientes` | Formulario, filtro por nombre/documento y presentación enmascarada del DNI. |
| 5 | `Mejora agenda con búsqueda, motivos y horario AM PM` | Selector de paciente, profesional, fecha, hora AM/PM, motivos y consulta de citas. |
| 6 | `Implementa inventario de tabletas y alcohol medicinal` | Alta de productos, vencimientos y movimientos de existencias. |
| 7 | `Genera comprobantes de cita imprimibles y reportes agregados` | Vista tipo recibo, impresión, exportación TXT y resumen estadístico. |
| 8 | `Amplía pruebas automatizadas de servicios y persistencia` | Pruebas para DNI duplicado, citas, inventario, reportes, atenciones y cifrado. |
| 9 | `Documenta requerimientos y criterios de aceptación del sistema` | Requerimientos funcionales, restricciones y criterios verificables. |
| 10 | `Describe paradigmas, patrones de diseño y modelo UML` | Comparación de paradigmas, SOLID, Singleton, Factory y clases UML. |
| 11 | `Documenta el inventario ficticio de medicamentos e insumos` | Lista de productos de demostración, incluidas tabletas y alcohol medicinal. |
| 12 | `Agrega guías de ejecución, pruebas y navegación visual` | Pasos para NetBeans, pruebas automatizadas y distribución de la interfaz. |
| 13 | `Actualiza README y adjunta capturas de los módulos` | Instrucciones generales y capturas verificables de las cuatro pestañas. |
| 14 | `Registra el historial de commits descriptivos del proyecto` | Este documento y la relación de los cambios versionados. |
| 15 | `Simplifica inventario y elimina campos no usados en exposición` | Quita de la interfaz el código visible, lote, precio y detalle del movimiento; actualiza persistencia, datos ficticios y capturas. |

**Importante:** los commits están guardados en el repositorio local del ZIP. Para que también aparezcan en GitHub, el proyecto debe conectarse al repositorio remoto correspondiente y ejecutar `git push`; esta copia no se ha publicado en GitHub.
