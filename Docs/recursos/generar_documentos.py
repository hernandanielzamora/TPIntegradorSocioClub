"""Exportador documental local; no importa la aplicación ni accede a MySQL.

Desde raíz: python Docs/recursos/generar_documentos.py
Dependencias externas de documentación: python-docx, reportlab, pymupdf.
Puede usar una instalación aislada en .cache/doc-tools (no versionada).
"""
from pathlib import Path
import sys
import re
import textwrap
from html import escape

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / '.cache/doc-tools'))
from docx import Document
from docx.shared import Inches, Pt, Cm, RGBColor
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib import colors
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Image, Table, TableStyle, Preformatted
from reportlab.graphics.shapes import Drawing, Rect, String, Line, Polygon
from reportlab.graphics import renderPDF, renderSVG
import pymupdf

OUT = ROOT / 'Docs/recursos'
GREEN = '#18533f'
FONT_DIR = Path('C:/Windows/Fonts')
for name, file in [('Body', 'arial.ttf'), ('Bold', 'arialbd.ttf'), ('Mono', 'consola.ttf')]:
    pdfmetrics.registerFont(TTFont(name, str(FONT_DIR / file)))


def box(d, x, y, w, h, title, lines):
    d.add(Rect(x, y, w, h, rx=6, fillColor=colors.HexColor('#eff5f1'), strokeColor=colors.HexColor(GREEN)))
    d.add(String(x+w/2, y+h-20, title, fontName='Bold', fontSize=12, textAnchor='middle'))
    for i, line in enumerate(lines):
        d.add(String(x+w/2, y+h-39-i*16, line, fontName='Body', fontSize=10, textAnchor='middle'))


def arrow(d, x1, y1, x2, y2):
    d.add(Line(x1, y1, x2, y2, strokeColor=colors.HexColor(GREEN), strokeWidth=1.4))
    if x1 == x2:
        sign = 1 if y2 > y1 else -1
        pts = [x2,y2,x2-4,y2-sign*7,x2+4,y2-sign*7]
    else:
        sign = 1 if x2 > x1 else -1
        pts = [x2,y2,x2-sign*7,y2-4,x2-sign*7,y2+4]
    d.add(Polygon(pts, fillColor=colors.HexColor(GREEN), strokeColor=None))


def diagram(name, d):
    renderSVG.drawToFile(d, str(OUT / (name + '.svg')))
    data = renderPDF.drawToString(d)
    pdf = pymupdf.open(stream=data, filetype='pdf')
    pdf[0].get_pixmap(matrix=pymupdf.Matrix(2,2)).save(str(OUT / (name + '.png')))
    pdf.close()


def diagrams():
    d=Drawing(600,350)
    box(d,5,245,180,90,'Sitio público',['HTML / CSS / JS','Catálogo inicial y formulario','Solo validación local'])
    box(d,205,245,180,90,'Secretaría',['MVC / Thymeleaf','HTML del servidor'])
    box(d,405,245,190,90,'Mi Club',['React / Router / Axios','JSON sobre HTTP'])
    box(d,205,125,390,85,'Backend Spring Boot',['Controller MVC / REST → Service compartido','Repository → JPA / Hibernate / JDBC'])
    arrow(d,295,245,295,210);arrow(d,500,245,500,210)
    box(d,260,5,280,80,'MySQL / InnoDB',['Desarrollo: socioclub','Ensayo separado: socioclub_test'])
    arrow(d,400,125,400,85)
    d.add(String(7,130,'El sitio no envía preinscripciones.',fontName='Body',fontSize=10))
    d.add(String(7,112,'CORS no es autenticación.',fontName='Body',fontSize=10))
    diagram('arquitectura',d)
    d=Drawing(600,300)
    box(d,5,105,175,180,'SOCIO',['PK id BIGINT','UNIQUE dni VARCHAR(8)','nombre, apellido, nacimiento','email, teléfono','estado_cuota: AL_DIA/MOROSO','estado: ACTIVO/INACTIVO'])
    box(d,205,105,190,180,'INSCRIPCION',['PK id BIGINT','FK socio_id → socio.id','FK actividad_id → actividad.id','fecha_inscripcion DATE','estado: ACTIVA / BAJA','Sin cascadas de borrado'])
    box(d,420,105,175,180,'ACTIVIDAD',['PK id BIGINT','nombre, disciplina, profesor','dias / horario de inicio','edad_minima / edad_maxima','cupo_maximo > 0','cuota_mensual DECIMAL(10,2)'])
    arrow(d,180,195,205,195);arrow(d,420,195,395,195)
    d.add(String(182,212,'1:N',fontName='Bold',fontSize=9))
    d.add(String(397,212,'N:1',fontName='Bold',fontSize=9))
    d.add(String(300,65,'Una BAJA histórica puede coexistir con una nueva ACTIVA.',fontName='Body',fontSize=11,textAnchor='middle'))
    d.add(String(300,43,'La regla de duplicados vigentes se coordina en servicio bajo lock.',fontName='Body',fontSize=10,textAnchor='middle'))
    diagram('modelo-er',d)
    d=Drawing(600,390)
    xs=[60,220,380,540]
    for x,title in zip(xs,['React / Axios','API / DTO','Servicio / locks','JPA / MySQL']):
        box(d,x-57,330,114,45,title,[])
        d.add(Line(x,30,x,330,strokeColor=colors.HexColor('#9aaca3'),strokeDashArray=[4,3]))
    steps=[(0,1,295,'POST /inscripciones'),(1,2,255,'inscribir(socioId, actividadId)'),(2,3,215,'lock socio → actividad; contar'),(2,3,175,'guardar ACTIVA si cumple'),(3,2,135,'fila / transacción'),(2,1,95,'DTO / error de negocio'),(1,0,55,'201 y recarga / 409 RNx')]
    for a,b,y,t in steps:
        arrow(d,xs[a],y,xs[b],y)
        d.add(String((xs[a]+xs[b])/2,y+7,t,fontName='Body',fontSize=9,textAnchor='middle'))
    diagram('secuencia-inscripcion',d)


