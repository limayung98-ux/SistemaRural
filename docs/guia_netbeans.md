# Abrir el proyecto en Apache NetBeans

1. Extraer el ZIP `SistemaRural_SantaRosa.zip` en una carpeta de trabajo.
2. Abrir Apache NetBeans.
3. Seleccionar **File → Open Project** y abrir la carpeta que contiene `pom.xml`.
4. Si NetBeans solicita resolver dependencias, aceptar y esperar a que Maven termine.
5. Ejecutar el proyecto con **Run Project**. La clase principal es `pe.santarosa.Main`.
6. Para las pruebas, seleccionar **Test** sobre el proyecto o ejecutar `mvn test` en la terminal.

## Ejecución desde Windows

- Doble clic en `ejecutar.bat` para abrir el sistema.
- Doble clic en `probar.bat` para ejecutar JUnit 5.

Se necesita JDK 17 o superior y Maven instalado y reconocido por la terminal. Si aparece `mvn no se reconoce`, configurar la variable `MAVEN_HOME` con la carpeta de Maven y agregar `%MAVEN_HOME%\\bin` al `Path` de Windows. Reiniciar la terminal después del cambio.
