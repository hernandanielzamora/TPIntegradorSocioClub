"""Auditoría estática de archivos seleccionados por Git, sin imprimir coincidencias."""
from pathlib import Path
import subprocess,re,json,zipfile
from pypdf import PdfReader
ROOT=Path(__file__).resolve().parents[3]
files=[f for f in subprocess.check_output(['git','ls-files','-z'],cwd=ROOT).decode('utf8').split('\0') if f]
patterns=[r'-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----',r'gh[pousr]_[A-Za-z0-9]{30,}',r'github_pat_[A-Za-z0-9_]{40,}',r'AKIA[A-Z0-9]{16}',r'sk-(?:proj-)?[A-Za-z0-9_-]{40,}']
for name in files:
 p=Path(name)
 assert not set(p.parts)&{'.git','node_modules','target','build','.cache','entrega-final'},name
 assert p.name!='.env' and (not p.name.startswith('.env.') or p.name=='.env.example'),name
 f=ROOT/p
 if p.suffix.lower()=='.png':continue
 if p.suffix.lower()=='.pdf':content='\n'.join(page.extract_text() for page in PdfReader(f).pages)
 elif p.suffix.lower()=='.docx':
  with zipfile.ZipFile(f) as z:content='\n'.join(z.read(n).decode('utf8') for n in z.namelist() if n.endswith('.xml'))
 else:content=f.read_text(encoding='utf8',errors='replace')
 assert not any(re.search(pattern,content) for pattern in patterns),'Posible secreto en '+name
assert 'backend/.env.example' in files and 'app-socios/.env.example' in files
result={'archivos_revisados':len(files),'coincidencias_patrones':0,'env_privados_incluidos':False,'generados_excluidos':True,'pdf_y_docx_inspeccionados':True,'limite':'Revisión estática por patrones conocidos y rutas; no garantiza detectar cualquier secreto.'}
(ROOT/'Docs/entrega/auditoria-secretos.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
print(json.dumps(result,ensure_ascii=False))
