package myproject.kosanku.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Gambar implements Serializable {
    public String urlGambar;

    public  Gambar(){

    }

    public Gambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }
}
