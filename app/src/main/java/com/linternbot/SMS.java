package com.linternbot;

public class SMS {
    public int idMensaje;
    public String textoSMS;

    public SMS() {
    }
    public SMS(int mensaje, String textoSMS ) {
        this.idMensaje = mensaje;
        this.textoSMS = textoSMS;
    }
    public int getIdMensaje() {
        return idMensaje;
    }
    public void setIdMensaje(int mensaje) {
        this.idMensaje = mensaje;
    }
    public String getTextoSMS() {
        return textoSMS;
    }
    public void setTextoSMS(String textoSMS) {
        this.textoSMS = textoSMS;
    }
}
