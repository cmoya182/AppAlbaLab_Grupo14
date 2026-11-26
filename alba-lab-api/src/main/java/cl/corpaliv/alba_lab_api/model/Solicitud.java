package cl.corpaliv.alba_lab_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoContacto;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(nullable = false)
    private String fecha;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    public Solicitud() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoContacto() { return tipoContacto; }
    public void setTipoContacto(String tipoContacto) { this.tipoContacto = tipoContacto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}