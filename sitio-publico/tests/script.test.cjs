'use strict';
const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
// Reutiliza jsdom ya fijado por react-scripts; no instala dependencias nuevas.
const { JSDOM, VirtualConsole } = require('../../app-socios/node_modules/jsdom');
const { Actividad, actividades, filtrarActividades, validarPreinscripcion } = require('../js/script');
const raiz = path.resolve(__dirname, '..');
const script = fs.readFileSync(path.join(raiz, 'js/script.js'), 'utf8');
function abrir(t, pagina, query='', peticion) {
  const errores = [];
  const consola = new VirtualConsole();
  consola.on('jsdomError', (error) => errores.push(error));
  const dom = new JSDOM(fs.readFileSync(path.join(raiz,pagina),'utf8'), {
    url:`http://localhost:5500/${pagina}${query}`, runScripts:'outside-only', virtualConsole:consola,
  });
  t.after(() => { dom.window.close(); assert.equal(errores.length,0,errores.map(String).join('\n')); });
  dom.llamadas = [];
  dom.window.fetch = async (url, opciones = {}) => {
    dom.llamadas.push({url, opciones});
    if (peticion) return peticion(url, opciones);
    return opciones.method === 'POST' ? {ok:true,status:201,json:async()=>({id:42})} :
      {ok:true,status:200,json:async()=>actividades.map((a,i)=>({id:i+1,nombre:a.nombre}))};
  };
  dom.window.eval(script);
  dom.window.document.dispatchEvent(new dom.window.Event('DOMContentLoaded'));
  return dom;
}
const valido = {nombre:'Ana',apellido:'Ensayo',dni:'87654321',email:'ana@example.invalid',actividad:'yoga'};
const completar = d => {for(const [k,v] of Object.entries({...valido,actividad:'8'})) d.getElementById(k).value=v;};
const esperar = () => new Promise(resolve=>setImmediate(resolve));

