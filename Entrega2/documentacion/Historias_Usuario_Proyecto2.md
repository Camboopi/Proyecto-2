# Historias de Usuario Actualizadas - Proyecto 2
## Club Deportivo Séneca (CDS)

Andrew Steven Avilán Rodríguez  
Laura Camila Ballén Álzate  
Universidad de los Andes  
2026

## Cambios y Ajustes Realizados sobre las Historias del Proyecto 1

Con base en la revisión de la entrega anterior, la retroalimentación recibida durante el seguimiento del proyecto y los nuevos requerimientos del Club Deportivo Séneca para la segunda etapa, se depuraron las historias de usuario del Proyecto 1 antes de incorporar el módulo de torneos internos. Todas las historias del catálogo especifican ahora de forma explícita sus datos de entrada y sus salidas o resultados verificables.

- **Inscripción de un único equipo representante en competencias externas:** En la historia de participación en competencias universitarias externas (Torneo de los Cerros y ASCUN), se ajustó el alcance para reflejar que en cada competencia externa se inscribe un único equipo representante del club en categoría Mayores, en lugar de una lista de múltiples equipos del mismo club.
- **Acumulación de tarjetas amarillas entre partidos y cancelación de encuentros:** En fútbol, las tarjetas amarillas se acumulan a lo largo de los distintos partidos disputados por un jugador. Cuando un socio completa dos tarjetas amarillas (en el mismo encuentro o en partidos distintos) o recibe una tarjeta roja directa, queda suspendido automáticamente para el siguiente partido de su equipo. Si a causa de esa suspensión el equipo queda con menos jugadores habilitados del mínimo reglamentario (7 en fútbol) o intenta alinear al jugador sancionado, el siguiente partido no puede disputarse y queda cancelado automáticamente.
- **Uso simultáneo de descuento de empleado y puntos de fidelidad:** Los empleados (entrenadores y fisioterapeutas) acumulan también el 2% del valor de sus compras en puntos de fidelidad y pueden aplicar simultáneamente su descuento laboral propio del 15% junto con la redención de sus puntos en una misma compra.
- **Sincronización entre stock general y ubicaciones físicas de inventario:** Al registrar una venta en la tienda deportiva, las unidades vendidas se descuentan automáticamente tanto del stock general del artículo como de las ubicaciones físicas (Mostrador Tienda y Bodega Principal).

## Historias de Usuario Curadas del Proyecto 1

### Inscripción de un jugador en un equipo de conjunto (Socio)
**Historia:** Como socio deportista, quiero inscribirme en un equipo de conjunto correspondiente a mi categoría de edad con un número de camiseta disponible, para entrenar y competir representando a mi categoría dentro del club.
**Entradas:** Credenciales o login del socio, edad registrada, nombre del equipo de conjunto de destino, posición de juego en el campo y número único de camiseta (dorsal) solicitado.
**Salidas y resultados:** Confirmación de inscripción exitosa creando el vínculo del socio en la nómina del equipo con su dorsal asignado, o mensaje de rechazo cuando se incumple la categoría de edad, el cupo máximo, la disponibilidad del dorsal o la unicidad de equipo por deporte.
**Criterios de Aceptación:**
- Dado que el equipo tiene cupos libres, la edad del socio corresponde a la categoría del equipo (Sub-13, Sub-15, Sub-17 o Mayores), el número de camiseta no está ocupado y el socio no pertenece a otro equipo del mismo deporte, cuando solicita su inscripción, entonces el sistema registra al jugador en la nómina oficial del equipo.
- Dado que la edad del socio no coincide con la categoría del equipo o el dorsal ya fue asignado a otro integrante, cuando intenta inscribirse, entonces el sistema rechaza el registro.
- Dado que el socio ya pertenece a un equipo de fútbol del club, cuando intenta inscribirse en un segundo equipo de fútbol, entonces el sistema impide la inscripción, pero sí le permite inscribirse en un equipo de baloncesto o voleibol.
**Reglas del caso:** Categorías de edad (Sub-13 para menores de 13 años, Sub-15 hasta 15 años, Sub-17 hasta 17 años y Mayores desde 18 años), cupo máximo por equipo, dorsal único dentro de cada equipo y máximo un equipo por cada deporte de conjunto.

