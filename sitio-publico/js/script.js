'use strict';

// Catálogo inicial de la sección 3.3 del enunciado. No es disponibilidad en vivo.
class Actividad {
  constructor(id, nombre, disciplina, dias, horario, edadMinima, edadMaxima, cupoMaximo, cuotaMensual, profesor) {
    if (![id, nombre, disciplina, dias, horario, profesor].every((v) => typeof v === 'string' && v.trim()) ||
        !/^([01]?\d|2[0-3]):[0-5]\d$/.test(horario) || !Number.isInteger(edadMinima) || edadMinima < 0 ||
        !Number.isInteger(edadMaxima) || edadMaxima < edadMinima || !Number.isInteger(cupoMaximo) || cupoMaximo <= 0 ||
        !Number.isFinite(cuotaMensual) || cuotaMensual < 0) throw new TypeError('Actividad inicial inválida');
    Object.assign(this, { id, nombre, disciplina, dias, horario, edadMinima, edadMaxima, cupoMaximo, cuotaMensual, profesor });
  }
  esAptaParaEdad(edad) {
    return Number.isInteger(edad) && edad >= this.edadMinima && edad <= this.edadMaxima;
  }
}
const actividades = [
  new Actividad('futbol-infantil', 'Fútbol infantil', 'Fútbol', 'Lun y Mié', '18:00', 6, 12, 40, 18000, 'Diego Ferreyra'),
  new Actividad('voley-mixto', 'Vóley mixto', 'Vóley', 'Mar y Jue', '19:00', 13, 60, 24, 20000, 'Laura Ceballos'),
  new Actividad('basquet-juvenil', 'Básquet juvenil', 'Básquet', 'Lun y Vie', '19:30', 12, 17, 20, 19000, 'Martín Oviedo'),
  new Actividad('natacion', 'Natación', 'Natación', 'Mar y Jue', '17:00', 5, 70, 30, 26000, 'Sofía Bustos'),
  new Actividad('patin-artistico', 'Patín artístico', 'Patín', 'Mié y Vie', '17:30', 5, 15, 25, 21000, 'Carla Peralta'),
  new Actividad('ajedrez', 'Ajedrez', 'Ajedrez', 'Sáb', '10:00', 8, 99, 16, 9000, 'Raúl Giménez'),
  new Actividad('funcional-adultos', 'Funcional adultos', 'Fitness', 'Lun, Mié y Vie', '20:30', 18, 65, 30, 22000, 'Paula Luna'),
  new Actividad('yoga', 'Yoga', 'Bienestar', 'Mar y Jue', '9:00', 16, 99, 15, 17000, 'Inés Arguello'),
];

function filtrarActividades(disciplina, edadTexto) {
  if (disciplina && !actividades.some((a) => a.disciplina === disciplina)) return { datos: [], error: 'Seleccioná una disciplina del catálogo.' };
  const edadIngresada = String(edadTexto ?? '').trim();
  if (edadIngresada && (!/^\d{1,3}$/.test(edadIngresada) || Number(edadIngresada) > 120)) {
    return { datos: [], error: 'Ingresá una edad entera entre 0 y 120, o dejá el campo vacío.' };
  }
  return { datos: actividades.filter((a) => (!disciplina || a.disciplina === disciplina) &&
    (!edadIngresada || a.esAptaParaEdad(Number(edadIngresada)))), error: '' };
}

