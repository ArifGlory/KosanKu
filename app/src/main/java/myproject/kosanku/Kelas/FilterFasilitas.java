package myproject.kosanku.Kelas;

public class FilterFasilitas {

    public String idKos;
    public String idFasilitas;

    public FilterFasilitas(String idKos, String idFasilitas) {
        this.idKos = idKos;
        this.idFasilitas = idFasilitas;
    }

    public FilterFasilitas(){

    }

    public String getIdKos() {
        return idKos;
    }

    public void setIdKos(String idKos) {
        this.idKos = idKos;
    }

    public String getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(String idFasilitas) {
        this.idFasilitas = idFasilitas;
    }
}