### Reserva de instalación para práctica de disciplina individual (Socio)
**Historia:** Como socio practicante de tenis, pádel o tenis de mesa, quiero reservar una cancha o mesa en un bloque horario indicando la modalidad y cantidad de jugadores, para asegurar un espacio reglamentario de juego.
**Entradas:** Usuario solicitante, instalación deportiva seleccionada (cancha de tenis, cancha de pádel o mesa de tenis de mesa), bloque horario, modalidad (Sencillos o Dobles) y número de jugadores.
**Salidas y resultados:** Reserva registrada en el sistema para el bloque horario indicado, o rechazo cuando la modalidad no es admitida por la instalación, se supera el límite de reservas simultáneas de la disciplina, el socio porta una bebida caliente en zona restringida o quien intenta reservar es el Administrador.
**Criterios de Aceptación:**
- Dado que la instalación admite la modalidad e integrantes indicados (2 para sencillos o 4 para dobles en tenis y tenis de mesa; obligatoriamente 4 en dobles para pádel) y no se supera el tope de instalaciones simultáneas de la disciplina, cuando el socio confirma la reserva, entonces el sistema asigna el espacio.
- Dado que el socio adquirió previamente una bebida caliente y solicita ingresar a una cancha de pádel o a una mesa de tenis de mesa techada, cuando intenta registrar la reserva, entonces el sistema la bloquea por seguridad física.
- Dado un usuario con rol de Administrador, cuando intenta reservar una instalación para uso personal, entonces el sistema rechaza la operación.
**Reglas del caso:** Capacidad por instalación según modalidad, pádel exclusivamente en modalidad de dobles (4 jugadores), límite de reservas simultáneas por disciplina, prohibición de bebidas calientes en pádel y mesas techadas, y restricción de uso personal para el Administrador.

### Partido de práctica individual y actualización del ranking ELO (Socio)
**Historia:** Como socio inscrito en una academia individual de raqueta, quiero disputar un partido de práctica frente a otro miembro y registrar al ganador, para actualizar automáticamente nuestro puntaje en el ranking ELO de la disciplina.
**Entradas:** Disciplina individual, reserva de instalación asociada, socio jugador A, socio jugador B, modalidad del encuentro, indicador de participación de un entrenador y resultado indicando quién obtuvo la victoria.
**Salidas y resultados:** Validación de compatibilidad de niveles entre los rivales y actualización inmediata del puntaje ELO de ambos jugadores (incremento para el ganador y disminución proporcional para el perdedor según la expectativa de victoria).
**Criterios de Aceptación:**
- Dado que ambos socios tienen el mismo nivel o niveles adyacentes (Principiante con Intermedio, o Intermedio con Avanzado), cuando registran el resultado del partido de práctica, entonces el sistema permite el encuentro y recalcula el puntaje ELO de ambos perfiles.
- Dado que un socio de nivel Principiante desea jugar contra uno de nivel Avanzado sin la presencia de un entrenador, cuando se valida la viabilidad del partido, entonces el sistema impide su realización por incompatibilidad de niveles.
- Dado que en el encuentro participa un entrenador del club, cuando se enfrenta un jugador Principiante con uno Avanzado, entonces el sistema habilita el partido sin restricción de nivel.
**Reglas del caso:** Emparejamiento permitido solo entre niveles iguales o adyacentes salvo que participe un entrenador, y cálculo del ranking ELO con factor K = 32 sobre los perfiles de la academia.

