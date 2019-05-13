package myproject.kosanku.Kelas;

import java.io.Serializable;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Rating implements Serializable {
    public int rate;
    public String komentar;

    public Rating(){

    }

    public Rating(int rate, String komentar) {
        this.rate = rate;
        this.komentar = komentar;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
}
