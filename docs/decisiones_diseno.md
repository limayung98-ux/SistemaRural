# Decisiones de diseño — Centro de Salud Santa Rosa

## Caso aplicado y alcance

El prototipo representa un establecimiento de salud que necesita organizar registros de pacientes, citas, atenciones y existencias de medicamentos. La interfaz de escritorio divide el trabajo en pestañas, mantiene los formularios visibles y presenta la agenda y los reportes en tablas o comprobantes legibles. El alcance es académico y local; no reemplaza una historia clínica institucional.

## Paradigmas de programación

| Paradigma | Ventajas | Desventajas | Aplicación concreta |
|---|---|---|---|
| Orientado a objetos | Encapsula atributos y reglas, y permite separar responsabilidades. | Requiere diseñar clases y relaciones. | Entidades `Paciente`, `Cita`, `Medicamento` y `Atencion`; servicio y repositorio. |
| Funcional | Simplifica filtros, transformaciones y resúmenes de colecciones. | No reemplaza validaciones ni cambios de estado. | `filter` para búsquedas y alertas; `map` y `reduce` para el valor de inventario y conteos. |
| Orientado a eventos | Responde de forma inmediata a las acciones de la interfaz. | Los listeners pueden acoplarse si concentran reglas de negocio. | Eventos de Swing para registrar, buscar, cancelar, atender y actualizar tablas. |
| Estructurado / imperativo | Facilita seguir validaciones y secuencias de persistencia. | Puede producir métodos largos si se usa sin modularidad. | Conversión de fechas, manejo de errores y lectura/escritura cifrada. |

Se integran principalmente los paradigmas orientado a objetos, funcional y orientado a eventos. Esta combinación permite modelar entidades, procesar colecciones y actualizar la interfaz a partir de acciones del usuario. La lógica de negocio queda en `ServicioSalud`, no en los botones.

## Modelo y patrones

- **Herencia:** `Paciente` y `ProfesionalSalud` derivan de `Persona`.
- **Agregación/composición:** `DatosSistema` administra colecciones de pacientes, citas, atenciones, medicamentos y movimientos.
- **Asociación:** una cita se vincula con un paciente mediante su identificador; una atención referencia una cita.
- **Singleton:** `ConfiguracionAplicacion` centraliza nombre y ubicación del establecimiento y ruta de datos.
- **Factory:** `FabricaRegistros` centraliza la creación de entidades e identificadores.

El diagrama completo se encuentra en `diagrama_clases.mmd`.

## Buenas prácticas y SOLID

1. **SRP:** las entidades modelan datos y validaciones; el servicio coordina casos de uso; el repositorio persiste; la interfaz presenta y recoge acciones.
2. **DIP:** `ServicioSalud` depende de `RepositorioSalud`, no de la implementación cifrada concreta.
3. **Encapsulamiento:** atributos privados, métodos de acceso y colecciones de solo lectura.
4. **Validación temprana:** se verifican nombre, edad, DNI, fecha, cantidad y disponibilidad de stock antes de guardar.
5. **Modularidad y legibilidad:** paquetes separados para modelo, fábrica, configuración, servicio, repositorio e interfaz.
6. **Manejo de excepciones:** errores de validación y persistencia se capturan y comunican sin cerrar inesperadamente la aplicación.

## Protección de datos y responsabilidad

El DNI se utiliza temporalmente durante el registro y se transforma con PBKDF2-HMAC-SHA256 y salt individual para comparar duplicados. Los datos locales se cifran con AES/GCM. Las tablas y reportes generales muestran el documento enmascarado o indicadores agregados. El comprobante individual contiene datos mínimos para identificar la cita y debe mantenerse bajo custodia del personal autorizado.

El prototipo no acredita cumplimiento integral de la Ley N.° 29733. La clave local se encuentra en el mismo perfil del usuario y no existe autenticación multiusuario; por ello, estas medidas son demostrativas. Antes de un despliegue real se requieren gestión independiente de claves, roles, auditoría, copias de seguridad cifradas y evaluación institucional/legal.

## Limitaciones y recomendaciones

- No existe conexión con RENIEC, MINSA, SIS u otros servicios externos.
- No se implementan autenticación por roles, recetas electrónicas ni interoperabilidad clínica.
- La persistencia es local y no está diseñada para múltiples equipos concurrentes.
- Se recomienda incorporar base de datos institucional, auditoría, gestión externa de claves y pruebas de integración antes de cualquier uso real.

## Referencias (APA 7.ª edición)

Blasco, F. (2019). *Programación orientada a objetos en Java*. Digitalia / Biblioteca UPN.

Congreso de la República del Perú. (2011). *Ley N.° 29733, Ley de Protección de Datos Personales*. https://www.leyes.congreso.gob.pe/Documentos/Leyes/29733.pdf

Martin, R. C. (2019). *Clean code: A handbook of agile software craftsmanship*. Prentice Hall.

Ministerio de Salud del Perú. (2022). *Política Nacional de Salud Digital del Perú*. https://www.gob.pe/minsa

Trejos Buriticá, O. I., & Muñoz Guerrero, L. E. (2021). *Introducción a la programación con Python*. Digitalia / Biblioteca UPN.