### Compra de artículos en la tienda deportiva y sincronización de inventario (Socio / Empleado)
**Historia:** Como comprador en la tienda deportiva, quiero adquirir implementos aplicando códigos de descuento o redimiendo puntos de fidelidad, para obtener mis artículos con la liquidación exacta de IVA y mantener actualizado el inventario físico del club.
**Entradas:** Fecha de la compra, usuario comprador, artículos seleccionados con sus cantidades, código de descuento de empleado opcional y cantidad de puntos de fidelidad a redimir.
**Salidas y resultados:** Factura de venta de tienda con subtotal, descuentos aplicados, IVA del 19%, valor total a pagar, acreditación del 2% en nuevos puntos de fidelidad, disminución del stock general del artículo y descuento sincronizado en las ubicaciones físicas (Mostrador Tienda y Bodega Principal).
**Criterios de Aceptación:**
- Dado que hay unidades disponibles del artículo solicitado, cuando el comprador confirma la transacción, entonces el sistema liquida el subtotal, aplica los descuentos procedentes, suma el 19% de IVA, abona el 2% en puntos de fidelidad y descuenta las unidades tanto del stock general como del mostrador y la bodega.
- Dado que un socio presenta el código de descuento de un empleado del club, cuando procesa la compra, entonces recibe un 8% de descuento sobre el subtotal antes de IVA y puede redimir adicionalmente sus puntos de fidelidad acumulados (1 punto = $1 peso).
**Reglas del caso:** IVA del 19% en tienda deportiva, abono del 2% del valor de la compra en puntos de fidelidad redimibles a razón de $1 peso por punto, descuento del 8% para socios referidos por un empleado y sincronización obligatoria con las ubicaciones de inventario.

### Compra en cafetería con información de alérgenos y restricción de bebidas calientes (Socio / Empleado)
**Historia:** Como usuario de la cafetería del club, quiero adquirir bebidas y snacks conociendo previamente sus alérgenos y ajustando la propina sugerida, para realizar un consumo seguro dentro de las instalaciones permitidas.
**Entradas:** Fecha de la venta, comprador, productos de cafetería seleccionados (bebidas frías/calientes y snacks), instalación donde se encuentra el comprador y valor de propina deseado.
**Salidas y resultados:** Factura de cafetería con subtotal, impuesto al consumo del 8%, propina (10% sugerido o valor modificado por el comprador), alérgenos informados, acreditación del 2% en puntos de fidelidad, o bloqueo del despacho si incluye una bebida caliente hacia una instalación restringida.
**Criterios de Aceptación:**
- Dado que el pedido incluye un snack con alérgenos declarados (maní, gluten o lácteos) y una bebida fría, cuando se registra la venta, entonces el sistema informa los alérgenos al comprador, aplica el 8% de impoconsumo y suma la propina indicada.
- Dado que el comprador se encuentra en una cancha de pádel o en una mesa de tenis de mesa techada y solicita una bebida caliente, cuando se valida el despacho, entonces el sistema rechaza la entrega por seguridad física.
**Reglas del caso:** Impuesto al consumo del 8%, propina sugerida del 10% modificable libremente, declaración obligatoria de alérgenos en snacks y prohibición estricta de bebidas calientes en canchas de pádel y mesas de tenis de mesa techadas.

### Aplicación simultánea de descuento laboral del 15% y puntos de fidelidad (Empleado)
**Historia:** Como empleado del club (entrenador o fisioterapeuta), quiero realizar compras en la tienda o en la cafetería aplicando mi descuento propio del 15% y redimiendo al mismo tiempo mis puntos de fidelidad acumulados, para aprovechar conjuntamente ambos beneficios institucionales.
**Entradas:** Empleado comprador autenticado, artículos o productos seleccionados, aplicación de su código de empleado personal, cantidad de puntos de fidelidad que desea redimir y fecha de la compra.
**Salidas y resultados:** Venta registrada con el descuento laboral del 15% sobre el subtotal y la resta simultánea de los puntos redimidos, además de la acreditación del 2% de puntos nuevos sobre el valor pagado.
**Criterios de Aceptación:**
- Dado que un empleado cuenta con puntos de fidelidad acumulados y realiza una compra usando su propio código de descuento, cuando solicita redimir también sus puntos en la misma transacción, entonces el sistema aplica el 15% de descuento laboral y descuenta adicionalmente los puntos redimidos sin excluir ninguno de los dos beneficios.
- Dado que el empleado finaliza su compra, cuando el club registra la factura, entonces el saldo de puntos del empleado disminuye en los puntos redimidos y se incrementa con el 2% generado por la nueva compra.
**Reglas del caso:** Descuento del 15% para compras propias del empleado, compatibilidad simultánea entre el descuento de empleado y la redención de puntos de fidelidad, y acumulación del 2% en puntos para todos los compradores del club.

