# SistemaRural-PE — Centro de Salud Santa Rosa

**Curso:** Lenguajes de Programación — UAIN1288P  
**Tecnología:** Java OpenJDK 17+, Swing, Maven y JUnit 5  
**IDE recomendado:** Apache NetBeans

Prototipo académico de escritorio para organizar pacientes, citas, atenciones, medicamentos e informes de un establecimiento de salud. La interfaz usa pestañas y una distribución sencilla, siguiendo la referencia visual proporcionada para el Centro de Salud Santa Rosa.

## Módulos del sistema

- **Pacientes:** registro de nombres y apellidos, DNI, edad y teléfono; búsqueda por nombre o documento enmascarado. En las tablas nunca se muestra el DNI completo.
- **Citas:** selección del paciente y profesional, fecha, hora en formato AM/PM y motivo mediante una lista desplegable. La cita nueva aparece inmediatamente en la agenda. Se puede buscar, cancelar o registrar la atención.
- **Medicamentos:** registro de tabletas, cápsulas y alcohol medicinal; la pantalla muestra presentación, existencias, stock mínimo, vencimiento y estado. Permite ajustar ingresos y salidas sin mostrar código, lote, precio ni detalle del movimiento.
- **Reportes:** comprobante de cita con presentación similar a un recibo, opción de imprimir o exportar a TXT, y resumen estadístico sin nombres ni documentos de pacientes.

El comprobante es constancia interna del registro de cita; no es una factura tributaria ni una receta médica. El inventario permite llevar existencias, pero no recomienda ni prescribe medicamentos. Al iniciar por primera vez se cargan registros de demostración con nombres, documentos, teléfonos, citas y existencias ficticias.

## Abrir y ejecutar en Apache NetBeans

1. Instalar JDK 17 o superior, Apache NetBeans y Maven.
2. Descomprimir el archivo ZIP.
3. Abrir NetBeans y seleccionar **File → Open Project**.
4. Elegir la carpeta `SistemaRural_SantaRosa`, donde está el archivo `pom.xml`.
5. Esperar a que Maven descargue JUnit 5 y los complementos del proyecto.
6. Ejecutar el proyecto con **Run Project**. La clase principal es `pe.santarosa.Main`.

También se puede usar una terminal abierta en la carpeta del proyecto:

```bash
mvn clean test
mvn exec:java
```

En Windows están incluidos `ejecutar.bat` y `probar.bat` para iniciar la aplicación y ejecutar las pruebas, respectivamente.

## Pruebas automatizadas

Las pruebas JUnit 5 se encuentran en `src/test/java/pe/santarosa`. Comprueban, entre otros comportamientos, la detección de DNI duplicado, la prevención de cruces de horario, el control de stock, la protección de los documentos y la ausencia del DNI completo en reportes agregados.

Ejecutar `mvn test` y comprobar el resultado `BUILD SUCCESS`. La salida de esa ejecución debe capturarse desde NetBeans o desde la terminal para adjuntarla como evidencia de la sustentación.

## Diseño y paradigmas

- **Orientado a objetos:** `Persona`, `Paciente`, `ProfesionalSalud`, `Cita`, `Medicamento` y `Atencion` encapsulan estado y reglas.
- **Funcional:** `filter`, `map` y `reduce` realizan búsquedas, alertas y cálculos sobre colecciones.
- **Orientado a eventos:** listeners de Swing ejecutan los casos de uso desde botones, listas y pestañas.
- **Estructurado/imperativo:** las validaciones y el control de excepciones mantienen secuencias explícitas.

Se aplican los patrones **Singleton** (`ConfiguracionAplicacion`) y **Factory** (`FabricaRegistros`). `ServicioSalud` depende de la abstracción `RepositorioSalud`, lo que desacopla las reglas de negocio de la persistencia.

La documentación de apoyo está en `docs/`: requisitos, decisiones de diseño, diagrama de clases, guía de pruebas, inventario demostrativo e historial de commits. Las capturas de demostración de los cuatro módulos se encuentran en `docs/captura_pacientes_santa_rosa.png`, `docs/captura_citas_santa_rosa.png`, `docs/captura_medicamentos_santa_rosa.png` y `docs/captura_reportes_santa_rosa.png`.

## Persistencia y protección de información

Los datos se guardan en el perfil del usuario, dentro de `.sistemarural-santa-rosa`, mediante cifrado AES-256-GCM. El DNI no se almacena en texto plano: se utiliza PBKDF2-HMAC-SHA256 con un salt aleatorio por paciente para las comparaciones y solo se muestra enmascarado. Los reportes estadísticos exportables contienen datos agregados.

Estas medidas son controles demostrativos y no certifican por sí solas el cumplimiento integral de la Ley N.° 29733 ni convierten el prototipo en un sistema clínico de producción. La clave se almacena en el mismo perfil local; un despliegue real necesitaría gestión externa de claves, autenticación, roles, auditoría, respaldos cifrados y revisión institucional. Los datos precargados son ficticios y están destinados exclusivamente a la presentación académica.

## Licencia y alcance

Material académico demostrativo. No reemplaza una historia clínica electrónica, no realiza diagnósticos y no debe emplearse para decisiones clínicas reales.
