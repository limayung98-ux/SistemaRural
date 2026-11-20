# Requerimientos y restricciones — Centro de Salud Santa Rosa

## Requerimientos funcionales

1. Registrar pacientes con nombres y apellidos, DNI, edad y teléfono.
2. Validar el formato del DNI y evitar que un mismo documento se registre dos veces.
3. Buscar pacientes por nombre o por los dígitos visibles del documento enmascarado.
4. Programar citas vinculadas a un paciente y un profesional de salud.
5. Seleccionar fecha, hora AM/PM y motivo de atención desde controles de la interfaz.
6. Evitar que el mismo profesional tenga dos citas activas en la misma fecha y hora.
7. Consultar y filtrar la agenda; cancelar una cita programada.
8. Registrar una atención desde una cita y actualizar su estado.
9. Registrar tabletas, cápsulas, alcohol medicinal y otros insumos con presentación, vencimiento y existencias. La interfaz no muestra códigos internos, lotes ni precios.
10. Actualizar las existencias mediante ingresos y salidas, sin permitir stock negativo y sin solicitar un detalle libre del movimiento.
11. Generar comprobantes de cita imprimibles y exportables, y reportes estadísticos agregados.

## Restricciones de diseño

- Desarrollar con Java OpenJDK 17 o superior, Swing, Maven y Apache NetBeans.
- Mantener una aplicación de escritorio local, sin depender de servicios externos para su operación básica.
- Organizar el código en capas y mantener las reglas de negocio fuera de los componentes gráficos.
- Proteger el DNI mediante hash con salt y cifrar los registros locales; evitar exponer identificadores completos en reportes generales.
- Utilizar únicamente datos ficticios en la demostración académica y respetar el alcance de la Ley N.° 29733.

## Criterios de aceptación

- Un paciente válido se registra y se muestra en la tabla con el DNI enmascarado; un DNI duplicado se rechaza.
- Una cita válida se agrega inmediatamente a la agenda con fecha y hora legibles en formato AM/PM; se rechaza un cruce de horario del profesional.
- Un movimiento de inventario actualiza el stock; el sistema bloquea una salida mayor a las existencias disponibles.
- El comprobante se puede previsualizar, imprimir y exportar sin mostrar el DNI completo.
- Las pruebas automatizadas finalizan sin fallos al ejecutar `mvn test`.