### Planeación de sesiones de entrenamiento y registro de asistencia con evaluación (Empleado - Entrenador)
**Historia:** Como entrenador del club, quiero programar sesiones de entrenamiento convocando a un equipo o grupo de socios y registrar su asistencia junto con una evaluación formativa de 1 a 5, para hacer seguimiento al proceso deportivo de los jugadores.
**Entradas:** Entrenador responsable, fecha, hora, duración en minutos, instalación asignada, equipo o lista de socios convocados, estado de asistencia de cada deportista (Asistió, Llegó tarde o Faltó) y calificación opcional entre 1 y 5 con comentario.
**Salidas y resultados:** Sesión de entrenamiento almacenada con su lista de convocados, registros de asistencia individuales y evaluaciones de desempeño asociadas válidamente dentro de la escala de 1 a 5.
**Criterios de Aceptación:**
- Dado que el entrenador define fecha, hora, duración e instalación y convoca a un equipo del club, cuando guarda la sesión, entonces todos los socios de la nómina quedan registrados como convocados.
- Dado que el entrenador registra la asistencia de un socio y añade una evaluación con nota entre 1 y 5 y una observación formativa, cuando confirma el registro, entonces la evaluación queda vinculada al historial del deportista; si la nota está fuera del rango de 1 a 5, el sistema no la acepta.
**Reglas del caso:** Solo los entrenadores pueden planear sesiones y evaluar jugadores; los estados válidos de asistencia son Asistió, Llegó tarde y Faltó, y la evaluación opcional se califica en escala entera de 1 a 5.

### Solicitud y aprobación de cambios de turno semanal respetando el quórum mínimo (Empleado / Administrador)
**Historia:** Como empleado del club, quiero solicitar el cambio de mi turno semanal asignado, y como administrador quiero aprobarlo únicamente si ambos turnos conservan al menos dos entrenadores y un fisioterapeuta, para no desatender la operación del club.
**Entradas:** Empleado solicitante, turno semanal de origen, turno semanal de destino y decisión de revisión por parte del Administrador.
**Salidas y resultados:** Traslado efectivo del empleado al turno de destino cuando el turno de origen mantiene el quórum mínimo de dos entrenadores y un fisioterapeuta, o rechazo automático de la solicitud cuando el retiro del empleado rompería dicho mínimo.
**Criterios de Aceptación:**
- Dado que el turno de origen cuenta con tres entrenadores y un fisioterapeuta, cuando un entrenador solicita trasladarse a otro turno y el administrador evalúa la solicitud, entonces el sistema aprueba el cambio y actualiza ambos turnos.
- Dado que el turno de origen tiene exactamente dos entrenadores o un único fisioterapeuta, cuando uno de ellos solicita cambiar de turno, entonces el sistema impide la aprobación para preservar la disponibilidad mínima exigida.
**Reglas del caso:** Presencia obligatoria en todo turno de atención de al menos 2 entrenadores y 1 fisioterapeuta.

