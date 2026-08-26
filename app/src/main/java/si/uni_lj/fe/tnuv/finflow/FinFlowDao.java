package si.uni_lj.fe.tnuv.finflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FinFlowDao {
    @Insert
    void insertCilj(Cilj cilj);

    @Update
    void updateCilj(Cilj cilj);

    @Delete
    void deleteCilj(Cilj cilj);

    @Query("SELECT * FROM cilji")
    List<Cilj> getAllCilji();

    @Query("SELECT * FROM cilji WHERE id = :id")
    Cilj getCiljById(int id);

    @Insert
    void insertStrosek(Strosek strosek);

    @Update
    void updateStrosek(Strosek strosek);

    @Delete
    void deleteStrosek(Strosek strosek);

    @Query("SELECT * FROM stroski ORDER BY datum DESC")
    List<Strosek> getAllStroski();

    @Query("SELECT * FROM stroski WHERE id = :id")
    Strosek getStrosekById(int id);

    @Query("SELECT SUM(znesek) FROM stroski")
    double getTotalConsumption();

    @Query("SELECT kategorija, SUM(znesek) as vsota FROM stroski GROUP BY kategorija")
    List<KategorijaVsota> getPorabaPoKategorijah();
}