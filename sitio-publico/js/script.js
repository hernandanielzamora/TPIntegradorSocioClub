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

function validarPreinscripcion(datos) {
  const errores = {};
  for (const campo of ['nombre', 'apellido']) {
    if (!String(datos[campo] ?? '').trim() || String(datos[campo]).trim().length > 80) errores[campo] = 'Completá este campo con hasta 80 caracteres.';
  }
  if (!/^\d{7,8}$/.test(String(datos.dni ?? ''))) errores.dni = 'Ingresá 7 u 8 dígitos sin puntos ni espacios.';
  const email = String(datos.email ?? '').trim();
  if (email.length > 150 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errores.email = 'Ingresá un correo electrónico válido.';
  if (!actividades.some((a) => a.id === datos.actividad)) errores.actividad = 'Seleccioná una actividad del catálogo.';
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
      tarjeta.append(crearElemento('p', a.disciplina, 'etiqueta'), crearElemento('h3', a.nombre));
      const detalles = crearElemento('dl');
      const campos = [['Días y horario', `${a.dias} · ${a.horario} h`], ['Edades', `${a.edadMinima} a ${a.edadMaxima} años`],
        ['Profesor/a', a.profesor], ['Cupo máximo', `${a.cupoMaximo} personas`], ['Cuota mensual', moneda.format(a.cuotaMensual)]];
      for (const [titulo, valor] of campos) detalles.append(crearElemento('dt', titulo), crearElemento('dd', valor));
      const enlace = crearElemento('a', 'Preparar preinscripción', 'boton boton-secundario');
      // location se usa al llegar al formulario para preseleccionar esta actividad.
      enlace.href = `preinscripcion.html?actividad=${encodeURIComponent(a.id)}`;
      enlace.setAttribute('aria-label', `Preparar preinscripción a ${a.nombre}`);
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
  for (const actividad of actividades) {
    const opcion = crearElemento('option', actividad.nombre); opcion.value = actividad.id; selector.append(opcion);
  }
  // Uso real de location: solo identificador de actividad, nunca datos personales en la URL.
  const elegida = new URLSearchParams(window.location.search).get('actividad');
  if (elegida && actividades.some((a) => a.id === elegida)) selector.value = elegida;
  else if (elegida) document.getElementById('error-actividad').textContent = 'La actividad del enlace no pertenece al catálogo; elegí una opción.';
  const campos = ['nombre', 'apellido', 'dni', 'email', 'actividad'];
  const resultado = document.getElementById('resultado-preinscripcion');
  function limpiarCampo(nombre) {
    document.getElementById(`error-${nombre}`).textContent = '';
    formulario.elements.namedItem(nombre).setAttribute('aria-invalid', 'false');
  }
  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault(); // No hay endpoint de recepción: nunca enviar ni afirmar persistencia.
    const datos = Object.fromEntries(campos.map((campo) => [campo, formulario.elements.namedItem(campo).value]));
    const errores = validarPreinscripcion(datos);
    campos.forEach((campo) => {
      limpiarCampo(campo);
      if (errores[campo]) {
        document.getElementById(`error-${campo}`).textContent = errores[campo];
        formulario.elements.namedItem(campo).setAttribute('aria-invalid', 'true');
      }
    });
    const primero = campos.find((campo) => errores[campo]);
    resultado.textContent = primero ? 'Revisá los campos indicados antes de continuar.' :
      'Datos validados localmente. No se enviaron ni guardaron. Para confirmar la preinscripción, presentalos en Secretaría.';
    if (primero) formulario.elements.namedItem(primero).focus();
    else resultado.focus();
  });
  for (const evento of ['input', 'change']) formulario.addEventListener(evento, (e) => {
    if (campos.includes(e.target.name)) { limpiarCampo(e.target.name); resultado.textContent = ''; }
  });
  formulario.addEventListener('reset', () => { campos.forEach(limpiarCampo); resultado.textContent = ''; });
  document.getElementById('validar-preinscripcion').disabled = false;
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
