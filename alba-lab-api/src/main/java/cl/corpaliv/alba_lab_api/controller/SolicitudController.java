package cl.corpaliv.alba_lab_api.controller;

import cl.corpaliv.alba_lab_api.model.Solicitud;
import cl.corpaliv.alba_lab_api.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {
    private final SolicitudRepository repository;

    public SolicitudController(SolicitudRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        return ResponseEntity.ok(repository.findAllByOrderByIdDesc());
    }

    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Solicitud solicitud) {
        if (solicitud.getEstado() == null || solicitud.getEstado().isBlank()) {
            solicitud.setEstado("PENDIENTE");
        }
        Solicitud guardada = repository.save(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> actualizarSolicitud(
            @PathVariable Long id,
            @RequestBody Solicitud datosActualizados
    ) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setTipoContacto(datosActualizados.getTipoContacto());
                    existente.setNombre(datosActualizados.getNombre());
                    existente.setCorreo(datosActualizados.getCorreo());
                    existente.setTelefono(datosActualizados.getTelefono());
                    existente.setMensaje(datosActualizados.getMensaje());
                    existente.setFecha(datosActualizados.getFecha());
                    if (datosActualizados.getEstado() != null) {
                        existente.setEstado(datosActualizados.getEstado());
                    }
                    return ResponseEntity.ok(repository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}