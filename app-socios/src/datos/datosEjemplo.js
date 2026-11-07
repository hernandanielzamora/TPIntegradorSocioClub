// Datos locales de ejemplo (un arreglo en el código), como pide el hito de la
// semana 2 para la Parte D. Cuando la API esté lista, estos datos van a venir
// del backend con Axios y este archivo deja de usarse.
//
// Las ocho actividades son las de la sección 3.3 del enunciado. Los nombres de
// los campos son los mismos del Anexo B para que el cambio a la API sea directo.
// cupoDisponible = cupoMaximo - inscripciones ACTIVAS (lo calcula el backend).
// Más abajo están los socios y las inscripciones de prueba.
export const actividadesEjemplo = [
  {
    id: 1,
    nombre: 'Fútbol infantil',
    disciplina: 'Fútbol',
    dias: 'Lun y Mié',
    horario: '18:00',
    edadMinima: 6,
    edadMaxima: 12,
    cupoMaximo: 40,
    cupoDisponible: 0, // Cupo completo: sirve para mostrar «Sin cupo» (RN1).
    cuotaMensual: 18000,
    profesor: 'Diego Ferreyra',
  },
  {
    id: 2,
    nombre: 'Vóley mixto',
    disciplina: 'Vóley',
    dias: 'Mar y Jue',
    horario: '19:00',
    edadMinima: 13,
    edadMaxima: 60,
    cupoMaximo: 24,
    cupoDisponible: 9,
    cuotaMensual: 20000,
    profesor: 'Laura Ceballos',
  },
  {
    id: 3,
    nombre: 'Básquet juvenil',
    disciplina: 'Básquet',
    dias: 'Lun y Vie',
    horario: '19:30',
    edadMinima: 12,
    edadMaxima: 17,
    cupoMaximo: 20,
    cupoDisponible: 6,
    cuotaMensual: 19000,
    profesor: 'Martín Oviedo',
  },
  {
    id: 4,
    nombre: 'Natación',
    disciplina: 'Natación',
    dias: 'Mar y Jue',
    horario: '17:00',
    edadMinima: 5,
    edadMaxima: 70,
    cupoMaximo: 30,
    cupoDisponible: 12,
    cuotaMensual: 26000,
    profesor: 'Sofía Bustos',
  },
  {
    id: 5,
    nombre: 'Patín artístico',
    disciplina: 'Patín',
    dias: 'Mié y Vie',
    horario: '17:30',
    edadMinima: 5,
    edadMaxima: 15,
    cupoMaximo: 25,
    cupoDisponible: 8,
    cuotaMensual: 21000,
    profesor: 'Carla Peralta',
  },
  {
    id: 6,
    nombre: 'Ajedrez',
    disciplina: 'Ajedrez',
    dias: 'Sáb',
    horario: '10:00',
    edadMinima: 8,
    edadMaxima: 99,
    cupoMaximo: 16,
    cupoDisponible: 10,
    cuotaMensual: 9000,
    profesor: 'Raúl Giménez',
  },
  {
    id: 7,
    nombre: 'Funcional adultos',
    disciplina: 'Fitness',
    dias: 'Lun, Mié y Vie',
    horario: '20:30',
    edadMinima: 18,
    edadMaxima: 65,
    cupoMaximo: 30,
    cupoDisponible: 14,
    cuotaMensual: 22000,
    profesor: 'Paula Luna',
  },
  {
    id: 8,
    nombre: 'Yoga',
    disciplina: 'Bienestar',
    dias: 'Mar y Jue',
    horario: '9:00',
    edadMinima: 16,
    edadMaxima: 99,
    cupoMaximo: 15,
    cupoDisponible: 1, // Cupo casi completo.
    cuotaMensual: 17000,
    profesor: 'Inés Arguello',
  },
];

// Socios de prueba. Son los mismos socios ficticios de Docs/pruebas-sistema.md,
// así cada uno sirve para mostrar una regla en la exposición:
//   30111222 Juan Pérez         → inscripción exitosa (sin actividades al inicio)
//   30222333 Lucas Gómez        → RN1: Fútbol infantil no tiene cupo
//   30333444 Martín López       → RN2: ya está inscripto en Fútbol infantil
//   30444555 Nicolás Fernández  → RN3: 14 años, fuera de rango en Funcional adultos
//   30555666 Federico Ramírez   → RN4: cuota MOROSO
//   30666777 Agustín Torres     → RN5: ya tiene 3 actividades (y muestra el 15 %)
export const sociosEjemplo = [
  {
    id: 1,
    dni: '30111222',
    nombre: 'Juan',
    apellido: 'Pérez',
    fechaNacimiento: '2001-05-15',
    email: 'juan.perez@ejemplo.com',
    telefono: '351 555-0101',
    estadoCuota: 'AL_DIA',
  },
  {
    id: 2,
    dni: '30222333',
    nombre: 'Lucas',
    apellido: 'Gómez',
    fechaNacimiento: '2016-06-10',
    email: 'lucas.gomez@ejemplo.com',
    telefono: '351 555-0102',
    estadoCuota: 'AL_DIA',
  },
  {
    id: 3,
    dni: '30333444',
    nombre: 'Martín',
    apellido: 'López',
    fechaNacimiento: '2015-08-12',
    email: 'martin.lopez@ejemplo.com',
    telefono: '351 555-0103',
    estadoCuota: 'AL_DIA',
  },
  {
    id: 4,
    dni: '30444555',
    nombre: 'Nicolás',
    apellido: 'Fernández',
    fechaNacimiento: '2012-04-10',
    email: 'nicolas.fernandez@ejemplo.com',
    telefono: '351 555-0104',
    estadoCuota: 'AL_DIA',
  },
  {
    id: 5,
    dni: '30555666',
    nombre: 'Federico',
    apellido: 'Ramírez',
    fechaNacimiento: '2006-03-20',
    email: 'federico.ramirez@ejemplo.com',
    telefono: '351 555-0105',
    estadoCuota: 'MOROSO',
  },
  {
    id: 6,
    dni: '30666777',
    nombre: 'Agustín',
    apellido: 'Torres',
    fechaNacimiento: '2006-02-15',
    email: 'agustin.torres@ejemplo.com',
    telefono: '351 555-0106',
    estadoCuota: 'AL_DIA',
  },
];

// Inscripciones iniciales (estado ACTIVA o BAJA, como en el Anexo B).
export const inscripcionesEjemplo = [
  { id: 1, socioId: 3, actividadId: 1, fechaInscripcion: '2026-03-02', estado: 'ACTIVA' },
  { id: 2, socioId: 6, actividadId: 2, fechaInscripcion: '2026-03-05', estado: 'ACTIVA' },
  { id: 3, socioId: 6, actividadId: 4, fechaInscripcion: '2026-03-05', estado: 'ACTIVA' },
  { id: 4, socioId: 6, actividadId: 7, fechaInscripcion: '2026-03-09', estado: 'ACTIVA' },
];
