# Datos iniciales y compatibilidad

Fuente: sección 3.3, página 4 del PDF, contrastada con `app-socios/src/datos/datosEjemplo.js` y `Docs/pruebas-sistema.md` el 2026-11-07. No se modificó React ni el sitio público.

## Actividades

Las ocho actividades de `database/seed.sql` conservan nombre, disciplina, profesor, días, horario, edades, cupo y cuota del PDF, coincidentes con React. Se conserva `9:00` para Yoga. Los IDs se asignan en MySQL y se resuelven por nombre en el seed: la API futura no debe presuponer que coinciden con los IDs locales de React.

## Socios sintéticos

Los seis primeros mantienen los DNI, nombres, apellidos, fechas de nacimiento y estados usados por el grupo. Se emplean correos bajo `example.invalid` y teléfonos marcados PRUEBA para evitar contactos reales. Esos contactos difieren intencionalmente de los datos locales de React; la futura API será la fuente persistente.

| DNI | Nombre | Apellido | Nacimiento | Estado |
| --- | --- | --- | --- | --- |
| 30111222 | Juan | Pérez | 2001-05-15 | AL_DIA |
| 30222333 | Lucas | Gómez | 2016-06-10 | AL_DIA |
| 30333444 | Martín | López | 2015-08-12 | AL_DIA |
| 30444555 | Nicolás | Fernández | 2012-04-10 | AL_DIA |
| 30555666 | Federico | Ramírez | 2006-03-20 | MOROSO |
| 30666777 | Agustín | Torres | 2006-02-15 | AL_DIA |
| 70000001 | SocioPrueba01 | Ensayo | 1963-01-15 | AL_DIA |
| 70000002 | SocioPrueba02 | Ensayo | 1966-01-15 | AL_DIA |
| 70000003 | SocioPrueba03 | Ensayo | 1969-01-15 | AL_DIA |
| 70000004 | SocioPrueba04 | Ensayo | 1972-01-15 | AL_DIA |
| 70000005 | SocioPrueba05 | Ensayo | 1975-01-15 | AL_DIA |
| 70000006 | SocioPrueba06 | Ensayo | 1978-01-15 | AL_DIA |
| 70000007 | SocioPrueba07 | Ensayo | 1981-01-15 | AL_DIA |
| 70000008 | SocioPrueba08 | Ensayo | 1984-01-15 | AL_DIA |
| 70000009 | SocioPrueba09 | Ensayo | 1987-01-15 | AL_DIA |
| 70000010 | SocioPrueba10 | Ensayo | 1990-01-15 | AL_DIA |
| 70000011 | SocioPrueba11 | Ensayo | 1993-01-15 | AL_DIA |
| 70000012 | SocioPrueba12 | Ensayo | 1996-01-15 | AL_DIA |
| 70000013 | SocioPrueba13 | Ensayo | 1999-01-15 | AL_DIA |
| 70000014 | SocioPrueba14 | Ensayo | 2002-01-15 | AL_DIA |
| 70000015 | SocioPrueba15 | Ensayo | 2005-01-15 | MOROSO |

Edades calculadas a la fecha de referencia 2026-11-07: entre 10 y 63 años, con menores y mayores. Son combinaciones ficticias; no verifican ni representan identidades reales. Los DNI cumplen únicamente el formato académico de 7 u 8 dígitos.

## Inscripciones de ejemplo

- Martín (30333444) tiene Fútbol infantil ACTIVA.
- Agustín (30666777) conserva las tres ACTIVAS de React: Vóley mixto, Natación y Funcional adultos.
- Los catorce socios 70000001–70000014 tienen Yoga ACTIVA: 14/15 plazas.
- Federico (30555666) tiene Ajedrez BAJA para mostrar historial; conserva cero ACTIVAS y no está en Yoga. Un moroso puede conservar bajas históricas.
- Juan (30111222), Lucas (30222333) y Nicolás (30444555) comienzan sin ACTIVAS.

Total previsto en base vacía: 21 socios, 2 MOROSOS, 8 actividades, 19 inscripciones (18 ACTIVAS/1 BAJA). Las cuotas son BigDecimal/DECIMAL; no se precalcula cupoDisponible.

## Diferencias detectadas y preparación propuesta

1. React almacena cupos disponibles fijos (por ejemplo Fútbol 0 y Yoga 1), pero sus cuatro inscripciones no respaldan esos conteos. Son escenarios locales de demostración. El seed conserva Yoga casi completa con catorce vínculos reales y Fútbol con un solo vínculo. La API futura calculará cupo desde ACTIVAS.
2. PS02 exige 40 ACTIVAS en Fútbol para rechazar a Lucas, mientras PS03 requiere cupo libre para aislar el duplicado de Martín. Son escenarios independientes: no pueden compartir esa misma preparación. Antes de PS02, preparar cuarenta socios habilitados de 6–12 años distintos de Lucas; no se cargan cuarenta niños extra solo para simular un cupo ficticio. PS03 funciona sobre la preparación inicial de Fútbol. No se marcaron esos casos como ejecutados.
3. El comentario de React para Nicolás menciona Funcional, mientras el caso PS04 usa Fútbol. Se conserva el escenario documental: Nicolás tiene 14 años a la fecha de referencia y queda fuera de Fútbol (6–12); también queda fuera de Funcional (18–65). No requiere cambiar componentes.
4. Los cupos, edades y precondiciones deben verificarse de nuevo antes de la exposición. El paso del tiempo cambia las edades; ejecutar seed de nuevo no modifica fechas ni restaura bajas.
5. El seed omite registros existentes por DNI, nombre o vínculo socio/actividad. No corrige silenciosamente un registro contradictorio del usuario. Sobre una base ya poblada hay que revisar diferencias antes de decidir correcciones.

La validación de formato y coherencia realizada sin servidor no reemplaza ejecutar los scripts dos veces en MySQL. La prueba optativa de reproducibilidad documentada en [MySQL local](mysql-local.md) comprueba conteos, conservación de cambios y ausencia de reactivaciones.
