package si.uni_lj.fe.tnuv.finflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class PorabaFragment extends Fragment implements StrosekAdapter.Listener {

    TextView tvSkupnaPoraba;
    LinearLayout llStroski;
    PieChart chartPoraba;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poraba, container, false);

        tvSkupnaPoraba = view.findViewById(R.id.tv_skupna_poraba);
        llStroski = view.findViewById(R.id.ll_stroski_container);
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

                    llStroski.removeAllViews();
                    StrosekAdapter adapter = new StrosekAdapter(getContext(), stroski, this);
                    for (int i = 0; i < adapter.getCount(); i++) {
                        llStroski.addView(adapter.getView(i, null, llStroski));
                    }

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
                .setTitle(R.string.poraba_izbrisi_naslov)
                .setMessage(R.string.poraba_izbrisi_potrditev)
                .setPositiveButton(R.string.gumb_izbrisi, (dialog, which) -> {
                    AppDatabase db = AppDatabase.getDatabase(getContext());
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().deleteStrosek(strosek);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(this::posodobiPrikaz);
                        }
                    });
                })
                .setNegativeButton(R.string.gumb_preklici, null)
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
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartPoraba));

        chartPoraba.setData(data);
        chartPoraba.setUsePercentValues(true);
        chartPoraba.getDescription().setEnabled(false);
        chartPoraba.setEntryLabelColor(Color.WHITE);
        chartPoraba.setEntryLabelTextSize(16f);
        chartPoraba.setDrawEntryLabels(false);
        chartPoraba.setDrawHoleEnabled(false);
        
        Legend l = chartPoraba.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
        l.setMaxSizePercent(0.85f);
        l.setYOffset(10f);
        l.setXOffset(0f);
        l.setXEntrySpace(8f);
        l.setYEntrySpace(5f);
        l.setFormToTextSpace(6f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(11f);
        l.setTextColor(getResources().getColor(R.color.besediloTemno));

        chartPoraba.animateY(600);
        chartPoraba.invalidate();
    }
}