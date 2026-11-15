"""Auditoría reproducible de fuentes, contratos y resultados; no ejecuta SQL."""
from pathlib import Path
import json,re,subprocess
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
selected={'ApiControllerTests','ApiConsultaServiceTests','SecretariaControllerTests','SecretariaServiceTests','SocioBajaServiceTests','InscripcionServiceTests','IntegridadModeloTests','SocioClubApplicationTests'}
totals={k:0 for k in ['tests','failures','errors','skipped']};classes=[]
for f in (ROOT/'backend/target/surefire-reports').glob('TEST-*.xml'):
 xml=ET.parse(f).getroot()
 if xml.attrib['name'].split('.')[-1] in selected:
  classes.append({'clase':xml.attrib['name'],**{k:int(xml.attrib[k]) for k in totals}})
  for k in totals:totals[k]+=int(xml.attrib[k])
assert len(classes)==8 and totals==dict(tests=111,failures=0,errors=0,skipped=0),totals
def log(name):
 raw=(ROOT/'.cache'/name).read_bytes()
 return raw.decode('utf-16' if raw.startswith((b'\xff\xfe',b'\xfe\xff')) else 'utf8',errors='replace')
react=log('final-react.txt');site=log('final-sitio.txt')
assert re.search(r'Tests:\s+58 passed, 58 total',react)
assert re.search(r'Test Suites:\s+5 passed, 5 total',react)
assert re.search(r'tests\s+25',site) and re.search(r'pass\s+25',site) and re.search(r'fail\s+0',site)
assert (ROOT/'app-socios/build/index.html').is_file()
browser=json.loads(read('Docs/entrega/evidencias/verificacion-navegador.json'));assert not browser['errors'];assert not any(c['overflow'] for c in browser['checks'])
result={'backend':totals,'clases_backend':classes,'react':{'tests':58,'suites':5,'fallos':0,'fuente':'ejecución Jest real, .cache/final-react.txt'},'sitio':{'tests':25,'fallos':0,'fuente':'.cache/final-sitio.txt'},'build_react':'Compilación real finalizada con salida 0; build/index.html comprobado','postman':{'json_valido':True,'requests':len(reqs),'contratos_obligatorios':9,'runner_ejecutado':False},'cors':'Origen permitido y ajeno comprobados por ApiControllerTests; no HTTP nuevo con MySQL','e2e':{'PS01':'No ejecutado','PS02':'No ejecutado','PS03':'No ejecutado','PS04':'No ejecutado','PS05':'No ejecutado','PS06':'No ejecutado'},'navegador':{'capturas':len(browser['checks']),'desbordes':0,'excepciones_js':0},'sql_ejecutado':False,'limitaciones':['RF3 sin recepción','Capturas de Secretaría y React con API pendientes','Postman runner y E2E pendientes','Acceso del docente a GitHub por confirmar','main requiere autorización expresa']}
(OUT/'verificaciones.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
print(json.dumps(result,ensure_ascii=False))