function validarPreinscripcion(datos, opciones = actividades) {
  const errores = {};
  for (const campo of ['nombre', 'apellido']) {
    if (!String(datos[campo] ?? '').trim() || String(datos[campo]).trim().length > 80) errores[campo] = 'Completá este campo con hasta 80 caracteres.';
  }
  if (!/^\d{7,8}$/.test(String(datos.dni ?? ''))) errores.dni = 'Ingresá 7 u 8 dígitos sin puntos ni espacios.';
  const email = String(datos.email ?? '').trim();
  if (email.length > 150 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errores.email = 'Ingresá un correo electrónico válido.';
  if (!opciones.some((a) => String(a.id) === datos.actividad)) errores.actividad = 'Seleccioná una actividad del catálogo.';
  return errores;
}

function crearElemento(etiqueta, texto, clase) {
  const elemento = document.createElement(etiqueta);
  if (texto !== undefined) elemento.textContent = texto;
  if (clase) elemento.className = clase;
  return elemento;
}
function iniciarCatalogo() {
  const listado = document.getElementById('catalogo-actividades');
  if (!listado) return;
  const disciplina = document.getElementById('filtro-disciplina');
  const edad = document.getElementById('filtro-edad');
  for (const nombre of [...new Set(actividades.map((a) => a.disciplina))]) {
    const opcion = crearElemento('option', nombre); opcion.value = nombre; disciplina.append(opcion);
  }
  const moneda = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', maximumFractionDigits: 2 });
  function actualizar() {
    const { datos, error } = filtrarActividades(disciplina.value, edad.value);
    document.getElementById('error-edad').textContent = error;
    edad.setAttribute('aria-invalid', error ? 'true' : 'false');
    listado.replaceChildren();
    document.getElementById('resultado-catalogo').textContent = error ? 'Revisá los filtros.' : `${datos.length} actividades encontradas.`;
    if (!error && datos.length === 0) listado.append(crearElemento('p', 'No hay actividades para esa combinación de filtros.', 'estado-vacio'));
    for (const a of datos) {
      const tarjeta = crearElemento('article', undefined, 'tarjeta-actividad');
      tarjeta.dataset.disciplina = a.id;
      const visual = crearElemento('div', String(actividades.indexOf(a) + 1).padStart(2, '0'), 'actividad-visual');
      visual.setAttribute('aria-hidden', 'true');
      tarjeta.append(visual, crearElemento('p', a.disciplina, 'etiqueta'), crearElemento('h3', a.nombre));
      const detalles = crearElemento('dl');
      const campos = [['Días y horario', `${a.dias} · ${a.horario} h`], ['Edades', `${a.edadMinima} a ${a.edadMaxima} años`],
        ['Profesor/a', a.profesor], ['Cupo máximo', `${a.cupoMaximo} personas`], ['Cuota mensual', moneda.format(a.cuotaMensual)]];
      for (const [titulo, valor] of campos) detalles.append(crearElemento('dt', titulo), crearElemento('dd', valor));
      const enlace = crearElemento('a', 'Preinscribirme', 'boton boton-secundario');
      // location se usa al llegar al formulario para preseleccionar esta actividad.
      enlace.href = `preinscripcion.html?actividad=${encodeURIComponent(a.id)}`;
      enlace.setAttribute('aria-label', `Preinscribirme a ${a.nombre}`);
      tarjeta.append(detalles, enlace); listado.append(tarjeta);
    }
  }
  disciplina.addEventListener('change', actualizar);
  edad.addEventListener('input', actualizar);
  document.querySelector('.filtros-catalogo').addEventListener('submit', (evento) => { evento.preventDefault(); actualizar(); });
  document.getElementById('limpiar-filtros').addEventListener('click', () => { disciplina.value = ''; edad.value = ''; actualizar(); });
  actualizar();
}
function iniciarFormulario() {
  const formulario = document.querySelector('.formulario-preinscripcion');
  if (!formulario) return;
  const selector = formulario.elements.namedItem('actividad');
  const boton = document.getElementById('validar-preinscripcion');
  const reintentar = document.getElementById('reintentar-catalogo');
  const api = formulario.dataset.apiBase.replace(/\/$/, '');
  let opciones = [], enviando = false, cargado = false;
  // Uso real de location: solo identificador de actividad, nunca datos personales en la URL.
  const elegida = new URLSearchParams(window.location.search).get('actividad');
  const campos = ['nombre', 'apellido', 'dni', 'email', 'actividad'];
  const resultado = document.getElementById('resultado-preinscripcion');
  function limpiarCampo(nombre) {
    document.getElementById(`error-${nombre}`).textContent = '';
    formulario.elements.namedItem(nombre).setAttribute('aria-invalid', 'false');
  }
  async function solicitar(ruta, opcionesPeticion = {}) {
    const cancelacion = new AbortController();
    const limite = setTimeout(() => cancelacion.abort(), 10000);
    try { return await fetch(api + ruta, { ...opcionesPeticion, signal: cancelacion.signal }); }
    finally { clearTimeout(limite); }
  }
  async function cargarActividades() {
    cargado = false; boton.disabled = true; selector.disabled = true; reintentar.hidden = true;
    resultado.textContent = 'Cargando actividades disponibles para solicitar…';
    try {
      const respuesta = await solicitar('/actividades');
      if (!respuesta.ok) throw new Error('catalogo');
      const datos = await respuesta.json();
      if (!Array.isArray(datos) || datos.some(a => !Number.isSafeInteger(a.id) || a.id <= 0 || typeof a.nombre !== 'string')) throw new Error('catalogo');
      opciones = datos;
      selector.replaceChildren(crearElemento('option', 'Seleccioná una actividad'));
      selector.firstElementChild.value = '';
      for (const a of opciones) { const opcion = crearElemento('option', a.nombre); opcion.value = a.id; selector.append(opcion); }
      const inicial = actividades.find(a => a.id === elegida);
      const coincidencias = opciones.filter(a => a.nombre === inicial?.nombre);
      if (elegida && coincidencias.length === 1) selector.value = String(coincidencias[0].id);
      else if (elegida) document.getElementById('error-actividad').textContent = 'La actividad del enlace no pertenece al catálogo actual; elegí una opción.';
      cargado = opciones.length > 0; boton.disabled = !cargado; selector.disabled = !cargado;
      resultado.textContent = cargado ? '' : 'No hay actividades para solicitar en este momento.';
    } catch {
      opciones = []; resultado.textContent = 'No se pudieron cargar las actividades. Comprobá la conexión con el club e intentá nuevamente.';
      reintentar.hidden = false;
    }
  }
  reintentar.addEventListener('click', cargarActividades);
  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    if (enviando) return;
    if (!cargado) { resultado.textContent = 'Esperá a que se carguen las actividades antes de enviar.'; return; }
    const datos = Object.fromEntries(campos.map((campo) => [campo, formulario.elements.namedItem(campo).value]));
    const errores = validarPreinscripcion(datos, opciones);
    campos.forEach((campo) => {
      limpiarCampo(campo);
      if (errores[campo]) {
        document.getElementById(`error-${campo}`).textContent = errores[campo];
        formulario.elements.namedItem(campo).setAttribute('aria-invalid', 'true');
      }
    });
    const primero = campos.find((campo) => errores[campo]);
    if (primero) {
      resultado.textContent = 'Revisá los campos indicados antes de continuar.';
      formulario.elements.namedItem(primero).focus(); return;
    }
    enviando = true; boton.disabled = true; boton.textContent = 'Enviando solicitud…';
    formulario.querySelector('[type="reset"]').disabled = true;
    resultado.textContent = 'Enviando tu preinscripción…';
    try {
      const respuesta = await solicitar('/preinscripciones', { method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify({ nombre: datos.nombre.trim(), apellido: datos.apellido.trim(), dni: datos.dni,
          email: datos.email.trim(), actividadId: Number(datos.actividad) }) });
      const recibido = await respuesta.json();
      if (respuesta.status !== 201 || !Number.isSafeInteger(recibido.id) || recibido.id <= 0) {
        if (!respuesta.ok && typeof recibido.error === 'string') {
          resultado.textContent = recibido.error;
          for (const detalle of recibido.detalles ?? []) {
            const campo = detalle.campo === 'actividadId' ? 'actividad' : detalle.campo;
            if (campos.includes(campo)) {
              document.getElementById(`error-${campo}`).textContent = detalle.mensaje;
              formulario.elements.namedItem(campo).setAttribute('aria-invalid', 'true');
            }
          }
          return;
        }
        throw new Error('recepcion');
      }
      formulario.reset();
      resultado.textContent = `Preinscripción recibida. Tu número de solicitud es #${recibido.id}. Secretaría tiene tus datos para contactarte. No reserva cupo ni confirma una inscripción.`;
    } catch {
      resultado.textContent = 'No pudimos confirmar la recepción. No vuelvas a enviar sin consultar antes en Secretaría; la conexión pudo interrumpirse después del guardado.';
    } finally {
      enviando = false; boton.disabled = !cargado; boton.textContent = 'Enviar preinscripción';
      formulario.querySelector('[type="reset"]').disabled = false; resultado.focus();
    }
  });
  for (const evento of ['input', 'change']) formulario.addEventListener(evento, (e) => {
    if (campos.includes(e.target.name)) { limpiarCampo(e.target.name); resultado.textContent = ''; }
  });
  formulario.addEventListener('reset', () => { campos.forEach(limpiarCampo); resultado.textContent = ''; });
  cargarActividades();
}
let sitioIniciado = false;
function iniciarSitio() {
  if (sitioIniciado) return;
  sitioIniciado = true;
  iniciarCatalogo(); iniciarFormulario();
}
if (typeof document !== 'undefined') {
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', iniciarSitio, { once: true });
  else iniciarSitio();
}
if (typeof module !== 'undefined' && module.exports) module.exports = { Actividad, actividades, filtrarActividades, validarPreinscripcion };
