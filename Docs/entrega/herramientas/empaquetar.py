"""Crea ZIP desde fuentes versionadas, verifica contenido y extrae sin sobrescribir.
Ejecutar desde la raíz después de auditar y crear el commit candidato.
"""
from pathlib import Path
import subprocess,zipfile,hashlib,json,shutil,re,datetime
from pypdf import PdfReader
ROOT=Path(__file__).resolve().parents[3];DEST=ROOT/'entrega-final';DEST.mkdir(exist_ok=True)
NAME='SocioClub_Entrega_Final_Grupo3';SNAPSHOT=DEST/NAME
files=subprocess.check_output(['git','ls-files','-z'],cwd=ROOT).decode('utf8').split('\0');files=sorted(set(f for f in files if f))
for f in files:
 path=Path(f)
 assert not set(path.parts)&{'.git','node_modules','target','build','.cache','entrega-final'},f
 assert path.name!='.env' and (not path.name.startswith('.env.') or path.name=='.env.example'),f
 assert not path.suffix.lower() in {'.log','.pem','.key','.pfx','.p12','.sqlbak'},f
 assert not path.name.lower().startswith(('credentials.','credentials-','service-account')),f
 source=ROOT/f;assert source.is_file(),f
 if source.suffix.lower() not in {'.png','.pdf','.docx'}:
  content=source.read_text(encoding='utf8',errors='replace')
  for pattern in [r'-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----',r'gh[pousr]_[A-Za-z0-9]{30,}',r'github_pat_[A-Za-z0-9_]{40,}',r'AKIA[A-Z0-9]{16}',r'sk-(?:proj-)?[A-Za-z0-9_-]{40,}']:
   assert not re.search(pattern,content),f+' contiene posible secreto'
required=['README.md','sitio-publico/index.html','backend/pom.xml','backend/mvnw','backend/mvnw.cmd','backend/.mvn/wrapper/maven-wrapper.properties','backend/.env.example','app-socios/.env.example','app-socios/package.json','app-socios/package-lock.json','database/schema.sql','database/seed.sql','database/migrations/001_estado_socio.sql','postman/SocioClub.postman_collection.json','Docs/entrega/TPI_SocioClub_Grupo3.pdf','Docs/entrega/informe-tecnico-final.docx','Docs/entrega/checklist-final.md']
assert set(required)<=set(files),set(required)-set(files)
assert 8<=len(PdfReader(ROOT/'Docs/entrega/TPI_SocioClub_Grupo3.pdf').pages)<=12
# Se conserva una carpeta de entrega; no se elimina una carpeta existente.
if SNAPSHOT.exists():raise RuntimeError('La carpeta de entrega ya existe; conservarla y elegir destino nuevo antes de repetir.')
SNAPSHOT.mkdir()
for f in files:
 dst=SNAPSHOT/f;dst.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(ROOT/f,dst)
archive=DEST/(NAME+'.zip');assert not archive.exists()
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for f in files:z.write(SNAPSHOT/f,NAME+'/'+f)
with zipfile.ZipFile(archive) as z:
 assert z.testzip() is None
 assert len(z.namelist())==len(files)
 extraction=DEST/('verificacion-extraida-'+datetime.datetime.now().strftime('%Y%m%d-%H%M%S'));extraction.mkdir()
 for name in z.namelist():
  target=(extraction/name).resolve();assert target.is_relative_to(extraction.resolve())
 z.extractall(extraction)
for f in files:
 assert hashlib.sha256((ROOT/f).read_bytes()).digest()==hashlib.sha256((extraction/NAME/f).read_bytes()).digest(),f
pdf=extraction/NAME/'Docs/entrega/TPI_SocioClub_Grupo3.pdf';pages=len(PdfReader(pdf).pages)
sha=hashlib.sha256(archive.read_bytes()).hexdigest();commit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip();branch=subprocess.check_output(['git','branch','--show-current'],cwd=ROOT,text=True).strip()
summary={'zip':str(archive),'bytes':archive.stat().st_size,'sha256':sha,'archivos':len(files),'paginas_pdf':pages,'commit':commit,'rama_al_empaquetar':branch,'extraccion':str(extraction/NAME),'secretos':'Sin coincidencias de patrones de credenciales y exclusiones verificadas; revisión estática, no garantía universal','estado':'CANDIDATO NO LISTO: RF3 y evidencias/validaciones pendientes'}
(DEST/'verificacion-paquete.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
lines=['SocioClub · Grupo 3 · manifiesto del paquete candidato',json.dumps(summary,ensure_ascii=False,indent=2),'','CONTENIDO Y SHA-256 INDIVIDUALES']
lines.extend(hashlib.sha256((ROOT/f).read_bytes()).hexdigest()+'  '+NAME+'/'+f for f in files)
(DEST/'manifest-entrega.txt').write_text('\n'.join(lines)+'\n',encoding='utf8')
print(json.dumps(summary,ensure_ascii=False))
