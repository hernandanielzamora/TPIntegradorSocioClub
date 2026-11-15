# Entrega técnica final — Grupo 3

RF3 está resuelto: solicitudes persistentes en MySQL y bandeja en Secretaría. Pruebas completas y evidencias reales. **Salvedad académica:** solo tres integrantes tienen aportes; el usuario confirmó la ausencia de aportes de Fernando y Joaquín y pidió continuar, sin inventar autoría. main permanece intacta porque requiere autorización expresa. Entregar la rama dev.

- [PDF, doce páginas](TPI_SocioClub_Grupo3.pdf), [DOCX editable](informe-tecnico-final.docx) y [Markdown](informe-tecnico-final.md).
- [Checklist y rúbrica](checklist-final.md), [matriz](matriz-auditoria.md), [resultados nuevos](verificaciones.json) y [pruebas de sistema](../pruebas-sistema.md).
- [RF3: contrato, pantalla y migración](../preinscripciones.md).
- [Evidencias reales](evidencias/README.md).
- [Instalación desde cero](../instalacion-desde-cero.md) y [README raíz](../../README.md).

El ZIP y el manifest SHA-256 se generan en entrega-final/, fuera del índice Git. La carpeta fuente y una extracción de comprobación acompañan el paquete. No incluyen .git, .env, node_modules, target, build, caches ni logs. La versión anterior se conserva en una subcarpeta version-anterior-*.

## Regenerar documentos y paquete

Desde la raíz, instalar las dependencias documentales en el entorno Python elegido: reportlab, python-docx, pypdf, PyMuPDF y Pillow. No son dependencias de las aplicaciones. El generador utiliza Arial de Windows y dibuja los diagramas vectoriales en el PDF.

```powershell
python Docs/entrega/herramientas/generar_informe.py
python Docs/entrega/herramientas/auditar.py
python Docs/entrega/herramientas/auditar_secretos.py
```

La auditoría requiere los reportes y logs de las nuevas ejecuciones; no debe ejecutarse sin volver a preparar una instancia de ensayo nueva. No ejecutar indiscriminadamente las suites MySQL sobre una base con fixtures. La paginación de 8–12 se valida sobre el PDF, no sobre Word. El DOCX es editable y su paginación puede variar según el editor.

Después de revisar e incluir todas las fuentes/evidencias en Git y crear el commit:

```powershell
python Docs/entrega/herramientas/empaquetar.py
```

La auditoría previa coteja los nueve contratos. El empaquetador usa git ls-files, conserva las entregas anteriores dentro del directorio de entrega, verifica exclusiones, crea el ZIP, lo abre y extrae y compara todos los archivos mediante SHA-256. No se borra una entrega anterior.

## Instalación y prueba de RF3

Base nueva: schema.sql incluye preinscripcion; seed.sql se carga una vez. Base anterior: respaldo y 004_preinscripcion.sql antes del nuevo arranque. 002/003 NO se ejecutan. En este cierre solo se usó MySQL nuevo en 3308; las bases originales no se alteraron.

El formulario tiene data-api-base=http://localhost:8080/api; cambiarlo si el backend utiliza otra URL y configurar CORS según esa instalación. Ejecutar el sitio con HTTP en localhost:5500; React en localhost:3000; backend en localhost:8080. Secretaría lista las solicitudes en /secretaria/preinscripciones.

Playwright se utilizó desde la instalación existente de la computadora. Para repetir las capturas puede instalarse aparte y ejecutar herramientas/verificar_sistema.cjs capturas. Las escrituras de las pruebas requieren exclusivamente un entorno aislado y datos aprobados. El modo e2e usa los IDs del archivo escenarios.json; no reutilizarlos a ciegas en otra base. Newman ejecuta la colección con un entorno deliberadamente preparado; ver postman/README.md.

## Publicación futura de main

Únicamente tras autorización expresa:

```powershell
git fetch origin
git switch main
git merge --ff-only origin/main
git merge --no-ff origin/dev -m 'release: integrar entrega final SocioClub Grupo 3'
git diff --check
git push origin main
```

No usar force push ni reset --hard. Ante divergencia o conflicto, revisar antes de continuar. El repositorio es público y se verificó acceso anónimo; no se cambiaron permisos.
