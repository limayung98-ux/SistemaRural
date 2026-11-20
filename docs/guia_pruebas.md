# Guía de pruebas automatizadas

Las pruebas utilizan JUnit 5 y están ubicadas en `src/test/java/pe/santarosa`.

| Prueba | Comportamiento verificado |
|---|---|
| DNI duplicado | Rechaza un documento ya registrado aunque el hash utilice un salt individual. |
| Cruce de horario | Impide dos citas activas con el mismo profesional en la misma fecha y hora. |
| Control de inventario | Bloquea una salida mayor que el stock y comprueba el cálculo funcional del inventario mediante `map` y `reduce`. |
| Reporte agregado | Verifica que el reporte general incluye indicadores y no expone el DNI completo. |
| Persistencia cifrada | Comprueba que los datos se recuperan y que el archivo no contiene el nombre o DNI de prueba en texto legible. |

## Ejecución

En la terminal abierta en la carpeta del proyecto:

```bash
mvn test
```

En Apache NetBeans: clic derecho sobre el proyecto → **Test**. La evidencia para la sustentación debe ser una captura real del resultado de la ejecución, mostrando el total de pruebas y `BUILD SUCCESS`.
