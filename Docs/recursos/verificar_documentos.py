"""Controles de los entregables, sin ejecutar tests de aplicación ni SQL."""
from pathlib import Path
import sys
import re
import json
import hashlib
import zipfile
from xml.etree import ElementTree as ET

ROOT=Path(__file__).resolve().parents[2]
sys.path.insert(0,str(ROOT/'.cache/doc-tools'))
import pymupdf

guide=ROOT/'Docs/defensa/guia-integral-defensa.md'
report=ROOT/'Docs/entrega/informe-tecnico-final.md'
s=guide.read_text(encoding='utf-8')
questions=re.findall(r'^### P(\d{2})\. (.+)',s,re.M)
assert [int(q[0]) for q in questions]==list(range(1,61))
modules=re.findall(r'^## (\d+)\. ',s,re.M)
assert list(map(int,modules))==list(range(1,21))
assert s.count('**Repregunta:**')==60
assert report.read_text(encoding='utf-8').rfind('## 9. Bitácora')>report.read_text(encoding='utf-8').rfind('## 8.')

# Match real snippets against source, ignoring indentation only. Proposed shell/SQL
# commands and illustrative JSON are intentionally excluded from this assertion.
sources=[]
for folder in ['backend/src','app-socios/src','sitio-publico']:
    for p in (ROOT/folder).rglob('*'):
        if p.suffix in ['.java','.js','.properties','.html','.css']:
            sources.append((p,re.sub(r'\s+','',p.read_text(encoding='utf-8-sig'))))
matched=[]
for language,code in re.findall(r'```(\w+)\n(.*?)\n```',s,re.S):
    if language not in ['java','javascript','jsx','properties','html']:continue
    normalized=re.sub(r'\s+','',code)
    hits=[str(p.relative_to(ROOT)).replace('\\','/') for p,content in sources if normalized in content]
    assert hits, 'Extracto no encontrado: '+code[:100]
    matched.append({'lenguaje':language,'fuentes':hits})

checked=[]
for relative in ['Docs/entrega/TPI_SocioClub_GrupoN.pdf','Docs/defensa/guia-integral-defensa.pdf']:
    p=ROOT/relative;pdf=pymupdf.open(str(p))
    assert len(pdf)>=8
    if 'GrupoN' in relative:
        assert len(pdf)<=12
        assert any('github.com/hernandanielzamora/TPIntegradorSocioClub' in a.get('uri','') for a in pdf[0].get_links())
        assert 'Bitácora' in pdf[-1].get_text()
    outside=[]
    for i,page in enumerate(pdf):
        for block in page.get_text('dict')['blocks']:
            for line in block.get('lines',[]):
                for span in line.get('spans',[]):
                    x0,y0,x1,y1=span['bbox']
                    if x0<0 or y0<0 or x1>page.rect.width+.5 or y1>page.rect.height+.5:outside.append((i+1,span['text']))
    assert not outside,outside
    checked.append({'archivo':relative,'paginas':len(pdf),'bytes':p.stat().st_size,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()})

for p in [report.with_suffix('.docx'),guide.with_suffix('.docx')]:
    with zipfile.ZipFile(p) as z:
        assert z.testzip() is None
        ET.fromstring(z.read('word/document.xml'))
        assert any(n.startswith('word/media/') for n in z.namelist())

for p in [guide,report,ROOT/'Docs/defensa/guion-presentacion.md']:
    content=p.read_text(encoding='utf-8')
    assert '\ufffd' not in content
    for link in re.findall(r'!\[[^]]*\]\(([^)]+)\)',content):assert (p.parent/link).exists(),link

summary={'modulos':20,'preguntas':60,'repreguntas':60,'palabras_guia':len(s.split()),'extractos_reales_verificados':matched,'pdf':checked,'docx':'ZIP/XML válidos; apertura gráfica en Word pendiente','limite':'Chequeo geométrico no sustituye inspección visual; no ejecuta pruebas funcionales'}
out=ROOT/'.cache/documentos-p23';out.mkdir(exist_ok=True)
(out/'verificacion.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(summary,ensure_ascii=True,indent=2))
