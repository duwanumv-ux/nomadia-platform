/**
 * NomadIA · Cierre del modal de la Ley de Rigidez Financiera.
 * Al cerrarlo, el foco vuelve al campo "monto" para que el usuario ajuste el valor.
 */
(function () {
  'use strict';

  const modal = document.getElementById('modal-rigidez');
  if (!modal) {
    return;
  }

  function cerrarModal() {
    modal.hidden = true;
    const campoMonto = document.getElementById('monto');
    if (campoMonto) {
      campoMonto.focus();
      campoMonto.select();
    }
  }

  modal.querySelector('[data-cerrar-modal]').addEventListener('click', cerrarModal);
  document.addEventListener('keydown', function (evento) {
    if (evento.key === 'Escape') {
      cerrarModal();
    }
  });
  modal.querySelector('[data-cerrar-modal]').focus();
})();