def plain(s):
    return re.sub(r'\*\*(.*?)\*\*|`([^`]+)`',lambda m:m.group(1) or m.group(2),s)


def inline(s):
    s=escape(s)
    s=re.sub(r'\*\*(.*?)\*\*',r'<b>\1</b>',s)
    s=re.sub(r'`([^`]+)`',r'<font name="Mono">\1</font>',s)
    s=re.sub(r'https://github\.com/[A-Za-z0-9_./-]+',lambda m:'<link href="'+m.group(0)+'" color="#18533f">'+m.group(0)+'</link>',s)
    return s


def doc_paragraph(doc, value):
    p=doc.add_paragraph()
    parts=re.split(r'(https://github\.com/[A-Za-z0-9_./-]+)',plain(value))
    for part in parts:
        if part.startswith('https://github.com/'):
            rel=p.part.relate_to(part,'http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink',is_external=True)
            link=OxmlElement('w:hyperlink');link.set(qn('r:id'),rel)
            run=OxmlElement('w:r');text=OxmlElement('w:t');text.text=part;run.append(text);link.append(run);p._p.append(link)
        else:p.add_run(part)


def blocks(md):
    lines=md.splitlines();i=0
    while i<len(lines):
        line=lines[i].strip()
        if not line: i+=1;continue
        if line=='<!-- pagebreak -->':yield('break','');i+=1;continue
        if line.startswith('```'):
            code=[];i+=1
            while i<len(lines) and not lines[i].startswith('```'):code.append(lines[i]);i+=1
            yield('code','\n'.join(code));i+=1;continue
        if line.startswith('|'):
            rows=[]
            while i<len(lines) and lines[i].strip().startswith('|'):
                cells=[c.strip() for c in lines[i].strip().strip('|').split('|')]
                if not all(re.fullmatch(r':?-+:?',c) for c in cells):rows.append(cells)
                i+=1
            yield('table',rows);continue
        m=re.fullmatch(r'!\[(.*?)\]\((.*?)\)',line)
        if m:yield('image',(m.group(1),m.group(2)));i+=1;continue
        if line.startswith('#'):
            depth=len(line)-len(line.lstrip('#'));yield('h'+str(depth),line[depth:].strip());i+=1;continue
        para=[line];i+=1
        while i<len(lines) and lines[i].strip() and not re.match(r'^(#|\||```|!\[|<!--)',lines[i].strip()):
            # Keep each list item as its own paragraph.
            if re.match(r'^(?:- |\d+\. )',lines[i].strip()):break
            para.append(lines[i].strip());i+=1
        yield('p',' '.join(para))


def footer(canvas,doc):
    canvas.setStrokeColor(colors.HexColor(GREEN));canvas.line(43,36,A4[0]-43,36)
    canvas.setFont('Body',8);canvas.setFillColor(colors.HexColor(GREEN))
    canvas.drawString(43,23,'SocioClub · versión dev 874c6bb · borrador académico')
    canvas.drawRightString(A4[0]-43,23,str(doc.page))


