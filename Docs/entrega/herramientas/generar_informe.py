"""Genera MD, DOCX y PDF desde un mismo contenido. Ejecutar desde la raíz.
Dependencias: python -m pip install reportlab python-docx pypdf pymupdf
"""
from pathlib import Path
import sys, html, json
from xml.etree import ElementTree as ET
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak, KeepTogether
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.graphics.shapes import Drawing, Rect, String, Line, Polygon
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from pypdf import PdfReader

ROOT=Path(__file__).resolve().parents[3]
OUT=ROOT/'Docs/entrega';OUT.mkdir(parents=True,exist_ok=True)
GREEN='#163e32'; PALE='#eef2e4'; INK='#263c33'; GRAY='#536259'
repo='https://github.com/hernandanielzamora/TPIntegradorSocioClub'
members=['Hernán Zamora','Matias Jure','Mateo Hernandez','Fernando Oliva','Joaquín Mazza']
pages=[]
def page(title,blocks): pages.append((title,blocks))
def p(t):return ('p',t)
def h(t):return ('h',t)
def table(headers,rows,widths=None):return ('table',(headers,rows,widths))
def pic(name,caption,w=475):return ('pic',(name,caption,w))

page('SocioClub',[('cover',None),p('Universidad Siglo 21 · Taller de Construcción de Software'),p('Trabajo Práctico Integrador'),p('Grupo 3'),p(' · '.join(members)),p('Repositorio GitHub: '+repo),p('Versión candidata basada en dev, con cierre en feature/entrega-final. La referencia definitiva y el estado remoto se verifican en entrega-final/manifest-entrega.txt y en Git. main conserva la versión inicial; no es la versión candidata.'),h('Estado de esta entrega'),p('NO LISTO POR UN REQUISITO BLOQUEANTE. RF3 todavía no recibe ni almacena preinscripciones. El informe y el paquete existen, pero también requieren completar evidencias de Secretaría, casos de sistema y ejecución de Postman antes de presentarse como entrega final terminada.'),p('Integrantes y grupo confirmados por el usuario. Se omite la fecha de entrega por su indicación. Este documento distingue pruebas nuevas, antecedentes y validaciones pendientes.')])
page('2. Introducción y 3. Arquitectura',[
p('El Club Social y Deportivo Los Algarrobos, en el caso académico, reúne aproximadamente 650 socios en la zona norte de Córdoba. La gestión mediante cuadernos, planillas y mensajes produce cupos excedidos, duplicados, inscripciones fuera de edad y dificultades para detectar cuotas impagas. SocioClub concentra la consulta y la gestión de actividades para reducir esos errores.'),
p('La solución tiene tres interfaces: un sitio público con HTML, CSS y JavaScript; un sistema de Secretaría con MVC y Thymeleaf; y Mi Club, una aplicación React para socios. Secretaría y React comparten un backend Spring Boot y una base MySQL. El sitio público consulta un catálogo inicial en JavaScript; no debe interpretarse como disponibilidad en vivo.'),
('diagram','architecture'),
h('Un recorrido que comparte reglas'),p('Cuando un socio solicita una inscripción, React envía JSON mediante Axios. El controlador REST delega en los servicios; InscripcionService verifica las reglas y los repositorios JPA persisten la operación en MySQL. Secretaría usa la misma capa de servicio desde controladores MVC. De esta forma el cupo, la edad y la morosidad se controlan igual en ambos canales.'),
h('Límites de integración'),p('RF3 constituye el límite actual: el formulario público valida campos y DNI, pero no envía ni guarda solicitudes. La consigna usa “recibir preinscripciones” (páginas 2–3); por eso no se declara cumplimiento integral. Se propone un receptor adicional y una tabla independiente, previa aprobación de la migración. No es un endpoint del Anexo A. No se ejecutó SQL nuevo.')])
page('4. Modelo de datos',[
('diagram','entities'),
p('Socio identifica a cada persona mediante un DNI único e incluye nombre, apellido, fecha de nacimiento, contactos, estado de cuota y estado de vigencia. AL_DIA/MOROSO expresa la situación de pago; ACTIVO/INACTIVO expresa la vigencia del socio. Son conceptos distintos.'),
p('Actividad contiene disciplina, profesor, días y horario, rango de edad, cupo máximo y cuota mensual. Inscripcion relaciona un socio con una actividad y registra fecha y estado ACTIVA/BAJA. Las claves foráneas preservan el historial. Una baja lógica cambia el estado; no elimina los vínculos históricos.'),
h('Persistencia y datos reproducibles'),p('Las tres entidades usan JPA y relaciones OneToMany/ManyToOne. Los repositorios extienden JpaRepository, contienen búsquedas derivadas, consultas @Query y paginación Pageable. schema.sql define PK, FK, unicidad y restricciones; seed.sql prepara ocho actividades, 21 socios sintéticos, dos MOROSOS y Yoga con 14 de 15 plazas. Son propiedades del script revisado, no conteos nuevos de una base activa.'),
p('La migración 001 corresponde a estado de socio en bases anteriores. Debe aplicarse solo si falta esa columna, con respaldo y autorización. Un esquema nuevo ya la contiene. Los SQL 002 y 003 se conservan como propuestas no ejecutadas. Hibernate usa ddl-auto=validate y la inicialización SQL automática está desactivada.'),
h('Decisiones de implementación'),p('Java 25 y Spring Boot 4.1.2 organizan MVC, REST, servicios y JPA. Thymeleaf utiliza fragmentos y formularios enlazados a DTO. RN1–RN5 permanecen en el servicio transaccional para evitar duplicaciones entre canales y validar antes de persistir. Los DTO JSON evitan serializar las relaciones JPA bidireccionales.'),
p('React 18 con Create React App conserva componentes funcionales, PropTypes, hooks y Context API. Axios concentra la comunicación en servicios/api.js. RN6 se calcula en JavaScript y RN7 valida el DNI en el cliente. El sitio público utiliza una clase Actividad, eventos y manipulación del DOM sin frameworks adicionales.')])
page('5. Funcionalidades y reglas de negocio',[
p('La experiencia pública cubre la presentación del club, catálogo con días, horarios, edades, profesor y precios (RF1) y filtros combinados por disciplina y edad (RF2). La preinscripción valida nombre, apellido, correo, actividad y DNI, con errores junto a cada campo; RF3 permanece parcial porque no existe recepción remota.'),
p('Secretaría administra actividades y socios, ofrece búsqueda por apellido y paginación, y muestra inscriptos activos y cupo disponible (RF4, RF5 y RF7). Las altas de inscripción reutilizan las reglas del servicio (RF6). El socio ingresa por DNI, consulta sus ACTIVAS, solicita inscripciones o bajas y ve la cuota estimada (RF8–RF10). La identificación por DNI es académica y no acredita autenticación segura.'),
table(['Regla','Control implementado','Dónde se verifica'],[
['RN1','Rechazar alta sin cupo disponible.','InscripcionService; tests de servicio y API.'],
['RN2','Impedir dos ACTIVAS del socio en la misma actividad.','Servicio; conserva historial BAJA.'],
['RN3','Edad actual dentro del rango inclusivo.','Servicio; límites, cumpleaños y bisiesto.'],
['RN4','MOROSO no puede realizar nuevas inscripciones.','Servicio; permite consultas y bajas.'],
['RN5','Máximo tres inscripciones ACTIVAS.','Servicio; prueba de cuarta actividad.'],
['RN6','Suma mensual, 10 % con dos y 15 % con tres actividades.','Función pura JavaScript y Jest.'],
['RN7','DNI de siete u ocho dígitos sin puntos.','Formulario público e ingreso React.']], [42,285,148]),
h('Presentación y accesibilidad'),p('El cierre mejora el sitio público con una portada deportiva vectorial, títulos jerárquicos, paleta verde y fondo cálido, tarjetas diferenciadas por disciplina y un formulario con guía de pasos. El catálogo sigue siendo dinámico. Las vistas móviles usan una columna, la navegación indica la página actual y los controles mantienen foco visible, mensajes accesibles y enlace para saltar al contenido.'),
p('Los SVG y los detalles gráficos se construyeron en el proyecto y no representan instalaciones reales. No se incorporaron fuentes remotas, fotografías externas ni dependencias de interfaz. No se implementaron RN8 ni un panel administrativo React.')])
endpoints=[['GET','/api/actividades','200'],['GET','/api/actividades/{id}','200 / 404'],['POST','/api/actividades','201'],['PUT','/api/actividades/{id}','200 / 404'],['DELETE','/api/actividades/{id}','204 / 404 / 409'],['GET','/api/socios/dni/{dni}','200 / 404'],['GET','/api/socios/{id}/inscripciones','200 / 404'],['POST','/api/inscripciones','201 / 404 / 409'],['PUT','/api/inscripciones/{id}/baja','200 / 404']]
page('6. API REST y 7. Implementación',[
p('Los nueve endpoints mínimos del Anexo A se implementan en ApiController, bajo /api. Las consultas devuelven representaciones JSON; la API recibe DTO y delega a servicios compartidos. La lista de actividades acepta el parámetro opcional disciplina.'),
table(['Verbo','Ruta obligatoria','Respuestas del anexo'],endpoints,[55,285,135]),
h('Contrato y errores'),p('POST /api/inscripciones recibe socioId y actividadId. Una violación de negocio produce HTTP 409 con error y regla RN1–RN5. Un ID inexistente devuelve 404; entradas inválidas, 400. Crear devuelve 201, y borrar una actividad sin historial devuelve 204 sin cuerpo. Un fallo técnico de persistencia se trata sin publicar SQL ni credenciales.'),
p('Las respuestas de actividades incluyen cupoDisponible. Las inscripciones devuelven la actividad anidada, sin colecciones inversas ni ciclos infinitos. El alta de actividad usa Location hacia su detalle implementado. La baja de inscripción conserva el vínculo y puede repetirse de forma idempotente.'),
h('React, Axios y CORS'),p('React centraliza GET, POST y PUT con Axios, muestra los errores de la API y actualiza sus consultas después de altas o bajas. El contexto comparte al socio entre las cuatro rutas: /, /actividades, /actividades/:id y /mis-actividades. CORS admite los orígenes locales configurados y métodos explícitos; MockMvc comprueba un origen permitido y rechaza uno ajeno.'),
h('Postman y extensión existente'),p('La colección y el entorno local se analizaron como JSON y se contrastaron contra los nueve contratos. Sus ejemplos y scripts no son prueba de ejecución de Postman. El runner no se ejecutó en este cierre. PUT /api/socios/{id}/baja es una extensión existente del proyecto; no se cuenta entre los nueve mínimos ni resuelve RF3.')])
cases=[['PS01','Socio apto, al día, <3 ACTIVAS; vacante.','Inscribir y releer ACTIVAS/cupo.','201, una nueva ACTIVA.'],['PS02 / RN1','Cupo completo; socio apto.','POST con IDs verificados.','409 RN1, sin alta.'],['PS03 / RN2','Misma pareja ya ACTIVA.','Repetir alta y consultar.','409 RN2, sin duplicado.'],['PS04 / RN3','Edad fuera del rango.','Intentar alta y consultar.','409 RN3, sin alta.'],['PS05 / RN4','Socio ACTIVO/MOROSO.','Intentar alta y consultar.','409 RN4, sin alta.'],['PS06 / RN5','Tres ACTIVAS; destino distinto.','Intentar cuarta y consultar.','409 RN5, conserva tres.']]
page('8. Pruebas y evidencia de resultados',[
p('Las nuevas ejecuciones se separan de los antecedentes de 128 pruebas backend completos comunicados en etapas anteriores. En este cierre no se ejecutaron suites MySQL, migraciones, seed ni limpiezas de bases.'),
table(['Verificación nueva','Resultado','Alcance'],[
['Backend sin MySQL','111 aprobadas; 0 fallos/errores/omisiones.','Ocho clases; servicios, modelo, MVC y API.'],
['React Jest / RTL','58 aprobadas; cinco suites.','RN6/RN7, componentes e integración simulada.'],
['Sitio Node / jsdom','25 aprobadas.','Catálogo, filtros, DOM, formularios.'],
['Build React','Compiled successfully; salida 0.','Build local de producción.'],
['Chrome real','10 capturas; 0 desbordes y excepciones JS detectadas.','Sitio desktop/móvil e ingreso React.'],
['REST y CORS','MockMvc aprobado en la selección backend.','No sustituye llamadas con MySQL real.'],
['Postman / sistema','Pendiente de ejecución real.','JSON revisado; no equivale a runner.']], [103,180,192]),
h('Seis casos de sistema preparados'),
table(['Caso','Precondición / datos','Pasos','Esperado'],cases,[58,160,134,123]),
p('Resultado obtenido en PS01–PS06: no ejecutado E2E en este cierre. Evidencia disponible: unitarios, MockMvc y RTL; no se usa esa evidencia como resultado de sistema. Docs/pruebas-sistema.md contiene los procedimientos detallados y las consultas previas para seleccionar IDs y capturar condiciones actuales.'),
p('La base de ensayo contiene fixtures manuales según registros anteriores. Sin una base aislada aprobada no se ejecutan escrituras para construir estos escenarios. El botón React se deshabilita sin vacantes; PS02 se prepara para API/Postman. No se modifica cuota, edad ni cupo para forzar una regla sin autorización.')])
page('9. Evidencia visual · Sitio público',[
p('Capturas auténticas de Chrome local sobre los archivos actuales. La ilustración de la portada forma parte de la interfaz; las imágenes de esta sección son capturas de ejecución, no pantallas generadas artificialmente.'),
pic('sitio-inicio.png','Inicio: portada, navegación y acceso al catálogo.',420),
pic('sitio-filtros.png','Catálogo filtrado por Fútbol; resultado generado mediante DOM.',300),
p('También se incluyen capturas completas del formulario, errores de validación, catálogo general y vistas móviles de 390 px en Docs/entrega/evidencias. El registro verificacion-navegador.json identifica URL, dimensiones y comprobación de ancho. El filtro se accionó en el navegador real.')])
page('9. Evidencia visual · Mi Club y Secretaría',[
pic('react-ingreso.png','Mi Club: ingreso real del build React servido localmente.',400),
pic('react-error-dni.png','Validación RN7 real al intentar continuar sin DNI.',300),
h('Evidencia de Secretaría pendiente'),p('Backend y MySQL no estaban escuchando en los puertos locales al inspeccionar el entorno. No se fabrican capturas de Thymeleaf ni se usan servicios simulados como evidencia de MySQL. Faltan panel, socios, actividades e inscripciones; también catálogo autenticado, Mis actividades y resumen de cuota React.'),
p('Procedimiento: iniciar MySQL configurado sin ejecutar seed; cargar backend/.env con scripts/load-env.ps1 e iniciar backend en 8080. Abrir /secretaria y capturar panel, búsqueda de socios, actividades e inscripciones usando solo datos ficticios. Iniciar React con API en 8080, ingresar un DNI del seed vigente y capturar catálogo, ACTIVAS y cuota. Guardar en evidencias y regenerar el informe. Ver instrucciones exactas en evidencias/README.md.')])
page('10. Conclusiones y control de entrega',[
p('SocioClub integra las tecnologías de la materia en un flujo común de gestión. Compartir servicios entre MVC y REST permite mantener las reglas en un lugar; usar DTO evita ciclos al serializar; separar estados de cuota y vigencia permite conservar historial con bajas lógicas. React y el sitio público aplican estado, eventos, filtros y validación de entradas.'),
p('El trabajo conserva un historial colaborativo verificable. Git registra contribuciones de Hernán Zamora, Matias Jure y Mateo Hernandez; los cinco integrantes fueron confirmados por el usuario. No se atribuyen commits ni tareas técnicas a Fernando Oliva o Joaquín Mazza sin evidencia adicional. Las decisiones humanas, las tareas automáticas y los ensayos manuales se distinguen en la bitácora y el registro de trabajo.'),
h('Rúbrica: evidencia y riesgos, sin calificación'),
table(['Categoría','Evidencia disponible','Pendiente o riesgo'],[
['A · Público · 10 pts','Tres HTML, clase JS, filtros y validación; 25 tests.','RF3 sin recepción remota.'],
['B · Spring · 25 pts','Capas, entidades, CRUD, RN, MVC probado.','Capturas y recorrido MySQL actuales.'],
['C · REST · 15 pts','Nueve contratos, DTO, errores y CORS.','Runner Postman real.'],
['D · React · 25 pts','Router, Axios, contexto, props/hooks y 58 tests.','Recorrido autenticado, ACTIVAS y cuota.'],
['E · Pruebas · 10 pts','Automatizadas nuevas y PS01–PS06 preparados.','Seis resultados E2E con evidencia.'],
['F · Docs/IA · 15 pts','Este PDF, editable, instalación y bitácora.','Completar capturas y revisión humana final.']], [106,195,174]),
h('Qué entregar cuando se resuelvan los pendientes'),p('El aula virtual requiere un único PDF, TPI_SocioClub_Grupo3.pdf, con el enlace GitHub en su primera página. El docente debe poder consultar el repositorio; si es privado, requiere invitación. El ZIP es un complemento con fuentes, SQL, Postman y documentación. El paquete actual se rotula como candidato y no acredita cierre funcional completo.'),
p('RF3 requiere decidir una recepción persistente; la propuesta mínima está en rf3-decision-pendiente.md. No se actualizó main: solo puede integrarse allí con autorización expresa y una candidata validada. No se desplegó el sistema ni se cambiaron permisos del repositorio.')])
ia=[
['9 / B,E','Ejecutar persistencia y diagnosticar errores.','Análisis del 1045 y protección de credenciales de tests.','AH-1: el usuario corrigió la cuenta de ensayo en Workbench y validó conexión. Codex escribió la protección.'],
['10 / E','Conservar prueba CHECK tras fallo de aserción.','Helper que comprueba SQLException 3819 y restricción.','El usuario detectó/comunicó el fallo y aprobó alcance; Codex corrigió la aserción.'],
['11 / B,F','Evitar reconfigurar variables por terminal.','Plantilla y cargador local de .env.','AH-3: propuesta y requisitos humanos; parsing y script programados por Codex.'],
['Inc.6 / B','Revisar rechazo 1045 del arranque normal.','Diagnóstico de autenticación de desarrollo.','AH-2: el usuario corrigió contraseña local y confirmó arranque. No se cambió esquema.'],
['Fixture / E','Investigar error SQL 1064 al cargar ensayo.','Revisión y sustitución por SQL convencional.','El usuario comunicó el error y confirmó alternativa con COMMIT manual; Codex actualizó el archivo.'],
['24 / A,E,F','Cerrar entrega y mejorar el frontend.','Rediseño, regresiones, capturas y documentos.','Codex ajustó Mockito con agente explícito tras fallo real; el usuario confirmó integrantes y Grupo 3. No se atribuye revisión humana de código.']]
page('11. Bitácora de inteligencia artificial',[
p('Último capítulo del informe, conforme a Parte F. Se usa el formato real del Anexo C (páginas 13–14); la consigna menciona Anexo D en otros apartados. Las entradas se sintetizan de Docs/bitacora-ia.md, que conserva los registros extensos, evidencia y límites.'),
table(['N.º / Parte','Qué pedimos a la IA','Qué devolvió','Qué estaba mal / cambios y autoría'],ia,[54,105,133,183]),
h('Supervisión humana y autoría'),p('Se registran más de cinco intervenciones reales y al menos tres problemas o ajustes humanos documentados: autenticación de testing (AH-1), autenticación de desarrollo (AH-2), configuración local .env (AH-3) y alternativa de carga tras 1064. Son correcciones operativas y decisiones de diseño. No se reinterpretan como programación manual ni se garantiza que la cátedra considere las dos cuentas como ajustes distintos.'),
p('La IA generó y corrigió código, pruebas y documentación por instrucciones del usuario. Las ejecuciones manuales de MySQL, respaldos y validaciones comunicadas pertenecen a antecedentes humanos; no se presentan como ejecuciones nuevas de Codex. La selección backend actual usa Mockito y las pruebas React usan mocks de Axios; el consumo productivo usa la API real.'),
p('El aprendizaje principal es revisar el resultado contra la consigna y contra evidencia ejecutada. Un build exitoso no prueba persistencia; un JSON válido no prueba Postman; una validación de formulario no prueba recepción de solicitudes. Los pendientes se mantienen visibles hasta contar con resultados verificables.')])