### Registro de partidos internos, acumulación de tarjetas amarillas y cancelación de partidos (Administrador)
**Historia:** Como administrador del club, quiero registrar los partidos internos entre equipos de conjunto junto con las tarjetas disciplinarias de fútbol, para controlar automáticamente las suspensiones por acumulación de amarillas o tarjeta roja y cancelar cualquier partido posterior que no reúna el mínimo de jugadores habilitados.
**Entradas:** Fecha del encuentro, equipo local, equipo visitante, marcador propuesto y estadísticas individuales del partido (incluyendo goles, asistencias, tarjetas amarillas y tarjetas rojas en fútbol).
**Salidas y resultados:** Si ambos equipos pertenecen al mismo deporte y categoría, reúnen el mínimo de jugadores habilitados (7 en fútbol, 5 en baloncesto, 6 en voleibol), no alinean suspendidos y respetan la regla de empates, el partido queda disputado, se cumplen las sanciones previas y se acumulan las nuevas tarjetas; de lo contrario, el partido no se disputa y queda marcado como cancelado indicando el motivo.
**Criterios de Aceptación:**
- Dado que un jugador de fútbol recibe una tarjeta amarilla en un primer partido y recibe una segunda tarjeta amarilla en un partido posterior (o dos amarillas en el mismo encuentro, o una roja directa), cuando se registra el resultado del partido donde completa las dos amarillas, entonces el sistema reinicia su contador acumulado y le activa automáticamente una suspensión para el siguiente partido.
- Dado que un equipo de fútbol tiene 7 jugadores en su nómina y uno de ellos queda suspendido por acumulación de dos tarjetas amarillas, cuando se intenta disputar el siguiente partido con solo 6 jugadores habilitados, entonces el sistema rechaza el registro del resultado y marca el partido como cancelado por falta del mínimo reglamentario de jugadores habilitados.
- Dado un partido de baloncesto o voleibol donde se intenta registrar un marcador empatado, cuando se procesa el resultado, entonces el sistema lo rechaza porque esas disciplinas exigen un ganador.
**Reglas del caso:** Mínimo de jugadores habilitados por deporte (7 en fútbol, 5 en baloncesto, 6 en voleibol), prohibición de empates en baloncesto y voleibol, acumulación continua de tarjetas amarillas entre partidos de fútbol (2 amarillas acumuladas o 1 roja suspenden por un partido) y cancelación automática del encuentro cuando las suspensiones dejan al equipo sin el quórum mínimo habilitado.

### Inscripción del equipo representante y registro de partidos en competencias externas (Administrador)
**Historia:** Como administrador deportivo, quiero inscribir al único equipo representante del club en una competencia universitaria externa (Torneo de los Cerros o ASCUN) y registrar sus partidos frente a otras instituciones, para llevar la tabla de posiciones o el avance por fases eliminatorias.
**Entradas:** Competencia externa (Liga o Fases), equipo del club de categoría Mayores a inscribir, nómina oficial de jugadores habilitados, institución universitaria rival, escenario, fecha, alineados del encuentro y marcador final.
**Salidas y resultados:** Asociación del único equipo representante del club en la competencia, validación de que los alineados pertenezcan a la nómina oficial y no tengan suspensión activa, actualización de puntos en la tabla de posiciones (3 por victoria y 1 por empate en fútbol; 2 por victoria en baloncesto y voleibol) o avance de fase en ASCUN, y cancelación del partido si se alinea un suspendido o no se alcanza la nómina mínima habilitada.
**Criterios de Aceptación:**
- Dado un equipo de categoría Mayores que cuenta con el mínimo de jugadores habilitados, cuando el administrador registra su nómina oficial en el Torneo de los Cerros o en ASCUN, entonces ese equipo queda asignado como el único representante del club en dicha competencia.
- Dado que un equipo pertenece a una categoría juvenil (Sub-13, Sub-15 o Sub-17), cuando se intenta inscribir en una competencia universitaria externa, entonces el sistema rechaza la inscripción.
- Dado que un jugador acumula dos tarjetas amarillas o recibe una tarjeta roja en la competencia externa, cuando se intenta alinear en el siguiente partido o el equipo cae por debajo del mínimo de habilitados, entonces el partido de competencia no puede disputarse y queda cancelado.
**Reglas del caso:** Cada competencia externa tiene un único equipo representante del club (relación 0..1), exclusivo para categoría Mayores; exige validar nómina oficial, ausencias de suspensión activa y reglas de puntuación propias de cada deporte.

