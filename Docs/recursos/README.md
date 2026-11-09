# Recursos documentales y exportación

Tres diagramas originales basados en el código de dev 874c6bb: arquitectura, modelo ER y secuencia de inscripción. SVG es la fuente vectorial; PNG se incorpora en Markdown/DOCX/PDF. Son diagramas explicativos, no capturas de ejecución.

El exportador `generar_documentos.py` solo lee documentos y escribe estos recursos y los entregables de Docs/entrega y Docs/defensa. No abre .env, ejecuta SQL, importa el backend ni modifica dependencias de las aplicaciones. Usa Arial/Consolas de Windows; las fuentes se incrustan en PDF. En otro sistema hay que adaptar su ruta.

Herramientas de documentación utilizadas en instalación aislada `.cache/doc-tools`: python-docx 1.2.0, reportlab 5.1.0 y pymupdf 2.1. No son dependencias de SocioClub ni se distribuye esa carpeta. Para preparar otra instalación de documentación, revisar versiones disponibles y luego ejecutar desde raíz:

```powershell
python -m pip install --target .cache/doc-tools python-docx==1.2.0 reportlab==5.1.0 pymupdf==2.1
python Docs/recursos/generar_documentos.py
```

Los formatos se generan desde Markdown. El DOCX es editable; sus saltos pueden variar en Word y requieren revisar si se exporta nuevamente desde ese programa. El informe PDF usa saltos explícitos, con control de 8–12 páginas. Cambiar imágenes o contenido requiere regenerar y repetir la inspección visual y el control de texto/tablas; no confiar únicamente en que el script finalice.

Los marcadores de captura son pendientes reales. No reemplazarlos por ilustraciones ni quitar las limitaciones de evidencia para obtener una presentación aparentemente completa.
