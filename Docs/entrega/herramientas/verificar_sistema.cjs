/* Playwright real contra los tres servidores locales. Sin rutas ni API simuladas.
 * Instalar Playwright aparte o definir SOCIOCLUB_PLAYWRIGHT_MODULE con el módulo instalado.
 */
const {chromium,expect}=(()=>{const p=require(process.env.SOCIOCLUB_PLAYWRIGHT_MODULE||'playwright');return {...p,expect:require('node:assert/strict')};})();
const fs=require('node:fs');const path=require('node:path');const out=path.resolve(__dirname,'../evidencias');
const assert=require('node:assert/strict');
const mode=process.argv[2]||'capturas';
(async()=>{
 const browser=await chromium.launch({channel:'chrome',headless:true});
 const context=await browser.newContext({viewport:{width:1440,height:1000},locale:'es-AR'});
 const page=await context.newPage();const errors=[];page.on('pageerror',e=>errors.push(e.message));const checks=[];
 async function screenshot(name,url,action,width=1440,height=1000){
  await page.setViewportSize({width,height});if(url)await page.goto(url);await page.waitForLoadState('networkidle');
  if(action)await action();
  const overflow=await page.evaluate(()=>document.documentElement.scrollWidth>window.innerWidth);
  checks.push({name,url:page.url(),width,height,overflow});
  if(overflow)console.log(await page.evaluate(()=>[...document.querySelectorAll('body *')].filter(e=>e.getBoundingClientRect().right>innerWidth).map(e=>({tag:e.tagName,clase:e.className,texto:e.textContent.slice(0,60),ancho:e.getBoundingClientRect().width})).slice(0,12)));
  assert.equal(overflow,false,`Desborde en ${name}`);
  await page.screenshot({path:path.join(out,name+'.png')});
 }
 async function ingresar(dni){
  await page.goto('http://localhost:3000/');
  await page.waitForFunction(()=>document.querySelector('#dni') || [...document.querySelectorAll('button')].some(b=>b.textContent==='Salir'));
  if(await page.getByRole('button',{name:'Salir',exact:true}).count())await page.getByRole('button',{name:'Salir',exact:true}).click();
  await page.getByLabel('DNI',{exact:true}).fill(dni);
  await page.getByRole('button',{name:'Ingresar',exact:true}).click();await page.waitForURL('**/actividades');
  await page.locator('.tarjeta-actividad').first().waitFor();
 }
 try {
 if(mode==='capturas'){
  await screenshot('sitio-inicio','http://localhost:5500/index.html');
  await screenshot('sitio-catalogo','http://localhost:5500/actividades.html');
  await screenshot('sitio-filtros','http://localhost:5500/actividades.html',async()=>{await page.getByLabel('Disciplina',{exact:true}).selectOption('Fútbol');assert.equal(await page.locator('.tarjeta-actividad').count(),1);});
  await page.locator('.tarjeta-actividad').screenshot({path:path.join(out,'sitio-tarjeta.png')});
  await screenshot('sitio-formulario','http://localhost:5500/preinscripcion.html?actividad=yoga',async()=>{await page.getByRole('button',{name:'Enviar preinscripción',exact:true}).waitFor();assert.equal(await page.locator('#actividad').inputValue(),'8');});
  await screenshot('sitio-error',null,async()=>{await page.getByRole('button',{name:'Enviar preinscripción',exact:true}).click();await page.locator('#error-dni').waitFor({state:'visible'});});
  const before=await (await page.request.get('http://localhost:8080/api/actividades/8')).json();
  await page.getByLabel('Nombre',{exact:true}).fill('Ana');await page.getByLabel('Apellido',{exact:true}).fill('Entrega');
  await page.getByLabel('DNI',{exact:true}).fill('87654321');await page.getByLabel('Correo electrónico',{exact:true}).fill('ana@example.invalid');
  const responsePromise=page.waitForResponse(r=>r.url().endsWith('/api/preinscripciones')&&r.request().method()==='POST');
  await page.getByRole('button',{name:'Enviar preinscripción',exact:true}).click();const response=await responsePromise;assert.equal(response.status(),201);const receipt=await response.json();
  await page.getByText(/Preinscripción recibida/).waitFor();
  await screenshot('sitio-preinscripcion-recibida',null);
  const after=await (await page.request.get('http://localhost:8080/api/actividades/8')).json();assert.equal(after.cupoDisponible,before.cupoDisponible);
  await screenshot('secretaria-preinscripciones','http://localhost:8080/secretaria/preinscripciones',async()=>{await page.getByRole('cell',{name:'ana@example.invalid',exact:true}).first().waitFor();});
  fs.writeFileSync(path.join(out,'rf3-recepcion.json'),JSON.stringify({http:201,id:receipt.id,cupoAntes:before.cupoDisponible,cupoDespues:after.cupoDisponible,visibleSecretaria:true,datos:'Persona ficticia de ensayo, correo example.invalid'},null,2));
  for(const [n,u] of [['secretaria-panel',''],['secretaria-socios','/socios'],['secretaria-actividades','/actividades'],['secretaria-inscripciones','/inscripciones']])await screenshot(n,'http://localhost:8080/secretaria'+u);
  await screenshot('react-ingreso','http://localhost:3000/');
  await screenshot('react-error-dni',null,async()=>{await page.getByRole('button',{name:'Ingresar',exact:true}).click();await page.locator('#dni-error').waitFor();});
  await ingresar('30666777');await screenshot('react-catalogo',null);
  await screenshot('react-detalle','http://localhost:3000/actividades/2');
  await screenshot('react-mis-actividades','http://localhost:3000/mis-actividades',async()=>{await page.locator('.resumen-cuota').waitFor();});
  assert.match(await page.locator('.resumen-cuota').innerText(),/57.800/);
  await page.locator('.resumen-cuota').screenshot({path:path.join(out,'react-cuota.png')});
  for(const [n,u] of [['sitio-movil','http://localhost:5500/index.html'],['catalogo-movil','http://localhost:5500/actividades.html'],['formulario-movil','http://localhost:5500/preinscripcion.html'],['secretaria-movil','http://localhost:8080/secretaria/preinscripciones'],['react-movil','http://localhost:3000/mis-actividades']])await screenshot(n,u,null,390,844);
  fs.writeFileSync(path.join(out,'verificacion-navegador.json'),JSON.stringify({browser:'Playwright / Chrome real '+browser.version(),checks,errors},null,2));
  console.log(JSON.stringify({mode,capturas:checks.length+2,errors}));
 }else if(mode==='e2e'){
  const fixtures=JSON.parse(fs.readFileSync(path.join(out,'escenarios.json'),'utf8'));const results=[];
  for(const caso of fixtures.sistema){
   await ingresar(caso.dni);await page.goto('http://localhost:3000/actividades/'+caso.actividadId);await page.getByRole('button',{name:'Inscribirme',exact:true}).waitFor();
   const prev=await (await page.request.get(`http://localhost:8080/api/socios/${caso.socioId}/inscripciones`)).json();
   const cupo=await (await page.request.get(`http://localhost:8080/api/actividades/${caso.actividadId}`)).json();let response;
   if(caso.regla==='RN1'){
    assert.equal(await page.getByRole('button',{name:'Inscribirme',exact:true}).isDisabled(),true);
    response=await page.request.post('http://localhost:8080/api/inscripciones',{data:{socioId:caso.socioId,actividadId:caso.actividadId}});
   }else{
    const promise=page.waitForResponse(r=>r.url().endsWith('/api/inscripciones')&&r.request().method()==='POST');
    await page.getByRole('button',{name:'Inscribirme',exact:true}).click();response=await promise;
    if(caso.regla)await page.getByRole('alert').waitFor();else await page.getByRole('status').waitFor();
   }
   const body=await response.json();assert.equal(response.status(),caso.regla?409:201);
   if(caso.regla)assert.equal(body.regla,caso.regla);
   const post=await (await page.request.get(`http://localhost:8080/api/socios/${caso.socioId}/inscripciones`)).json();
   const nuevoCupo=await (await page.request.get(`http://localhost:8080/api/actividades/${caso.actividadId}`)).json();
   assert.equal(post.length,prev.length+(caso.regla?0:1));assert.equal(nuevoCupo.cupoDisponible,cupo.cupoDisponible-(caso.regla?0:1));
   await screenshot(caso.id.toLowerCase()+'-sistema',null);
   if(!caso.regla){
    await page.goto('http://localhost:3000/mis-actividades');await page.getByRole('button',{name:'Darme de baja',exact:true}).waitFor();
    assert.match(await page.locator('.resumen-cuota').innerText(),/18.000/);
   }
   results.push({...caso,http:response.status(),respuesta:body,activasAntes:prev.length,activasDespues:post.length,cupoAntes:cupo.cupoDisponible,cupoDespues:nuevoCupo.cupoDisponible,resultado:'APROBADO',canal:caso.regla==='RN1'?'Botón deshabilitado + petición HTTP real Playwright':'Navegador React → Axios → REST → servicios → MySQL'});
  }
  // RF9: baja real de la inscripción exitosa, confirmación visible y cuota actualizada.
  await ingresar(fixtures.sistema[0].dni);await page.goto('http://localhost:3000/mis-actividades');
  page.once('dialog',d=>d.accept());const bajando=page.waitForResponse(r=>r.url().endsWith('/baja')&&r.request().method()==='PUT');
  await page.getByRole('button',{name:'Darme de baja',exact:true}).click();assert.equal((await bajando).status(),200);
  await page.getByText('Todavía no estás inscripto en ninguna actividad.').waitFor();await screenshot('react-baja-confirmada',null);
  fs.writeFileSync(path.join(out,'casos-sistema-ejecutados.json'),JSON.stringify({browser:browser.version(),instancia:'MySQL aislado 3308, API real 8080',casos:results,bajaPosterior:'HTTP 200 y ACTIVAS vacías; conserva historial BAJA',errors},null,2));
  console.log(JSON.stringify({mode,casos:results.length,aprobados:results.length,errors}));
 }
 assert.deepEqual(errors,[]);
 }finally{await browser.close();}
})().catch(e=>{console.error(e);process.exitCode=1});