### Gestión de ubicaciones físicas de inventario, sugerencias y reportes consolidados (Administrador)
**Historia:** Como administrador del club, quiero trasladar artículos entre la bodega y el mostrador, reabastecer existencias, aprobar sugerencias de productos enviadas por empleados y generar reportes contables y deportivos, para supervisar integralmente la operación del club.
**Entradas:** Ubicación de inventario de origen y destino (Bodega Principal o Mostrador Tienda), artículo y cantidad a mover o reabastecer, sugerencia de empleado a revisar, y rubro/periodo para la generación de reportes.
**Salidas y resultados:** Existencias actualizadas por ubicación física y en el stock general, sugerencias aprobadas y reportes detallados de ventas por rubro (Tienda, Cafetería o consolidado), asistencia a entrenamientos, partidos disputados y evolución del ranking ELO.
**Criterios de Aceptación:**
- Dado que la Bodega Principal cuenta con existencias suficientes de un artículo, cuando el administrador traslada unidades hacia el Mostrador Tienda, entonces disminuyen las unidades en bodega y aumentan en el mostrador manteniendo intacto el stock total.
- Dado que el administrador solicita el reporte administrativo y deportivo del club, cuando se ejecuta la consulta, entonces el sistema consolida los ingresos separados por Tienda y Cafetería junto con las métricas de sesiones, partidos y rankings.
**Reglas del caso:** Trazabilidad de existencias entre Bodega Principal y Mostrador Tienda, aprobación administrativa de sugerencias de empleados y separación contable por rubros.

## Nuevas Historias de Usuario para los Torneos Internos (Proyecto 2)

### Creación de torneos internos recreativos y competitivos con reserva del 20% de cupos preferenciales (Administrador)
**Historia:** Como administrador del club, quiero crear torneos internos tanto recreativos como competitivos definiendo su deporte, categoría o nivel, modalidad, día de realización y número máximo de cupos, para fomentar la competencia interna reservando automáticamente el 20% de los cupos para miembros del club.
**Entradas:** Tipo de torneo (Recreativo o Competitivo), nombre del torneo, deporte (de conjunto o disciplina individual), categoría de edad o nivel de habilidad requerido, modalidad (Equipos, Sencillos o Dobles), número máximo de cupos, día de realización, y porcentaje del bono de descuento en tienda (si es recreativo) o tarifa de inscripción en pesos (si es competitivo).
**Salidas y resultados:** Torneo interno creado en estado de inscripciones abiertas, con el cálculo automático del 20% de sus cupos reservados como cupos preferenciales exclusivos para miembros del club (socios, equipos del club o empleados elegibles) y el 80% restante como cupos generales.
**Criterios de Aceptación:**
- Dado que el administrador crea un torneo interno con un máximo de 5 cupos, cuando el sistema inicializa el torneo, entonces reserva automáticamente 1 cupo preferencial (20%) para miembros del club y habilita 4 cupos generales para participantes externos o de la universidad.
- Dado que el torneo creado es de tipo recreativo, cuando se consulta su costo de inscripción, entonces es gratuito ($0) para todos los participantes y su premio queda configurado como un bono porcentual de descuento en la tienda del club.
- Dado que el torneo creado es de tipo competitivo, cuando se configura con una tarifa de inscripción por participante o equipo, entonces el fondo de premiación acumula los pagos recibidos para entregarlos como premio en efectivo al finalizar.
**Reglas del caso:** Dos tipos de torneos internos (Recreativo sin costo con premio en bono de tienda, y Competitivo con tarifa de inscripción y premio en efectivo equivalente al total recaudado) y reserva obligatoria del 20% de los cupos totales como cupos preferenciales para miembros del club.

