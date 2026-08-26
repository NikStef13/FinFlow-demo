package si.uni_lj.fe.tnuv.finflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class PorabaFragment extends Fragment implements StrosekAdapter.Listener {

    TextView tvSkupnaPoraba;
    ListView lvStroski;
    PieChart chartPoraba;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poraba, container, false);

        tvSkupnaPoraba = view.findViewById(R.id.tv_skupna_poraba);
        lvStroski = view.findViewById(R.id.lv_stroski);
        chartPoraba = view.findViewById(R.id.chart_poraba);

        Button btnDodajStrosek = view.findViewById(R.id.btn_dodaj_strosek);
        btnDodajStrosek.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DodajStrosekActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        posodobiPrikaz();
    }

    private void posodobiPrikaz() {
        AppDatabase db = AppDatabase.getDatabase(getContext());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            double skupaj = db.dao().getTotalConsumption();
            List<Strosek> stroski = db.dao().getAllStroski();
            List<KategorijaVsota> poKategorijah = db.dao().getPorabaPoKategorijah();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvSkupnaPoraba.setText(String.format("€%.2f", skupaj));

                    StrosekAdapter adapter = new StrosekAdapter(getContext(), stroski, this);
                    lvStroski.setAdapter(adapter);

                    prikaziGraf(poKategorijah);
                });
            }
        });
    }

    @Override
    public void onUredi(Strosek strosek) {
        Intent intent = new Intent(getActivity(), DodajStrosekActivity.class);
        intent.putExtra("strosek_id", strosek.id);
        startActivity(intent);
    }

    @Override
    public void onIzbrisi(Strosek strosek) {
        new AlertDialog.Builder(getContext())
                .setTitle("Izbriši strošek")
                .setMessage("Ali res želiš izbrisati ta vnos?")
                .setPositiveButton("Izbriši", (dialog, which) -> {
                    AppDatabase db = AppDatabase.getDatabase(getContext());
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().deleteStrosek(strosek);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(this::posodobiPrikaz);
                        }
                    });
                })
                .setNegativeButton("Prekliči", null)
                .show();
    }

    private void prikaziGraf(List<KategorijaVsota> poKategorijah) {
        List<PieEntry> vnosi = new ArrayList<>();
        for (KategorijaVsota kv : poKategorijah) {
            vnosi.add(new PieEntry((float) kv.vsota, kv.kategorija));
        }

        PieDataSet dataSet = new PieDataSet(vnosi, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(
                getResources().getColor(R.color.graf1),
                getResources().getColor(R.color.graf2),
                getResources().getColor(R.color.graf3),
                getResources().getColor(R.color.graf4),
                getResources().getColor(R.color.graf5),
                getResources().getColor(R.color.graf6)
        );
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(16f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartPoraba));

        chartPoraba.setData(data);
        chartPoraba.setUsePercentValues(true);
        chartPoraba.getDescription().setEnabled(false);
        chartPoraba.setEntryLabelColor(Color.WHITE);
        chartPoraba.setEntryLabelTextSize(16f);
        chartPoraba.setDrawHoleEnabled(false);
        chartPoraba.getLegend().setEnabled(false);

        chartPoraba.animateY(600);
        chartPoraba.invalidate();
    }
}