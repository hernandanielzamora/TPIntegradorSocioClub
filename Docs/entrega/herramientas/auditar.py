"""Auditoría reproducible de fuentes, contratos y resultados; no ejecuta SQL."""
from pathlib import Path
import json,re,subprocess
from pypdf import PdfReader
from xml.etree import ElementTree as ET
ROOT=Path(__file__).resolve().parents[3];OUT=ROOT/'Docs/entrega'
def read(p):return (ROOT/p).read_text(encoding='utf8')
def requests(items):
 for i in items:
  if 'request' in i:yield i
  yield from requests(i.get('item',[]))
c=json.loads(read('postman/SocioClub.postman_collection.json'));e=json.loads(read('postman/SocioClub-local.postman_environment.json'));reqs=list(requests(c['item']))
controller=read('backend/src/main/java/ar/edu/siglo21/socioclub/controller/api/ApiController.java')
endpoints=[('GET','/actividades'),('GET','/actividades/{id}'),('POST','/actividades'),('PUT','/actividades/{id}'),('DELETE','/actividades/{id}'),('GET','/socios/dni/{dni}'),('GET','/socios/{id}/inscripciones'),('POST','/inscripciones'),('PUT','/inscripciones/{id}/baja')]
for verb,path in endpoints:
 assert re.search(r'@'+verb.title()+r'Mapping\((?:value\s*=\s*)?"'+re.escape(path)+'"',controller),(verb,path)
 def normalized(url):
  if isinstance(url,dict):url=url.get('raw','')
  return re.sub(r'\{\{[^}]+\}\}', '{}',url)
 target=re.sub(r'\{[^}]+\}','{}','/api'+path)
 assert any(r['request']['method']==verb and target in normalized(r['request']['url']) for r in reqs),(verb,path,'Postman')
selected={'ApiControllerTests','ApiConsultaServiceTests','SecretariaControllerTests','SecretariaServiceTests','SocioBajaServiceTests','InscripcionServiceTests','IntegridadModeloTests','SocioClubApplicationTests','PersistenciaMySqlTests','InscripcionServiceMySqlTests','PreinscripcionControllerTests','PreinscripcionMySqlTests'}
totals={k:0 for k in ['tests','failures','errors','skipped']};classes=[]
for f in (ROOT/'backend/target/surefire-reports').glob('TEST-*.xml'):
 xml=ET.parse(f).getroot()
 if xml.attrib['name'].split('.')[-1] in selected:
  classes.append({'clase':xml.attrib['name'],**{k:int(xml.attrib[k]) for k in totals}})
  for k in totals:totals[k]+=int(xml.attrib[k])
assert len(classes)==12 and totals==dict(tests=136,failures=0,errors=0,skipped=0),totals
def log(name):
 raw=(ROOT/'.cache'/name).read_bytes()
 return raw.decode('utf-16' if raw.startswith((b'\xff\xfe',b'\xfe\xff')) else 'utf8',errors='replace')
react=log('rf3-react.txt');site=log('rf3-sitio.txt')
assert re.search(r'Tests:\s+58 passed, 58 total',react)
assert re.search(r'Test Suites:\s+5 passed, 5 total',react)
assert re.search(r'tests\s+29',site) and re.search(r'pass\s+29',site) and re.search(r'fail\s+0',site)
assert (ROOT/'app-socios/build/index.html').is_file()
browser=json.loads(read('Docs/entrega/evidencias/verificacion-navegador.json'));assert not browser['errors'];assert not any(c['overflow'] for c in browser['checks'])
newman=json.loads(read('Docs/entrega/evidencias/newman-resultados.json'));assert not newman['failures']
system=json.loads(read('Docs/entrega/evidencias/casos-sistema-ejecutados.json'));assert len(system['casos'])==6 and all(c['resultado']=='APROBADO' for c in system['casos'])
assert 'Compiled successfully' in log('rf3-build.txt')
assert 'BUILD SUCCESS' in log('rf3-backend-package.txt')
pages=len(PdfReader(ROOT/'Docs/entrega/TPI_SocioClub_Grupo3.pdf').pages);assert 8<=pages<=12
result={'backend':totals,'mysql_tests':sum(c['tests'] for c in classes if c['clase'].endswith('MySqlTests')),'clases_backend':classes,'react':{'tests':58,'suites':5,'fallos':0,'fuente':'Jest real, .cache/rf3-react.txt'},'sitio':{'tests':29,'fallos':0,'fuente':'.cache/rf3-sitio.txt'},'build_backend':'Maven package BUILD SUCCESS; 136 tests aprobados previamente, empaquetado final sin repetir tests','build_react':'Compiled successfully, salida 0','postman':{'json_valido':True,'requests':len(reqs),'contratos_obligatorios':9,'runner_ejecutado':True,'runner':newman['runner'],'aserciones':newman['stats']['assertions']['total'],'fallos':0},'cors':'MockMvc permitido/ajeno y OPTIONS HTTP real desde sitio público; CORS React comprobado por recorridos reales','e2e':{c['id']:c['resultado'] for c in system['casos']},'rf3':json.loads(read('Docs/entrega/evidencias/rf3-recepcion.json')),'navegador':{'capturas_png':len(list((OUT/'evidencias').glob('*.png'))),'desbordes_finales':0,'excepciones_js':0,'herramienta':browser['browser']},'pdf_paginas':pages,'sql_ejecutado':'Solo instancia nueva aislada 3308, elegida por el usuario','bases_originales_modificadas':False,'repositorio_publico':True,'salvedad_academica':'Solo Hernán, Matias y Mateo tienen aportes. El usuario confirmó ausencia de aportes de Fernando y Joaquín y pidió continuar sin inventar autoría.','main':'No modificada; requiere autorización expresa'}
(OUT/'verificaciones.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
print(json.dumps(result,ensure_ascii=False))