### Inscripción y retiro libre de socios o equipos del club antes del inicio del torneo (Socio)
**Historia:** Como socio del club, quiero inscribirme o inscribir a mi equipo en un torneo interno que coincida con mi categoría o nivel y poder retirarme libremente mientras el torneo aún no haya comenzado, para gestionar mi participación utilizando los cupos preferenciales o generales disponibles.
**Entradas:** Socio o equipo del club solicitante, torneo interno seleccionado, nivel o categoría del participante y acción a realizar (inscripción o retiro).
**Salidas y resultados:** Registro del participante en el torneo asignándole prioritariamente un cupo preferencial si aún hay disponibles (y sumando la tarifa al recaudo si es un torneo competitivo), o eliminación de su inscripción liberando el cupo ocupado y reversando el pago si se retira antes de que el torneo inicie.
**Criterios de Aceptación:**
- Dado que el torneo interno se encuentra en estado de inscripciones abiertas y el socio o equipo cumple la categoría de edad o el nivel requerido por el torneo, cuando solicita inscribirse, entonces el sistema lo registra como participante válido.
- Dado que un socio ya está inscrito en un torneo cuyas inscripciones siguen abiertas, cuando solicita retirarse antes de que el torneo pase a estar en curso, entonces el sistema elimina su registro, devuelve el cupo a la disponibilidad del torneo y descuenta su tarifa del recaudo si era competitivo.
- Dado que el torneo ya fue iniciado por el administrador y se encuentra en curso o finalizado, cuando un participante intenta inscribirse o retirarse, entonces el sistema rechaza la operación.
**Reglas del caso:** Inscripción y retiro libres únicamente antes del inicio del torneo (estado de inscripciones abiertas), validación estricta de categoría o nivel del torneo y prohibición de doble inscripción del mismo participante.

### Liberación de cupos preferenciales no ocupados e inscripción de participantes externos (Administrador)
**Historia:** Como administrador del club, quiero inscribir participantes o equipos externos en los cupos generales de un torneo interno y poder liberar los cupos preferenciales que no hayan sido ocupados antes del inicio del torneo, para permitir que cualquier participante elegible complete la capacidad del evento.
**Entradas:** Torneo interno en etapa previa al inicio, datos del participante o equipo externo (nombre, tipo y categoría o nivel) y orden administrativa de liberación de cupos preferenciales.
**Salidas y resultados:** Bloqueo de inscripciones externas cuando los cupos generales (80%) están llenos y los cupos preferenciales (20%) siguen reservados, y habilitación inmediata de los cupos preferenciales restantes tras ejecutar su liberación para que puedan ser tomados por cualquier participante que cumpla los requisitos.
**Criterios de Aceptación:**
- Dado un torneo de 5 cupos (4 generales y 1 preferencial) donde ya se inscribieron 4 participantes externos y ningún miembro del club ha tomado el cupo preferencial, cuando un quinto participante externo intenta inscribirse antes de liberar los cupos, entonces el sistema rechaza su inscripción porque el último cupo es preferencial.
- Dado el mismo torneo antes de iniciar, cuando el administrador ejecuta la liberación de los cupos preferenciales no llenados y el quinto participante externo solicita nuevamente su inscripción, entonces el sistema acepta el registro completando los 5 cupos del torneo.
**Reglas del caso:** Los participantes externos no pueden ocupar el 20% de cupos preferenciales a menos que el administrador libere explícitamente los cupos preferenciales vacantes antes de iniciar el torneo.

### Participación gratuita de empleados fuera de turno en torneos individuales y restricción de premio en efectivo (Empleado)
**Historia:** Como empleado del club (entrenador o fisioterapeuta), quiero inscribirme sin costo en torneos internos de disciplinas individuales que se realicen en un día en el que no tenga turno laboral asignado, conociendo que si gano un torneo competitivo no recibiré el premio en efectivo.
**Entradas:** Empleado autenticado, torneo interno seleccionado, nivel de juego y programación vigente de turnos semanales del club.
**Salidas y resultados:** Inscripción gratuita ($0) del empleado cuando el torneo corresponde a una disciplina individual y el empleado no tiene turno asignado en el día del torneo; rechazo automático si el torneo es de deporte de conjunto o si el empleado está de turno ese día; y liquidación de $0 en premio en efectivo si el empleado resulta campeón de un torneo competitivo.
**Criterios de Aceptación:**
- Dado que un entrenador tiene turno asignado el día lunes e intenta inscribirse en un torneo individual programado para el lunes, cuando el sistema verifica su disponibilidad laboral, entonces rechaza la inscripción por cruce con su turno de trabajo.
- Dado que el mismo entrenador se inscribe en un torneo competitivo de tenis programado para el sábado (día en que no tiene turno) cuya tarifa para socios y externos es de $50.000, cuando el sistema registra su inscripción, entonces le cobra $0 y no incrementa el recaudo por su cupo.
- Dado que un empleado gana el primer lugar de un torneo competitivo, cuando el administrador finaliza el torneo, entonces el empleado queda registrado como campeón en el historial pero el premio en efectivo entregado es $0.
**Reglas del caso:** Los empleados solo pueden participar en torneos de disciplinas individuales cuando no estén de turno el día del torneo, nunca pagan tarifa de inscripción ($0) y, si ganan un torneo competitivo, no reciben el premio en efectivo.