# Mantener la implementación después de la API en el orden de capítulos.
implementation=pages[2][1][-3:]
del pages[2][1][-3:]
pages[4][1].extend([h('7. Implementación'), implementation[1]])
pages[3][1].append(implementation[2])

font=Path('C:/Windows/Fonts')
for n,f in [('Body','arial.ttf'),('Bold','arialbd.ttf')]:pdfmetrics.registerFont(TTFont(n,str(font/f)))
styles=getSampleStyleSheet();styles.add(ParagraphStyle(name='BodyLocal',fontName='Body',fontSize=9.3,leading=13.7,textColor=colors.HexColor(INK),spaceAfter=9))
styles.add(ParagraphStyle(name='TitleLocal',fontName='Bold',fontSize=22,leading=26,textColor=colors.HexColor(GREEN),spaceAfter=18))
styles.add(ParagraphStyle(name='HeadLocal',fontName='Bold',fontSize=11.2,leading=15,textColor=colors.HexColor(GREEN),spaceBefore=7,spaceAfter=7))
styles.add(ParagraphStyle(name='CellLocal',fontName='Body',fontSize=7.7,leading=10.5,textColor=colors.HexColor(INK)))
styles.add(ParagraphStyle(name='CaptionLocal',fontName='Body',fontSize=7.6,leading=10,textColor=colors.HexColor(GRAY),spaceAfter=8))
def para(text,style='BodyLocal'):
 text=html.escape(text)
 text=text.replace(html.escape(repo),f'<link href="{repo}" color="#215b44">{repo}</link>')
 return Paragraph(text,styles[style])

