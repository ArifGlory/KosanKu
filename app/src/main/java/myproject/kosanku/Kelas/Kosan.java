package myproject.kosanku.Kelas;

public class Kosan {
    public String namaKos;
    public String alamat;
    public int harga;
    public int sisaKamar;
    public String uidPemilik;
    public String gambarUtama;
    public String nopePemilik;
    public String idKos;
    public String latlon;


    public Kosan(String namaKos, String alamat, int harga, int sisaKamar, String uidPemilik, String gambarUtama, String nopePemilik, String idKos, String latlon) {
        this.namaKos = namaKos;
        this.alamat = alamat;
        this.harga = harga;
        this.sisaKamar = sisaKamar;
        this.uidPemilik = uidPemilik;
        this.gambarUtama = gambarUtama;
        this.nopePemilik = nopePemilik;
        this.idKos = idKos;
        this.latlon = latlon;
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

    public String getNopePemilik() {
        return nopePemilik;
    }

    public void setNopePemilik(String nopePemilik) {
        this.nopePemilik = nopePemilik;
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
