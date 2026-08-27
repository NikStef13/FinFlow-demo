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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class CiljiFragment extends Fragment implements CiljAdapter.Listener {

    TextView tvSkupniCilji, tvSkupniCiljiOpis;
    LinearLayout llCilji;
    PieChart chartSkupniCilji;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cilji, container, false);

        tvSkupniCilji = view.findViewById(R.id.tv_skupni_cilji);
        tvSkupniCiljiOpis = view.findViewById(R.id.tv_skupni_cilji_opis);
        llCilji = view.findViewById(R.id.ll_cilji_container);
        chartSkupniCilji = view.findViewById(R.id.chart_skupni_cilji);

        Button btnDodajCilj = view.findViewById(R.id.btn_dodaj_cilj);
        btnDodajCilj.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DodajCiljActivity.class);
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
            List<Cilj> cilji = db.dao().getAllCilji();
            double skupajPrihranjeno = 0;
            double skupniCilj = 0;

            for (Cilj c : cilji) {
                skupajPrihranjeno += c.prihranjeno;
                skupniCilj += c.ciljniZnesek;
            }

            double finalSkupajPrihranjeno = skupajPrihranjeno;
            double finalSkupniCilj = skupniCilj;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvSkupniCilji.setText(String.format("€%.2f", finalSkupajPrihranjeno));
                    tvSkupniCiljiOpis.setText(getString(R.string.cilji_skupni_opis, String.format("€%.2f", finalSkupniCilj)));

                    llCilji.removeAllViews();
                    CiljAdapter adapter = new CiljAdapter(getContext(), cilji, this);
                    for (int i = 0; i < adapter.getCount(); i++) {
                        llCilji.addView(adapter.getView(i, null, llCilji));
                    }

                    prikaziSkupniGraf(finalSkupajPrihranjeno, finalSkupniCilj);
                });
            }
        });
    }

    @Override
    public void onUredi(Cilj cilj) {
        Intent intent = new Intent(getActivity(), DodajCiljActivity.class);
        intent.putExtra("cilj_id", cilj.id);
        startActivity(intent);
    }

    @Override
    public void onIzbrisi(Cilj cilj) {
        DialogHelper.prikaziPotrditev(getContext(), "Izbriši cilj", "Ali res želiš izbrisati ta cilj?", R.color.zelena, () -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.dao().deleteCilj(cilj);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::posodobiPrikaz);
                }
            });
        });
    }

    @Override
    public void onSpremenjenoPrihranjeno(Cilj cilj, double noviZnesek) {
        cilj.prihranjeno = noviZnesek;
        AppDatabase db = AppDatabase.getDatabase(getContext());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.dao().updateCilj(cilj);
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::posodobiPrikaz);
            }
        });
    }

    private void prikaziSkupniGraf(double prihranjeno, double skupniCilj) {
        List<PieEntry> vnosi = new ArrayList<>();
        
        if (skupniCilj <= 0) {
            vnosi.add(new PieEntry(1, ""));
        } else {
            double delej = Math.min(prihranjeno, skupniCilj);
            vnosi.add(new PieEntry((float) delej, ""));
            if (skupniCilj > prihranjeno) {
                vnosi.add(new PieEntry((float) (skupniCilj - prihranjeno), ""));
            }
        }

        PieDataSet dataSet = new PieDataSet(vnosi, "");
        if (skupniCilj <= 0) {
            dataSet.setColors(Color.parseColor("#40FFFFFF")); // Polprosojna bela za prazen cilj
        } else {
            dataSet.setColors(
                    Color.WHITE,
                    Color.parseColor("#40FFFFFF") // Polprosojna bela za preostanek
            );
        }
        
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        chartSkupniCilji.setData(data);
        chartSkupniCilji.setHoleColor(Color.TRANSPARENT);
        chartSkupniCilji.setTransparentCircleRadius(0f);
        chartSkupniCilji.setHoleRadius(60f); // Donut izgled
        chartSkupniCilji.getDescription().setEnabled(false);
        chartSkupniCilji.getLegend().setEnabled(false);
        chartSkupniCilji.setDrawEntryLabels(false);
        chartSkupniCilji.setRotationEnabled(false);
        chartSkupniCilji.animateY(800);
        chartSkupniCilji.invalidate();
    }
}