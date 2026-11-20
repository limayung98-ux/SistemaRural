# Rediseño de interfaz — Santa Rosa

La ventana se reorganizó con `JTabbedPane` para conservar el patrón de navegación de la captura de referencia. El encabezado identifica el Centro de Salud Santa Rosa. Las pestañas son Pacientes, Citas, Medicamentos y Reportes.

- Pacientes: campos alineados para nombres y apellidos, DNI, edad y teléfono; búsqueda y tabla debajo del formulario.
- Citas: formulario amplio con paciente, profesional, fecha, hora AM/PM y motivos desplegables; tabla filtrable con las citas registradas.
- Medicamentos: formulario simplificado con nombre, presentación, stock inicial, stock mínimo y vencimiento. La tabla muestra medicamento, presentación, existencias, mínimo, vencimiento y estado. No se muestran código, lote, precio ni detalle del movimiento. Se retiró el panel de movimientos y no existen ventas ni selección de productos para modificar existencias; el módulo se presenta únicamente como consulta del inventario.
- Reportes: vista previa de un comprobante de cita, impresión y exportación TXT; una segunda pestaña presenta indicadores agregados.

Se priorizó mantener los controles en el área principal, ampliar los campos de selección y usar una paleta clara similar a la referencia visual.

La ventana no incluye un pie con textos de sistema local o avisos de datos. La aplicación carga registros sintéticos al iniciar por primera vez para que los módulos tengan contenido durante la exposición.

- Pacientes: se eliminó el campo y la columna de teléfono. Al completar un registro correctamente, el único mensaje de confirmación es «Paciente registrado».
- Comprobante: se retiró el texto formal anterior y se reemplazó por un agradecimiento cercano al paciente.
