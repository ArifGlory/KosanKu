package myproject.kosanku.Kelas;

public class Fasilitas {
    public String idFasilitas;
    public String namaFasilitas;

    public Fasilitas(){

    }

    public Fasilitas(String idFasilitas, String namaFasilitas) {
        this.idFasilitas = idFasilitas;
        this.namaFasilitas = namaFasilitas;
    }

    public String getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(String idFasilitas) {
        this.idFasilitas = idFasilitas;
    }

    public String getNamaFasilitas() {
        return namaFasilitas;
    }

    public void setNamaFasilitas(String namaFasilitas) {
        this.namaFasilitas = namaFasilitas;
    }
}
