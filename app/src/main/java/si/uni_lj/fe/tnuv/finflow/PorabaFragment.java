package si.uni_lj.fe.tnuv.finflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    LinearLayout llStroski;
    LinearLayout llLegenda;
    PieChart chartPoraba;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poraba, container, false);

        tvSkupnaPoraba = view.findViewById(R.id.tv_skupna_poraba);
        llStroski = view.findViewById(R.id.ll_stroski_container);
        chartPoraba = view.findViewById(R.id.chart_poraba);
        llLegenda = view.findViewById(R.id.ll_legenda_poraba);

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
        DialogHelper.prikaziPotrditev(getContext(), "Izbriši strošek", "Ali res želiš izbrisati ta vnos?", R.color.temnoModra, () -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.dao().deleteStrosek(strosek);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::posodobiPrikaz);
                }
            });
        });
    }

    private void prikaziGraf(List<KategorijaVsota> poKategorijah) {
        List<PieEntry> vnosi = new ArrayList<>();
        for (KategorijaVsota kv : poKategorijah) {
            vnosi.add(new PieEntry((float) kv.vsota, kv.kategorija));
        }

        int[] barve = {
                getResources().getColor(R.color.graf1),
                getResources().getColor(R.color.graf2),
                getResources().getColor(R.color.graf3),
                getResources().getColor(R.color.graf4),
                getResources().getColor(R.color.graf5),
                getResources().getColor(R.color.graf6)
        };

        PieDataSet dataSet = new PieDataSet(vnosi, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(barve);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartPoraba));

        chartPoraba.setData(data);
        chartPoraba.setUsePercentValues(true);
        chartPoraba.getDescription().setEnabled(false);
        chartPoraba.setEntryLabelColor(Color.WHITE);
        chartPoraba.setEntryLabelTextSize(18f);
        chartPoraba.setDrawEntryLabels(false);
        chartPoraba.setDrawHoleEnabled(false);
        chartPoraba.getLegend().setEnabled(false);

        chartPoraba.animateY(600);
        chartPoraba.invalidate();

        prikaziLegendo(poKategorijah, barve);
    }

    private void prikaziLegendo(List<KategorijaVsota> poKategorijah, int[] barve) {
        llLegenda.removeAllViews();

        int naVrstico = 3;
        LinearLayout trenutnaVrstica = null;

        for (int i = 0; i < poKategorijah.size(); i++) {
            if (i % naVrstico == 0) {
                trenutnaVrstica = new LinearLayout(getContext());
                trenutnaVrstica.setOrientation(LinearLayout.HORIZONTAL);
                trenutnaVrstica.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams vrsticaParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                vrsticaParams.topMargin = dpToPx(8);
                trenutnaVrstica.setLayoutParams(vrsticaParams);
                llLegenda.addView(trenutnaVrstica);
            }

            KategorijaVsota kv = poKategorijah.get(i);
            int barva = barve[i % barve.length];

            LinearLayout postavka = new LinearLayout(getContext());
            postavka.setOrientation(LinearLayout.HORIZONTAL);
            postavka.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams postavkaParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i % naVrstico != 0) {
                postavkaParams.setMarginStart(dpToPx(14));
            }
            postavka.setLayoutParams(postavkaParams);

            View pika = new View(getContext());
            int velikostPike = dpToPx(12);
            LinearLayout.LayoutParams pikaParams = new LinearLayout.LayoutParams(velikostPike, velikostPike);
            pikaParams.setMarginEnd(dpToPx(6));
            pika.setLayoutParams(pikaParams);

            GradientDrawable oblika = new GradientDrawable();
            oblika.setShape(GradientDrawable.OVAL);
            oblika.setColor(barva);
            pika.setBackground(oblika);

            TextView besedilo = new TextView(getContext());
            besedilo.setText(kv.kategorija);
            besedilo.setTextSize(18f);
            besedilo.setTextColor(getResources().getColor(R.color.besediloTemno));

            postavka.addView(pika);
            postavka.addView(besedilo);
            trenutnaVrstica.addView(postavka);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}