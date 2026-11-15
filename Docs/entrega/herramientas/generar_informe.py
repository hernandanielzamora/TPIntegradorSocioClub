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
repo='https://github.com/hernandanielzamora/TPIntegradorSocioClub/tree/dev'
members=['Hernán Zamora','Matias Jure','Mateo Hernandez','Fernando Oliva','Joaquín Mazza']
pages=[]
def page(title,blocks): pages.append((title,blocks))
def p(t):return ('p',t)
def h(t):return ('h',t)
def table(headers,rows,widths=None):return ('table',(headers,rows,widths))
def pic(name,caption,w=475):return ('pic',(name,caption,w))

page('SocioClub',[('cover',None),p('Universidad Siglo 21 · Taller de Construcción de Software'),p('Trabajo Práctico Integrador'),p('Grupo 3'),p(' · '.join(members)),p('Repositorio GitHub — versión de entrega: '+repo),p('La versión de entrega se publica en dev. El manifiesto del ZIP identifica el commit exacto. main conserva la versión inicial y no debe utilizarse para evaluar esta entrega; actualizarla requiere autorización expresa.'),h('Estado de la entrega'),p('LISTO PARA ENTREGAR, con salvedad académica de participación. RF1–RF10 y RN1–RN7 implementados; recepción pública persistente y bandeja de Secretaría verificadas con MySQL real. Se completaron pruebas automatizadas, seis casos de sistema, colección Postman y capturas de las tres interfaces.'),p('El historial acredita aportes de Hernán Zamora, Matias Jure y Mateo Hernandez. El usuario confirmó que no hubo aportes de Fernando Oliva y Joaquín Mazza y pidió continuar así. Se conservan los cinco nombres del grupo sin inventar autoría o commits. Esta salvedad frente a la consigna se mantiene explícita.'),p('Se omite la fecha de entrega por indicación del usuario. Todas las pruebas con escrituras se ejecutaron en una instancia MySQL nueva y aislada; las bases originales y el servicio MySQL84 permanecieron intactos.')])
page('2. Introducción y 3. Arquitectura',[
p('El Club Social y Deportivo Los Algarrobos, en el caso académico, reúne aproximadamente 650 socios en la zona norte de Córdoba. La gestión mediante cuadernos, planillas y mensajes produce cupos excedidos, duplicados, inscripciones fuera de edad y dificultades para detectar cuotas impagas. SocioClub concentra la consulta y la gestión de actividades para reducir esos errores.'),
p('La solución tiene tres interfaces: un sitio público con HTML, CSS y JavaScript; un sistema de Secretaría con MVC y Thymeleaf; y Mi Club, una aplicación React para socios. Secretaría y React comparten un backend Spring Boot y una base MySQL. El sitio público mantiene un catálogo inicial en JavaScript para consulta; el formulario carga los identificadores reales de la API y envía las solicitudes al mismo backend.'),
('diagram','architecture'),
h('Un recorrido que comparte reglas'),p('Cuando un socio solicita una inscripción, React envía JSON mediante Axios. El controlador REST delega en los servicios; InscripcionService verifica las reglas y los repositorios JPA persisten la operación en MySQL. Secretaría usa la misma capa de servicio desde controladores MVC. De esta forma el cupo, la edad y la morosidad se controlan igual en ambos canales.'),
h('Recepción pública integrada'),p('El formulario envía POST /api/preinscripciones con datos validados y un actividadId real. El servicio guarda una Preinscripcion en MySQL y devuelve 201 con una referencia. Secretaría consulta las solicitudes más recientes en /secretaria/preinscripciones, con paginación. La solicitud no crea un socio ni consume cupo; esos pasos pertenecen a la inscripción formal.')])
page('4. Modelo de datos',[
('diagram','entities'),
p('Socio identifica a cada persona mediante un DNI único e incluye nombre, apellido, fecha de nacimiento, contactos, estado de cuota y estado de vigencia. AL_DIA/MOROSO expresa la situación de pago; ACTIVO/INACTIVO expresa la vigencia del socio. Son conceptos distintos.'),
p('Actividad contiene disciplina, profesor, días y horario, rango de edad, cupo máximo y cuota mensual. Inscripcion relaciona un socio con una actividad y registra fecha y estado ACTIVA/BAJA. Las claves foráneas preservan el historial. Una baja lógica cambia el estado; no elimina los vínculos históricos.'),
p('Preinscripcion agrega nombre, apellido, DNI, email, actividad solicitada y fecha de recepción. Su ManyToOne apunta a Actividad, sin relación con Socio: un vecino puede enviar una solicitud sin estar registrado. La bandeja solo se expone como vista de Secretaría; no existe un GET público de sus contactos.'),h('Persistencia y datos reproducibles'),p('Las cuatro entidades usan JPA y relaciones OneToMany/ManyToOne. Los repositorios extienden JpaRepository, contienen búsquedas derivadas, consultas @Query y paginación Pageable. schema.sql define PK, FK, unicidad y restricciones; seed.sql prepara ocho actividades, 21 socios sintéticos, dos MOROSOS y Yoga con 14 de 15 plazas. El seed se ejecutó exclusivamente en la nueva instancia aislada para la demostración; sus 21 socios y ocho actividades se comprobaron antes de crear escenarios adicionales.'),
p('La migración 001 corresponde a estado de socio en bases anteriores. Debe aplicarse solo si falta esa columna, con respaldo y autorización. Un esquema nuevo ya la contiene. 004_preinscripcion agrega únicamente la nueva tabla; se ejecutó dos veces sobre el seed aislado y conservó los conteos existentes. En un esquema nuevo la tabla ya está incluida. 002/003 continúan sin ejecutar. Hibernate usa ddl-auto=validate y no ejecuta SQL automáticamente.'),
h('Decisiones de implementación'),p('Java 25 y Spring Boot 4.1.2 organizan MVC, REST, servicios y JPA. Thymeleaf utiliza fragmentos y formularios enlazados a DTO. RN1–RN5 permanecen en el servicio transaccional para evitar duplicaciones entre canales y validar antes de persistir. Los DTO JSON evitan serializar las relaciones JPA bidireccionales.'),
p('React 18 con Create React App conserva componentes funcionales, PropTypes, hooks y Context API. Axios concentra la comunicación en servicios/api.js. RN6 se calcula en JavaScript y RN7 valida el DNI en el cliente. El sitio público utiliza una clase Actividad, eventos y manipulación del DOM sin frameworks adicionales.')])
page('5. Funcionalidades y reglas de negocio',[
p('La experiencia pública cubre la presentación del club, catálogo con días, horarios, edades, profesor y precios (RF1) y filtros combinados por disciplina y edad (RF2). La preinscripción valida nombre, apellido, correo, actividad y DNI, con errores junto a cada campo, y confirma RF3 solo después de recibir HTTP 201 del backend. Un fallo de red conserva los datos y advierte que debe consultarse antes de reenviar.'),
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
h('Contrato y errores'),p('POST /api/inscripciones recibe socioId y actividadId. Una violación de negocio produce HTTP 409 con error y regla RN1–RN5. Un ID inexistente devuelve 404; entradas inválidas, 400. Crear devuelve 201 y borrar una actividad sin referencias devuelve 204. Los errores técnicos no publican SQL ni credenciales.'),
p('Las respuestas de actividades incluyen cupoDisponible. Las inscripciones devuelven la actividad anidada, sin colecciones inversas ni ciclos infinitos. El alta de actividad usa Location hacia su detalle implementado. La baja de inscripción conserva el vínculo y puede repetirse de forma idempotente.'),
h('React, Axios y CORS'),p('React centraliza GET, POST y PUT con Axios, muestra los errores de la API y actualiza sus consultas después de altas o bajas. El contexto comparte al socio entre /, /actividades, /actividades/:id y /mis-actividades. CORS admite los orígenes locales configurados y métodos explícitos; MockMvc comprueba un origen permitido y rechaza uno ajeno.'),
h('Postman y extensión existente'),p('Newman ejecutó las 25 peticiones de la colección Postman: 34 aserciones, cero fallos. POST /api/preinscripciones recibe nombre, apellido, DNI, email y actividadId; devuelve 201, 400 o 404 según el caso. Esta ruta RF3 y PUT /api/socios/{id}/baja son adicionales a los nueve mínimos. La recepción devuelve solo ID y fecha, sin contactos.')])
import json
system=json.loads((OUT/'evidencias/casos-sistema-ejecutados.json').read_text(encoding='utf8'))
cases=[[c['id'],c['precondicion'],f"HTTP {c['http']}; ACTIVAS {c['activasAntes']} → {c['activasDespues']}; cupo {c['cupoAntes']} → {c['cupoDespues']}",c['id'].lower()+'-sistema.png'] for c in system['casos']]
page('8. Pruebas y evidencia de resultados',[
p('Las nuevas ejecuciones se realizaron contra MySQL Community 8.4 en 127.0.0.1:3308, inicializado en un directorio nuevo. Las bases socioclub y socioclub_test de esa instancia son independientes de las originales. No se inició MySQL84 ni se borraron sus datos. Las claves aleatorias permanecen fuera del repositorio.'),
table(['Verificación nueva','Resultado','Alcance'],[
['Backend completo','136 aprobadas; 0 fallos/errores/omisiones.','Incluye 18 pruebas MySQL; JAR empaquetado.'],
['React Jest / RTL','58 aprobadas; cinco suites.','RN6/RN7, componentes e integración simulada.'],
['Sitio Node / jsdom','29 aprobadas.','Envío, carga de IDs, red, errores y doble clic.'],
['Build React','Compiled successfully; salida 0.','Build servido con rutas SPA para Playwright.'],
['Playwright / Chrome','33 PNG reales de las tres interfaces.','Vistas desktop/móvil sin desbordes finales.'],
['REST y CORS','MockMvc y OPTIONS real aprobados.','Origen público 5500 y React 3000.'],
['Postman / Newman','25 peticiones; 34 aserciones aprobadas.','Nueve contratos, RN1–RN5, bajas y RF3.']], [103,180,192]),
h('Seis casos de sistema ejecutados'),table(['Caso','Datos y precondición','Resultado real','Evidencia'],cases,[42,168,160,105]),
p('PS01 y PS03–PS06 recorrieron React → Axios → REST → servicio → MySQL en Chrome real. PS02 comprobó el botón deshabilitado y realizó un POST HTTP real con Playwright para verificar 409/RN1. Después de cada caso se consultaron ACTIVAS y cupos para detectar escrituras indebidas. Todos aprobaron.'),
p('RF9 también se probó desde React: confirmación de baja, HTTP 200, lista de ACTIVAS vacía y conservación de la fila BAJA. RF3 se verificó mediante envío del formulario, HTTP 201, lectura SQL y aparición en Secretaría, sin crear socios ni cambiar cupos. Evidencia detallada en Docs/entrega/evidencias/.')])

