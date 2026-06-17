package si.uni_lj.fe.tnuv.finflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stroski")
public class Strosek {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double znesek;
    public String opis;
    public String kategorija;
    public long datum;

    public Strosek(double znesek, String opis, String kategorija, long datum) {
        this.znesek = znesek;
        this.opis = opis;
        this.kategorija = kategorija;
        this.datum = datum;
    }
}
