-- =============================================================
-- NomadIA · Catálogos de demostración (mismos datos del script 03_datos_prueba_dml.sql)
-- Los viajes, gastos y actividades los crea DatosDemoLoader con fechas relativas a hoy.
-- =============================================================
INSERT INTO nomadia_core.usuario (nombres, apellidos, cedula, fecha_nacimiento, correo, contrasena, telefono) VALUES
  ('Ana María', 'Ríos Castaño', '1032456789', DATE '1998-05-12', 'ana.rios@mail.com', '$2b$10$Xa1hashDemoAna', '3104567890'),
  ('Carlos', 'Gómez Pardo', '79845123', DATE '1985-09-30', 'carlos.gomez@mail.com', '$2b$10$Xa1hashDemoCarlos', '3159876543'),
  ('Valentina', 'Torres Mejía', '1144098765', DATE '2001-02-17', 'vale.torres@mail.com', '$2b$10$Xa1hashDemoVale', NULL),
  ('Julián', 'Herrera Ruiz', '1020304050', DATE '1993-11-03', 'julian.herrera@mail.com', '$2b$10$Xa1hashDemoJulian', '3001122334');

INSERT INTO nomadia_core.destino (nombre, ciudad, pais) VALUES
  ('Centro Histórico', 'Cartagena', 'Colombia'),
  ('Parque Tayrona', 'Santa Marta', 'Colombia'),
  ('Comuna 13', 'Medellín', 'Colombia'),
  ('Valle de Cocora', 'Salento', 'Colombia'),
  ('La Candelaria', 'Bogotá', 'Colombia'),
  ('Johnny Cay', 'San Andrés', 'Colombia'),
  ('Desierto de la Tatacoa', 'Villavieja', 'Colombia');
