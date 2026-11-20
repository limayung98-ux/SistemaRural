# Publicar el proyecto en GitHub

El proyecto incluye su carpeta `.git` y el historial completo de commits en la rama `main`. No tiene un remoto configurado, para evitar enlazarlo por error con el repositorio anterior.

1. En GitHub, crea un repositorio vacío llamado `SistemaRural_SantaRosa`. No agregues README, licencia ni `.gitignore`, porque esos archivos ya vienen en este proyecto.
2. Descomprime `SistemaRural_SantaRosa_actualizado.zip` y abre una terminal dentro de la carpeta `SistemaRural_SantaRosa`.
3. Ejecuta los comandos siguientes, reemplazando `TU_USUARIO` por tu usuario de GitHub:

```bash
git remote add origin https://github.com/TU_USUARIO/SistemaRural_SantaRosa.git
git push -u origin main
```

4. Para comprobar que los commits están incluidos antes y después de publicar:

```bash
git log --oneline
git status
```

La carpeta `.git` contiene los commits locales. El ZIP no publica automáticamente el proyecto; el `git push` lo envía al repositorio remoto que hayas creado.