page('9. Evidencia visual · Sitio público',[
p('Capturas auténticas obtenidas con Playwright y Chrome sobre la versión ejecutada. La ilustración vectorial es un recurso de la interfaz; estas figuras son capturas reales del navegador.'),
pic('sitio-inicio.png','Inicio: nueva portada, navegación y catálogo público.',420),
pic('sitio-tarjeta.png','Tarjeta generada por JavaScript: edades, horarios, profesor y cuota.',180),
p('Se incluyen además filtros, errores RN7, confirmación con número de solicitud y vistas móviles. No se utilizan imágenes generadas para acreditar ejecución.')])
page('9. Evidencia visual · Preinscripción',[
p('El formulario obtiene las actividades de la API y envía los datos a MySQL. Playwright comprobó HTTP 201, referencia #3, fila SQL y aparición en la bandeja, sin modificar socios ni cupos.'),
pic('sitio-formulario-legible.png','Formulario real de RF3 con actividad cargada desde el backend. La confirmación se registra solo tras recibir 201.',350)])
page('9. Evidencia visual · Secretaría',[
pic('secretaria-panel-legible.png','Panel real: acceso a socios, actividades, inscripciones y preinscripciones.',350),
pic('secretaria-bandeja-legible.png','Solicitudes persistidas, ordenadas por recepción. Contactos ficticios de ensayo.',450),
p('El menú identifica la sección actual. El listado pagina diez solicitudes y conserva datos de contacto, actividad y fecha. Playwright verificó la vista móvil y llevó a corregir el desborde del menú al agregar el quinto enlace. También se capturaron socios, actividades e inscripciones.')])
page('9. Evidencia visual · Mi Club',[
pic('react-mis-actividades.png','Inscripciones activas reales del socio ficticio Agustín Torres.',400),
pic('react-cuota.png','RN6 real: subtotal $68.000; bonificación 15 %; total $57.800.',230),
p('El ingreso por DNI, el catálogo, el detalle, RN7 y los mensajes RN2–RN5 se verificaron en Chrome. Se comprobó la inscripción exitosa y su baja posterior con actualización de la lista. Los datos viajan por Axios hacia el backend; no se sustituyó la API por mocks en estos recorridos.')])