def diagram(kind):
 d=Drawing(475,175)
 def box(x,y,w,hh,title,detail):
  d.add(Rect(x,y,w,hh,rx=7,fillColor=colors.HexColor(PALE),strokeColor=colors.HexColor('#b7c8ac')))
  d.add(String(x+w/2,y+hh-20,title,textAnchor='middle',fontName='Bold',fontSize=10,fillColor=colors.HexColor(GREEN)))
  for i,line in enumerate(detail):d.add(String(x+w/2,y+hh-36-i*13,line,textAnchor='middle',fontName='Body',fontSize=8,fillColor=colors.HexColor(INK)))
 def arrow(x,y,xx,yy):
  d.add(Line(x,y,xx,yy,strokeColor=colors.HexColor(GREEN),strokeWidth=1.2))
  d.add(Polygon([xx,yy,xx-4,yy+7,xx+4,yy+7],fillColor=colors.HexColor(GREEN),strokeColor=None))
 if kind=='architecture':
  box(0,115,145,55,'Sitio público',['HTML / CSS / JS','Catálogo inicial · RF3 parcial'])
  box(165,115,145,55,'Secretaría',['MVC / Thymeleaf'])
  box(330,115,145,55,'Mi Club',['React / Axios / JSON'])
  box(165,40,310,57,'Spring Boot',['MVC y REST → servicios → JPA','RN1–RN5 compartidas']);arrow(237,115,237,97);arrow(402,115,402,97)
  box(242,-3,155,32,'MySQL',[]);arrow(320,40,320,29)
 else:
  box(0,55,140,110,'Socio',['PK id · DNI único','fechaNacimiento','estadoCuota / estado','OneToMany inscripciones'])
  box(167,55,140,110,'Inscripcion',['PK id · fecha','FK socio_id','FK actividad_id','ACTIVA / BAJA'])
  box(335,55,140,110,'Actividad',['PK id · disciplina','edades / cupo','horario / cuota','OneToMany inscripciones'])
  d.add(Line(140,107,167,107,strokeColor=colors.HexColor(GREEN)));d.add(String(145,116,'1 : N',fontName='Body',fontSize=8))
  d.add(Line(307,107,335,107,strokeColor=colors.HexColor(GREEN)));d.add(String(309,116,'N : 1',fontName='Body',fontSize=8))
  d.add(String(237,30,'Inscripcion es la entidad de unión; las bajas conservan las claves foráneas.',textAnchor='middle',fontName='Body',fontSize=8))
 return d

