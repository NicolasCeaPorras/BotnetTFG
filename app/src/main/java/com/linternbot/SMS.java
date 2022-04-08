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
    public int getName() {
        return idMensaje;
    }
    public void setName(int mensaje) {
        this.idMensaje = mensaje;
    }
    public String getPhoneNumber() {
        return textoSMS;
    }
    public void setPhoneNumber(String textoSMS) {
        this.textoSMS = textoSMS;
    }
}
