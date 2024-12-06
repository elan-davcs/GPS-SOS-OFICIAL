package com.example.gpssosoficial.Modelos;

public class solicitud {
    String nombre, apellido, celular, id;

    public solicitud(){}
    public solicitud(String nombre, String apellido, String celular, String id) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.celular = celular;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