story=[];doc=Document();normal=doc.styles['Normal'];normal.font.name='Arial';normal.font.size=Pt(10)
section=doc.sections[0];section.top_margin=Inches(.65);section.bottom_margin=Inches(.65)
md=[]
for i,(title,blocks) in enumerate(pages):
 if i:story.append(PageBreak());doc.add_page_break()
 story.append(para(title,'TitleLocal'));doc.add_heading(title,0 if i==0 else 1);md.append('# '+title+'\n')
 for kind,v in blocks:
  if kind=='cover':
   story.append(Spacer(1,36));story.append(para('GESTIÓN DE SOCIOS, ACTIVIDADES E INSCRIPCIONES','HeadLocal'));story.append(Spacer(1,22));continue
  if kind in ('p','h'):
   story.append(para(v,'HeadLocal' if kind=='h' else 'BodyLocal'))
   doc.add_heading(v,2) if kind=='h' else doc.add_paragraph(v)
   md.append(('## ' if kind=='h' else '')+v+'\n')
  elif kind=='table':
   headers,rows,widths=v;data=[[para(t,'CellLocal') for t in row] for row in [headers]+rows]
   t=Table(data,colWidths=widths,repeatRows=1,hAlign='LEFT');t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),colors.HexColor('#dfe8d0')),('ROWBACKGROUNDS',(0,1),(-1,-1),[colors.white,colors.HexColor('#f5f7f0')]),('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),7),('RIGHTPADDING',(0,0),(-1,-1),7),('TOPPADDING',(0,0),(-1,-1),7),('BOTTOMPADDING',(0,0),(-1,-1),7),('LINEBELOW',(0,0),(-1,0),.7,colors.HexColor('#b7c8ac'))]));story.extend([t,Spacer(1,10)])
   dt=doc.add_table(rows=1,cols=len(headers));dt.style='Light Shading Accent 1'
   for cell,text in zip(dt.rows[0].cells,headers):cell.text=text
   for row in rows:
    for cell,text in zip(dt.add_row().cells,row):cell.text=text
   md.append('| '+' | '.join(headers)+' |\n| '+' | '.join(['---']*len(headers))+' |\n'+'\n'.join('| '+' | '.join(row)+' |' for row in rows)+'\n')
  elif kind=='pic':
   name,caption,w=v;f=OUT/'evidencias'/name
   if not f.exists():raise FileNotFoundError(f)
   from PIL import Image as PI
   with PI.open(f) as im:iw,ih=im.size
   # Las figuras de evidencia se mantienen en una página.
   img=Image(str(f),width=w,height=w*ih/iw);img.hAlign='LEFT';story.append(KeepTogether([img,para(caption,'CaptionLocal')]))
   doc.add_picture(str(f),width=Inches(w/72));doc.add_paragraph(caption,style='Caption');md.append(f'![{caption}](evidencias/{name})\n')
  elif kind=='diagram':
   d=diagram(v);story.extend([d,Spacer(1,12)])
   from reportlab.graphics import renderPM
   # El PDF mantiene diagramas vectoriales; DOCX usa una imagen renderizada del mismo diagrama.
   from reportlab.graphics import renderPDF
   import fitz
   tmp=fitz.open(stream=renderPDF.drawToString(d),filetype='pdf');f=OUT/f'diagrama-{v}.png';tmp[0].get_pixmap(matrix=fitz.Matrix(2,2)).save(str(f));tmp.close()
   doc.add_picture(str(f),width=Inches(6.2));md.append(f'![Diagrama {v}](diagrama-{v}.png)\n')

def footer(c,d):
 c.setStrokeColor(colors.HexColor('#dce1d4'));c.line(55,40,540,40);c.setFont('Body',8);c.setFillColor(colors.HexColor(GRAY));c.drawString(55,27,'SOCIOCLUB · GRUPO 3 · VERSIÓN CANDIDATA');c.drawRightString(540,27,str(d.page))
pdf=OUT/'TPI_SocioClub_Grupo3.pdf';SimpleDocTemplate(str(pdf),pagesize=(595.28,841.89),leftMargin=55,rightMargin=55,topMargin=48,bottomMargin=55,title='SocioClub · Grupo 3 · Informe técnico',author='Grupo 3').build(story,onFirstPage=footer,onLaterPages=footer)
# Enlace externo clicable también en el editable.
for paragraph in doc.paragraphs:
 if paragraph.text.startswith('Repositorio GitHub: '):
  paragraph.clear();paragraph.add_run('Repositorio GitHub: ');link=OxmlElement('w:hyperlink');rid=paragraph.part.relate_to(repo,'http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink',is_external=True);link.set(qn('r:id'),rid);run=OxmlElement('w:r');text=OxmlElement('w:t');text.text=repo;run.append(text);link.append(run);paragraph._p.append(link)
doc.save(OUT/'informe-tecnico-final.docx');(OUT/'informe-tecnico-final.md').write_text('\n'.join(md),encoding='utf8')
r=PdfReader(pdf);assert 8<=len(r.pages)<=12,len(r.pages)
assert any(repo in str(a.get_object()) for a in r.pages[0].get('/Annots',[]))
import fitz
renders=ROOT/'.cache/informe-render';renders.mkdir(exist_ok=True)
f=fitz.open(pdf)
for i,pg in enumerate(f):pg.get_pixmap(matrix=fitz.Matrix(1,1)).save(str(renders/f'pagina-{i+1:02}.png'))
print(json.dumps({'pdf':str(pdf),'paginas':len(r.pages),'docx':str(OUT/'informe-tecnico-final.docx')},ensure_ascii=False))