### Otorgamiento y redención no acumulable del bono de descuento por ganar un torneo recreativo (Socio / Administrador)
**Historia:** Como socio ganador de un torneo interno recreativo, quiero recibir un bono de descuento para la tienda del club y redimirlo en una compra sabiendo que dicho bono no es acumulable con códigos de empleado ni con puntos de fidelidad.
**Entradas:** Torneo recreativo en curso, nombre del participante campeón al finalizar el torneo, y posterior compra en la tienda deportiva donde el socio aplica su bono de descuento.
**Salidas y resultados:** Generación automática de un bono de descuento con código único y porcentaje definido asociado al socio campeón al cerrar el torneo recreativo, y aplicación exclusiva de dicho porcentaje sobre la compra en tienda bloqueando cualquier descuento adicional en la misma factura.
**Criterios de Aceptación:**
- Dado que un socio gana un torneo recreativo configurado con un bono del 25%, cuando el administrador finaliza el torneo, entonces el sistema cambia el estado a finalizado y acredita el bono en la cuenta del socio campeón.
- Dado que el socio aplica su bono de torneo recreativo en una compra de la tienda deportiva, cuando intenta aplicar además un código de descuento de empleado o redimir puntos de fidelidad en esa misma venta, entonces el sistema rechaza los descuentos adicionales por tratarse de un bono no acumulable.
**Reglas del caso:** Los torneos recreativos otorgan al campeón un bono porcentual de descuento en la tienda del club de un solo uso y estrictamente no acumulable con otros descuentos o redenciones de puntos.

### Registro de encuentros y consulta del estado e historial detallado de torneos en curso y finalizados (Socio / Empleado / Administrador)
**Historia:** Como usuario del sistema (socio, empleado o administrador), quiero consultar en cualquier momento el estado actual y el historial completo de los torneos internos en curso y finalizados, incluyendo sus participantes, los resultados de cada encuentro, el campeón y los premios otorgados.
**Entradas:** Registro de encuentros por ronda (fase, participante A, participante B, marcador y ganador) por parte del administrador mientras el torneo está en curso, y solicitud de consulta del estado e historial desde cualquiera de las tres consolas.
**Salidas y resultados:** Reporte detallado de cada torneo interno mostrando su tipo, deporte, categoría o nivel, modalidad, estado actual (Inscripciones Abiertas, En curso o Finalizado), distribución de cupos totales y preferenciales, listado de participantes inscritos con el valor pagado, historial cronológico de encuentros con sus marcadores, campeón registrado y resumen del premio en efectivo o bono otorgado.
**Criterios de Aceptación:**
- Dado un torneo interno que cuenta con al menos dos inscritos, cuando el administrador lo inicia y registra los marcadores de las rondas disputadas, entonces cada encuentro queda almacenado en el historial del torneo.
- Dado que existen torneos con inscripciones abiertas, en curso y finalizados en el club, cuando un socio, un empleado o el administrador consulta el historial de torneos desde su consola, entonces el sistema despliega la información completa de participantes, partidos jugados, ganadores y premios de todos los torneos.
**Reglas del caso:** Un torneo requiere mínimo dos inscritos para iniciar, solo permite registrar encuentros mientras está en curso y conserva de forma persistente todo su historial de participantes, enfrentamientos, ganador y premiación.
