/**
 * NomadIA · Pantalla "Procesando".
 * Espera un instante (como en el prototipo) y lleva al usuario al resumen del viaje.
 */
(function () {
  'use strict';

  const ESPERA_MILISEGUNDOS = 1800;
  const seccion = document.getElementById('procesando');

  if (seccion && seccion.dataset.destino) {
    window.setTimeout(function () {
      window.location.replace(seccion.dataset.destino);
    }, ESPERA_MILISEGUNDOS);
  }
})();
