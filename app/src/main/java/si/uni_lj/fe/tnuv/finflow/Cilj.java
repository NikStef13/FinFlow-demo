package si.uni_lj.fe.tnuv.finflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cilji")
public class Cilj {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String ime;
    public double ciljniZnesek;
    public double prihranjeno;
    public String rok;
    public String ikona;

    public Cilj(String ime, double ciljniZnesek, double prihranjeno, String rok, String ikona) {
        this.ime = ime;
        this.ciljniZnesek = ciljniZnesek;
        this.prihranjeno = prihranjeno;
        this.rok = rok;
        this.ikona = ikona;
    }
}
