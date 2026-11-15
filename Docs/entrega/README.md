# Paquete académico candidato — Grupo 3

El cierre genera documentos reales y un paquete reconstruible. **No está listo para entregar como proyecto completo:** RF3 no recibe preinscripciones y faltan evidencias de sistema. No se ejecutaron SQL nuevos, no se alteraron bases existentes y no se modificó main.

- [Informe PDF, diez páginas](TPI_SocioClub_Grupo3.pdf).
- [Editable DOCX](informe-tecnico-final.docx) y [fuente Markdown](informe-tecnico-final.md).
- [Checklist y rúbrica](checklist-final.md), [matriz de auditoría](matriz-auditoria.md) y [verificaciones actuales](verificaciones.json).
- [Decisión necesaria para RF3](rf3-decision-pendiente.md).
- [Capturas e instrucciones para completar faltantes](evidencias/README.md).
- Instalación de las tres aplicaciones: [guía desde cero](../instalacion-desde-cero.md) y [README raíz](../../README.md).

`entrega-final/SocioClub_Entrega_Final_Grupo3.zip` se genera fuera del índice Git; junto a él quedan manifest con SHA-256, carpeta fuente y extracción de comprobación. No incluye `.git`, `.env`, node_modules, target, build, caches ni logs.

## Regeneración

Desde la raíz, con Python disponible y dependencias documentales `reportlab`, `python-docx`, `pypdf`, `pymupdf` y `Pillow` instaladas en el entorno elegido:

```powershell
python Docs/entrega/herramientas/generar_informe.py
python Docs/entrega/herramientas/auditar.py
```

El generador usa Arial de Windows, conserva diagramas vectoriales en el PDF, valida páginas y enlace de portada y produce las diez páginas renderizadas bajo `.cache/informe-render/`. El DOCX es editable; su paginación depende del procesador de texto. El requisito de 8–12 páginas se comprueba sobre el PDF final.

La auditoría necesita los reportes Surefire nuevos; antes deben ejecutarse los comandos seguros del README. Tras revisar y crear el commit candidato:

```powershell
python Docs/entrega/herramientas/empaquetar.py
```

El empaquetador usa `git ls-files`; todos los archivos nuevos que deban distribuirse deben estar incluidos en el commit. Por seguridad conserva cualquier carpeta previa de entrega y se detiene si el destino ya existe. No borra ni sobrescribe entregas anteriores.

## Publicación futura en main

No ejecutar hasta resolver los bloqueantes y recibir autorización expresa para main:

```powershell
git fetch origin
git switch main
git merge --ff-only origin/main
git merge --no-ff origin/dev -m 'release: integrar entrega final SocioClub Grupo 3'
git diff --check
# Repetir los controles seguros del README y verificar el PDF/ZIP definitivo.
git push origin main
```

Ante conflictos o divergencia, detenerse y revisar: no usar force push ni reset --hard. El informe actual identifica dev como base candidata y advierte que main conserva la versión inicial.