def export(md_path,pdf_path,docx_path,report=False):
    md=md_path.read_text(encoding='utf-8')
    base=10 if report else 10.5
    styles={
        'p':ParagraphStyle('p',fontName='Body',fontSize=base,leading=base*1.35,spaceAfter=7),
        'h1':ParagraphStyle('h1',fontName='Bold',fontSize=23,leading=29,spaceAfter=18,textColor=colors.HexColor(GREEN)),
        'h2':ParagraphStyle('h2',fontName='Bold',fontSize=16,leading=21,spaceBefore=10,spaceAfter=12,keepWithNext=True,textColor=colors.HexColor(GREEN)),
        'h3':ParagraphStyle('h3',fontName='Bold',fontSize=12,leading=16,spaceBefore=9,spaceAfter=7,keepWithNext=True),
        'cell':ParagraphStyle('cell',fontName='Body',fontSize=8.5 if report else 9,leading=11.5,spaceAfter=0),
        'code':ParagraphStyle('code',fontName='Mono',fontSize=8,leading=10,spaceAfter=10,backColor=colors.HexColor('#f2f4f3'))}
    doc=Document();sec=doc.sections[0]
    sec.top_margin=Cm(1.8);sec.bottom_margin=Cm(1.8);sec.left_margin=Cm(1.8);sec.right_margin=Cm(1.8)
    sec.page_width=Cm(21);sec.page_height=Cm(29.7)
    normal=doc.styles['Normal'];normal.font.name='Arial';normal.font.size=Pt(base)
    normal.paragraph_format.space_after=Pt(7)
    for h in ['Title','Heading 1','Heading 2','Heading 3']:
        doc.styles[h].font.name='Arial';doc.styles[h].font.color.rgb=RGBColor.from_string('18533F')
    f=sec.footer.paragraphs[0];f.text='SocioClub · dev 874c6bb · borrador académico · '
    fld=OxmlElement('w:fldSimple');fld.set(qn('w:instr'),'PAGE');f._p.append(fld)
    story=[];width=A4[0]-86
    for typ,value in blocks(md):
        if typ=='break':story.append(PageBreak());doc.add_page_break()
        elif typ.startswith('h'):
            story.append(Paragraph(inline(value),styles.get(typ,styles['h3'])))
            doc.add_heading(plain(value),level=min(int(typ[1:]),3)-1 if typ=='h1' else min(int(typ[1:])-1,3))
        elif typ=='p':
            story.append(Paragraph(inline(value),styles['p']));doc_paragraph(doc,value)
        elif typ=='code':
            # Wrap visibly without silently dropping any characters.
            wrapped='\n'.join('\n'.join(textwrap.wrap(s,105,replace_whitespace=False,drop_whitespace=False) or ['']) for s in value.splitlines())
            story.append(Preformatted(wrapped,styles['code']))
            p=doc.add_paragraph();r=p.add_run(value);r.font.name='Consolas';r.font.size=Pt(8)
        elif typ=='image':
            alt,relative=value;path=(md_path.parent/relative).resolve()
            image=Image(str(path));scale=min(width/image.imageWidth,240/image.imageHeight)
            image.drawWidth=image.imageWidth*scale;image.drawHeight=image.imageHeight*scale
            story.extend([image,Spacer(1,8)])
            doc.add_picture(str(path),width=Inches(6.6))
            doc.add_paragraph(alt,style='Caption')
        elif typ=='table':
            cols=len(value[0]);ratios=[1]*cols
            if report and cols==4:ratios=[1,1,1.3,2]
            if report and cols==3:ratios=[1.2,2,1.4]
            if cols==2:ratios=[1,2.6]
            widths=[width*r/sum(ratios) for r in ratios]
            rows=[[Paragraph(inline(c),styles['cell']) for c in row] for row in value]
            t=Table(rows,colWidths=widths,repeatRows=1,hAlign='LEFT')
            t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),colors.HexColor('#dcebe2')),('VALIGN',(0,0),(-1,-1),'TOP'),('GRID',(0,0),(-1,-1),.4,colors.HexColor('#baccc0')),('LEFTPADDING',(0,0),(-1,-1),5),('RIGHTPADDING',(0,0),(-1,-1),5),('TOPPADDING',(0,0),(-1,-1),5),('BOTTOMPADDING',(0,0),(-1,-1),5)]))
            story.extend([t,Spacer(1,9)])
            table=doc.add_table(rows=1,cols=cols);table.style='Light Shading Accent 1'
            for j,c in enumerate(value[0]):table.rows[0].cells[j].text=plain(c)
            header=OxmlElement('w:tblHeader');table.rows[0]._tr.get_or_add_trPr().append(header)
            for row in value[1:]:
                cells=table.add_row().cells
                for j,c in enumerate(row):cells[j].text=plain(c)
            for row in table.rows:
                for cell in row.cells:
                    for p in cell.paragraphs:
                        for r in p.runs:r.font.size=Pt(9)
    pdf=SimpleDocTemplate(str(pdf_path),pagesize=A4,rightMargin=43,leftMargin=43,topMargin=43,bottomMargin=49,title=md.splitlines()[0].lstrip('# '),author='SocioClub · documentación asistida por Codex')
    pdf.build(story,onFirstPage=footer,onLaterPages=footer)
    doc.core_properties.author='SocioClub · documentación asistida por Codex'
    doc.core_properties.title=md.splitlines()[0].lstrip('# ')
    doc.save(str(docx_path))
    checked=pymupdf.open(str(pdf_path));print(pdf_path.relative_to(ROOT),len(checked),'páginas')
    if report and not 8<=len(checked)<=12:raise RuntimeError('Informe fuera de 8–12 páginas; revisar distribución')
    checked.close()


if __name__=='__main__':
    diagrams()
    export(ROOT/'Docs/entrega/informe-tecnico-final.md',ROOT/'Docs/entrega/TPI_SocioClub_GrupoN.pdf',ROOT/'Docs/entrega/informe-tecnico-final.docx',True)
    export(ROOT/'Docs/defensa/guia-integral-defensa.md',ROOT/'Docs/defensa/guia-integral-defensa.pdf',ROOT/'Docs/defensa/guia-integral-defensa.docx')