page('10. Conclusiones y control de entrega',[
p('SocioClub integra las tecnologías de la materia en un flujo común de gestión. Compartir servicios entre MVC y REST permite mantener las reglas en un lugar; usar DTO evita ciclos al serializar; separar estados de cuota y vigencia permite conservar historial con bajas lógicas. React y el sitio público aplican estado, eventos, filtros y validación de entradas.'),
p('El trabajo conserva un historial colaborativo verificable. Git registra contribuciones de Hernán Zamora, Matias Jure y Mateo Hernandez; los cinco integrantes fueron confirmados por el usuario. El usuario confirmó que Fernando Oliva y Joaquín Mazza no realizaron aportes y pidió continuar; no se crean commits ficticios ni se atribuyen tareas no realizadas. Las decisiones humanas, las tareas automáticas y los ensayos manuales se distinguen en la bitácora y el registro de trabajo.'),
h('Rúbrica: evidencia y riesgos, sin calificación'),
table(['Categoría','Evidencia disponible','Pendiente o riesgo'],[
['A · Público · 10 pts','Tres HTML, clase JS, filtros y recepción; 29 tests.','RF3 persistido y visible en Secretaría.'],
['B · Spring · 25 pts','Capas, entidades, CRUD, RN, MVC probado.','Capturas y recorridos reales completados.'],
['C · REST · 15 pts','Nueve contratos, DTO, errores y CORS.','Newman aprobado, 25 peticiones.'],
['D · React · 25 pts','Router, Axios, contexto, props/hooks y 58 tests.','Ingreso académico, ACTIVAS y cuota reales.'],
['E · Pruebas · 10 pts','Automatizadas nuevas y PS01–PS06 ejecutados.','Seis casos E2E aprobados con evidencia.'],
['F · Docs/IA · 15 pts','Este PDF, editable, instalación y bitácora.','Salvedad: solo tres integrantes con aportes.']], [106,195,174]),
h('Archivos que se entregan'),p('El aula virtual requiere un único PDF, TPI_SocioClub_Grupo3.pdf, con el enlace GitHub en su primera página. El repositorio es público y se comprobó acceso anónimo. El ZIP complementa al PDF con fuentes, SQL, Postman y documentación. Se entrega la rama dev y el manifiesto identifica su commit exacto.'),
p('RF3 está implementado y probado. Para una base anterior se aplica 004_preinscripcion antes de iniciar el nuevo backend; para una base nueva basta schema.sql. No se actualizó main, porque requiere autorización expresa. No se desplegó el sistema ni se cambiaron permisos. La demostración permanece local y el ingreso por DNI no es autenticación robusta.'),
p('npm audit del árbol actual informó 37 vulnerabilidades: 16 bajas, 8 moderadas y 13 altas, ninguna crítica. Se conserva Create React App por consigna y no se aplicó fix --force. Este resultado y la falta de autenticación robusta son límites para un futuro despliegue, no una acreditación de seguridad del demo académico.')])
ia=[
['9 / B,E','Ejecutar persistencia y diagnosticar errores.','Análisis del 1045 y protección de credenciales de tests.','AH-1: el usuario corrigió la cuenta de ensayo en Workbench y validó conexión. Codex escribió la protección.'],
['10 / E','Conservar prueba CHECK tras fallo de aserción.','Helper que comprueba SQLException 3819 y restricción.','El usuario detectó/comunicó el fallo y aprobó alcance; Codex corrigió la aserción.'],
['11 / B,F','Evitar reconfigurar variables por terminal.','Plantilla y cargador local de .env.','AH-3: propuesta y requisitos humanos; parsing y script programados por Codex.'],
['Inc.6 / B','Revisar rechazo 1045 del arranque normal.','Diagnóstico de autenticación de desarrollo.','AH-2: el usuario corrigió contraseña local y confirmó arranque. No se cambió esquema.'],
['Fixture / E','Investigar error SQL 1064 al cargar ensayo.','Revisión y sustitución por SQL convencional.','El usuario comunicó el error y confirmó alternativa con COMMIT manual; Codex actualizó el archivo.'],
['24 / A,E,F','Cerrar entrega y mejorar el frontend.','Rediseño, regresiones, capturas y documentos.','Codex ajustó Mockito con agente explícito tras fallo real; el usuario confirmó integrantes y Grupo 3. No se atribuye revisión humana de código.']]
ia.append(['RF3 / A,B,E,F','Guardar solicitudes, mostrarlas en Secretaría y finalizar pruebas.','Entidad y tabla, API receptora, listado paginado y Playwright/Newman reales.','Codex corrigió el menú móvil y el bloqueo del JAR en Windows; el usuario eligió MySQL aislado y confirmó la salvedad de participación.'])
page('11. Bitácora de inteligencia artificial',[
p('Último capítulo del informe, conforme a Parte F. Se usa el formato real del Anexo C (páginas 13–14); la consigna menciona Anexo D en otros apartados. Las entradas se sintetizan de Docs/bitacora-ia.md, que conserva los registros extensos, evidencia y límites.'),
table(['N.º / Parte','Qué pedimos a la IA','Qué devolvió','Qué estaba mal / cambios y autoría'],ia,[54,105,133,183]),
h('Supervisión humana y autoría'),p('Se registran más de cinco intervenciones reales y al menos tres problemas o ajustes humanos documentados: autenticación de testing (AH-1), autenticación de desarrollo (AH-2), configuración local .env (AH-3) y alternativa de carga tras 1064. Son correcciones operativas y decisiones de diseño. No se reinterpretan como programación manual ni se garantiza que la cátedra considere las dos cuentas como ajustes distintos.'),
p('La IA generó y corrigió código, pruebas y documentación por instrucciones del usuario. Los respaldos y validaciones manuales comunicados pertenecen a antecedentes humanos; no se presentan como ejecuciones nuevas de Codex. Los unitarios usan Mockito y RTL usa mocks de Axios; las 18 pruebas MySQL y los recorridos Playwright/Newman de este cierre utilizaron persistencia y API reales.'),
p('El aprendizaje principal es revisar el resultado contra la consigna y contra evidencia ejecutada. Un build exitoso no prueba persistencia; un JSON válido no prueba Postman; una validación de formulario no prueba recepción. En este cierre se agregaron persistencia, comprobaciones SQL, recorridos de navegador y ejecución de la colección para acreditar ese flujo de extremo a extremo.')])

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
  box(0,115,145,55,'Sitio público',['HTML / CSS / JS','Catálogo inicial + RF3 por API'])
  box(165,115,145,55,'Secretaría',['MVC / Thymeleaf'])
  box(330,115,145,55,'Mi Club',['React / Axios / JSON'])
  d.add(Line(72,115,72,67,strokeColor=colors.HexColor(GREEN)));d.add(Line(72,67,165,67,strokeColor=colors.HexColor(GREEN)));d.add(String(20,90,'RF3 / JSON',fontName='Body',fontSize=8))
  box(165,40,310,57,'Spring Boot',['MVC y REST → servicios → JPA','RN1–RN5 compartidas']);arrow(237,115,237,97);arrow(402,115,402,97)
  box(242,-3,155,32,'MySQL',[]);arrow(320,40,320,29)
 else:
  box(0,55,140,110,'Socio',['PK id · DNI único','fechaNacimiento','estadoCuota / estado','OneToMany inscripciones'])
  box(167,55,140,110,'Inscripcion',['PK id · fecha','FK socio_id','FK actividad_id','ACTIVA / BAJA'])
  box(335,55,140,110,'Actividad',['PK id · disciplina','edades / cupo','horario / cuota','OneToMany inscripciones'])
  d.add(Line(140,107,167,107,strokeColor=colors.HexColor(GREEN)));d.add(String(145,116,'1 : N',fontName='Body',fontSize=8))
  d.add(Line(307,107,335,107,strokeColor=colors.HexColor(GREEN)));d.add(String(309,116,'N : 1',fontName='Body',fontSize=8))
  box(0,0,240,42,'Preinscripcion',['Contacto, DNI, fecha · FK actividad_id']);d.add(Line(240,20,405,20,strokeColor=colors.HexColor(GREEN)));d.add(Line(405,20,405,55,strokeColor=colors.HexColor(GREEN)));d.add(String(310,27,'N : 1',fontName='Body',fontSize=8))
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
 c.setStrokeColor(colors.HexColor('#dce1d4'));c.line(55,40,540,40);c.setFont('Body',8);c.setFillColor(colors.HexColor(GRAY));c.drawString(55,27,'SOCIOCLUB · GRUPO 3 · ENTREGA TÉCNICA FINAL');c.drawRightString(540,27,str(d.page))
