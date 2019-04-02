package myproject.kosanku.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Kosan implements Serializable {
    public String namaKos;
    public String alamat;
    public int harga;
    public int sisaKamar;
    public String uidPemilik;
    public String gambarUtama;
    public String idKos;
    public String latlon;


    public Kosan(String namaKos, String alamat, int harga, int sisaKamar, String uidPemilik, String gambarUtama, String idKos, String latlon) {
        this.namaKos = namaKos;
        this.alamat = alamat;
        this.harga = harga;
        this.sisaKamar = sisaKamar;
        this.uidPemilik = uidPemilik;
        this.gambarUtama = gambarUtama;
        this.idKos = idKos;
        this.latlon = latlon;
    }

    public Kosan(){

    }

    public String getNamaKos() {
        return namaKos;
    }

    public void setNamaKos(String namaKos) {
        this.namaKos = namaKos;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getSisaKamar() {
        return sisaKamar;
    }

    public void setSisaKamar(int sisaKamar) {
        this.sisaKamar = sisaKamar;
    }

    public String getUidPemilik() {
        return uidPemilik;
    }

    public void setUidPemilik(String uidPemilik) {
        this.uidPemilik = uidPemilik;
    }

    public String getGambarUtama() {
        return gambarUtama;
    }

    public void setGambarUtama(String gambarUtama) {
        this.gambarUtama = gambarUtama;
    }


    public String getIdKos() {
        return idKos;
    }

    public void setIdKos(String idKos) {
        this.idKos = idKos;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }
}
