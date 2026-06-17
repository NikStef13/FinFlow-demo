package si.uni_lj.fe.tnuv.finflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FinFlowDao {
    @Insert
    void insertCilj(Cilj cilj);

    @Query("SELECT * FROM cilji")
    List<Cilj> getAllCilji();

    @Insert
    void insertStrosek(Strosek strosek);

    @Query("SELECT * FROM stroski ORDER BY datum DESC")
    List<Strosek> getAllStroski();

    @Query("SELECT SUM(znesek) FROM stroski")
    double getTotalConsumption();
}