test('catálogo inicial tiene ocho objetos Actividad coherentes con el seed', () => {
  assert.equal(actividades.length,8);
  const seed = fs.readFileSync(path.resolve(raiz,'../database/seed.sql'),'utf8');
  for (const a of actividades) {
    assert.ok(a instanceof Actividad);
    for (const dato of [a.nombre,a.disciplina,a.dias,a.horario,a.profesor]) assert.ok(seed.includes(`'${dato}'`));
  }
  assert.equal(new Set(actividades.map(a=>a.id)).size,8);
});
test('edad inclusiva acepta bordes y rechaza valores inválidos', () => {
  const futbol=actividades[0];
  assert.ok(futbol.esAptaParaEdad(6)); assert.ok(futbol.esAptaParaEdad(12));
  for(const edad of [5,13,NaN,-1,6.5,'6']) assert.equal(futbol.esAptaParaEdad(edad),false);
});
test('constructor rechaza horarios y límites inconsistentes', () => {
  assert.throws(()=>new Actividad('x','x','x','Lun','25:00',6,12,1,10,'x'),TypeError);
  assert.throws(()=>new Actividad('x','x','x','Lun','10:00',12,6,1,10,'x'),TypeError);
});
test('filtros por disciplina y edad se combinan', () => {
  assert.equal(filtrarActividades('', '').datos.length,8);
  assert.equal(filtrarActividades('Fútbol','12').datos.length,1);
  assert.equal(filtrarActividades('Fútbol','13').datos.length,0);
});
test('edad inválida o disciplina ajena producen error sin resultados inventados', () => {
  for(const edad of ['-1','4.5','abc','121','1234']) assert.ok(filtrarActividades('',edad).error);
  assert.ok(filtrarActividades('No existe','').error);
});
for (const pagina of ['index.html','actividades.html','preinscripcion.html']) {
  test(`${pagina}: estructura semántica, navegación, recursos y viewport`, (t) => {
    const {window:{document:d}}=abrir(t,pagina);
    for(const selector of ['html[lang="es"]','meta[name="viewport"]','header','nav','main','section','footer','h1','link[rel="stylesheet"]','script[src="js/script.js"]']) assert.ok(d.querySelector(selector),selector);
    assert.equal(d.querySelectorAll('h1').length,1);
    const ids=[...d.querySelectorAll('[id]')].map(n=>n.id); assert.equal(ids.length,new Set(ids).size);
    for(const enlace of d.querySelectorAll('nav a')) assert.ok(fs.existsSync(path.join(raiz,enlace.getAttribute('href'))));
    assert.equal(d.querySelectorAll('nav [aria-current="page"]').length,1);
    for(const imagen of d.querySelectorAll('img')) {assert.ok(imagen.alt.trim());assert.ok(fs.existsSync(path.join(raiz,imagen.getAttribute('src'))));}
  });
}
test('DOM genera tarjetas y actualiza filtros sin duplicarlas', (t) => {
  const dom=abrir(t,'actividades.html'); const d=dom.window.document;
  assert.equal(d.querySelectorAll('.tarjeta-actividad').length,8);
  d.querySelector('#filtro-disciplina').value='Fútbol';
  d.querySelector('#filtro-disciplina').dispatchEvent(new dom.window.Event('change'));
  assert.equal(d.querySelectorAll('.tarjeta-actividad').length,1);
  d.querySelector('#filtro-edad').value='13';
  d.querySelector('#filtro-edad').dispatchEvent(new dom.window.Event('input'));
  assert.equal(d.querySelectorAll('.tarjeta-actividad').length,0);
  assert.match(d.querySelector('.estado-vacio').textContent,/No hay actividades/);
  d.querySelector('#limpiar-filtros').click(); assert.equal(d.querySelectorAll('.tarjeta-actividad').length,8);
});
test('filtro inválido muestra error accesible y Enter no navega', (t) => {
  const dom=abrir(t,'actividades.html');const d=dom.window.document;
  d.querySelector('#filtro-edad').value='-1';
  d.querySelector('#filtro-edad').dispatchEvent(new dom.window.Event('input'));
  assert.equal(d.querySelector('#filtro-edad').getAttribute('aria-invalid'),'true');
  const e=new dom.window.Event('submit',{cancelable:true}); d.querySelector('form').dispatchEvent(e);
  assert.ok(e.defaultPrevented);
});
for(const dni of ['1234567','87654321']) test(`RN7 acepta DNI ${dni.length} dígitos`,()=>assert.equal(validarPreinscripcion({...valido,dni}).dni,undefined));
for(const dni of ['','123456','123456789','87.654.321','87654A21',' 87654321','8765 4321']) test(`RN7 rechaza ${JSON.stringify(dni)}`,()=>assert.ok(validarPreinscripcion({...valido,dni}).dni));
test('email inválido, campos vacíos y actividad ajena generan errores propios',()=>{
  assert.equal(Object.keys(validarPreinscripcion({})).length,5);
  for(const email of ['','ana','ana@','ana@example','ana @example.com']) assert.ok(validarPreinscripcion({...valido,email}).email);
  assert.ok(validarPreinscripcion({...valido,actividad:'ajena'}).actividad);
});
test('preselección resuelve slug público a ID real y rechaza identificadores ajenos', async (t)=>{
  const dom=abrir(t,'preinscripcion.html','?actividad=yoga'); await esperar(); assert.equal(dom.window.document.querySelector('#actividad').value,'8');
  const otro=abrir(t,'preinscripcion.html','?actividad=ajena'); await esperar(); assert.equal(otro.window.document.querySelector('#actividad').value,'');
  assert.match(otro.window.document.querySelector('#error-actividad').textContent,/no pertenece/);
});
test('formulario inválido no envía y lleva el foco al primer error', async (t)=>{
  const dom=abrir(t,'preinscripcion.html');await esperar();const d=dom.window.document;const e=new dom.window.Event('submit',{cancelable:true});
  d.querySelector('form').dispatchEvent(e); assert.ok(e.defaultPrevented);
  assert.equal(d.activeElement.id,'nombre'); assert.match(d.querySelector('#error-dni').textContent,/7 u 8/);
});
test('formulario válido envía JSON con ID real y confirma solo después de HTTP 201', async (t)=>{
  const dom=abrir(t,'preinscripcion.html');await esperar();const d=dom.window.document;
  completar(d);
  const e=new dom.window.Event('submit',{cancelable:true});d.querySelector('form').dispatchEvent(e);
  assert.ok(e.defaultPrevented);await esperar(); assert.match(d.querySelector('#resultado-preinscripcion').textContent,/Preinscripción recibida.*#42/);
  const post=dom.llamadas.find(p=>p.opciones.method==='POST');assert.equal(post.url,'http://localhost:8080/api/preinscripciones');
  assert.deepEqual(JSON.parse(post.opciones.body),{nombre:'Ana',apellido:'Ensayo',dni:'87654321',email:'ana@example.invalid',actividadId:8});
  assert.equal(dom.window.localStorage.length,0);assert.equal(dom.window.sessionStorage.length,0);
  assert.equal(d.activeElement.id,'resultado-preinscripcion');
});
test('errores se limpian al corregir o reiniciar sin insertar HTML del usuario', async (t)=>{
  const dom=abrir(t,'preinscripcion.html');await esperar();const d=dom.window.document;
  d.querySelector('form').dispatchEvent(new dom.window.Event('submit',{cancelable:true}));
  d.querySelector('#nombre').value='<img src=x onerror=alert(1)>';
  d.querySelector('#nombre').dispatchEvent(new dom.window.Event('input',{bubbles:true}));
  assert.equal(d.querySelector('#nombre').getAttribute('aria-invalid'),'false');
  assert.equal(d.querySelectorAll('img').length,0);
  d.querySelector('form').reset();assert.equal(d.querySelector('#error-dni').textContent,'');
});

test('fallo de catálogo impide enviar y permite reintentar',async t=>{
  let intento=0;
  const dom=abrir(t,'preinscripcion.html','',async()=>{if(!intento++)throw Error('red');return {ok:true,json:async()=>[{id:800,nombre:'Yoga'}]};});
  await esperar();const d=dom.window.document;assert.ok(d.querySelector('[type="submit"]').disabled);assert.equal(d.querySelector('#reintentar-catalogo').hidden,false);
  d.querySelector('#reintentar-catalogo').click();await esperar();assert.equal(d.querySelector('[type="submit"]').disabled,false);
  assert.equal(d.querySelector('#actividad option:last-child').value,'800');
});
test('error de red no afirma recepción y conserva datos',async t=>{
  const dom=abrir(t,'preinscripcion.html','',async(url,opt)=>{if(opt.method==='POST')throw Error('red');return {ok:true,json:async()=>[{id:8,nombre:'Yoga'}]};});
  await esperar();const d=dom.window.document;completar(d);d.querySelector('form').dispatchEvent(new dom.window.Event('submit',{cancelable:true}));await esperar();
  assert.match(d.querySelector('#resultado-preinscripcion').textContent,/No pudimos confirmar/);assert.equal(d.querySelector('#dni').value,'87654321');
});
test('rechazo del servidor muestra error junto al campo sin éxito ficticio',async t=>{
  const dom=abrir(t,'preinscripcion.html','',async(url,opt)=>opt.method==='POST'?{ok:false,status:400,json:async()=>({error:'Revisá los datos enviados',detalles:[{campo:'dni',mensaje:'DNI rechazado'}]})}:{ok:true,json:async()=>[{id:8,nombre:'Yoga'}]});
  await esperar();const d=dom.window.document;completar(d);d.querySelector('form').dispatchEvent(new dom.window.Event('submit',{cancelable:true}));await esperar();
  assert.equal(d.querySelector('#error-dni').textContent,'DNI rechazado');assert.equal(d.querySelector('#dni').getAttribute('aria-invalid'),'true');
});
test('doble envío mientras está pendiente solo realiza un POST',async t=>{
  let resolver;
  const dom=abrir(t,'preinscripcion.html','',async(url,opt)=>opt.method==='POST'?new Promise(r=>{resolver=r;}):{ok:true,json:async()=>[{id:8,nombre:'Yoga'}]});
  await esperar();const d=dom.window.document;completar(d);
  for(let i=0;i<2;i++)d.querySelector('form').dispatchEvent(new dom.window.Event('submit',{cancelable:true}));
  assert.equal(dom.llamadas.filter(p=>p.opciones.method==='POST').length,1);
  resolver({ok:true,status:201,json:async()=>({id:99})});await esperar();assert.match(d.querySelector('#resultado-preinscripcion').textContent,/#99/);
});
test('CSS tiene cobertura responsiva y selectores exigidos; revisión visual sigue pendiente',()=>{
  const css=fs.readFileSync(path.join(raiz,'css/estilos.css'),'utf8');
  for(const fragmento of ['@media','#resultado-catalogo','[aria-invalid="true"]','focus-visible']) assert.ok(css.includes(fragmento));
  assert.match(css, /@media\s*\(max-width:\s*(?:700px|40rem)\)/);
  assert.match(css, /\.catalogo-grid\s*\{\s*grid-template-columns:\s*1fr\s*;?\s*\}/);
});