pdf=OUT/'TPI_SocioClub_Grupo3.pdf';SimpleDocTemplate(str(pdf),pagesize=(595.28,841.89),leftMargin=55,rightMargin=55,topMargin=48,bottomMargin=55,title='SocioClub · Grupo 3 · Informe técnico',author='Grupo 3').build(story,onFirstPage=footer,onLaterPages=footer)
# Enlace externo clicable también en el editable.
for paragraph in doc.paragraphs:
 if paragraph.text.startswith('Repositorio GitHub'):
  paragraph.clear();paragraph.add_run('Repositorio GitHub: ');link=OxmlElement('w:hyperlink');rid=paragraph.part.relate_to(repo,'http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink',is_external=True);link.set(qn('r:id'),rid);run=OxmlElement('w:r');text=OxmlElement('w:t');text.text=repo;run.append(text);link.append(run);paragraph._p.append(link)
doc.save(OUT/'informe-tecnico-final.docx');(OUT/'informe-tecnico-final.md').write_text('\n'.join(md),encoding='utf8')
r=PdfReader(pdf);assert 8<=len(r.pages)<=12,len(r.pages)
assert any(repo in str(a.get_object()) for a in r.pages[0].get('/Annots',[]))
import fitz
renders=ROOT/'.cache/informe-render';renders.mkdir(exist_ok=True)
f=fitz.open(pdf)
for i,pg in enumerate(f):pg.get_pixmap(matrix=fitz.Matrix(1,1)).save(str(renders/f'pagina-{i+1:02}.png'))
print(json.dumps({'pdf':str(pdf),'paginas':len(r.pages),'docx':str(OUT/'informe-tecnico-final.docx')},ensure_ascii=False))
