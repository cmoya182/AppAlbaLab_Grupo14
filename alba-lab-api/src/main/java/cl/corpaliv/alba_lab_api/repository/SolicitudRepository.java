package cl.corpaliv.alba_lab_api.repository;

import cl.corpaliv.alba_lab_api.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findAllByOrderByIdDesc();
}