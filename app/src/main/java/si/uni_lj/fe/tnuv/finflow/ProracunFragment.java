package si.uni_lj.fe.tnuv.finflow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProracunFragment extends Fragment {

    SharedPreferences sp;
    TextView tvLimit, tvOdstotek, tvPorabljeno, tvPreostalo;
    ProgressBar pbProracun;
    LinearLayout layoutVnosLimit, llNasveti;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proracun, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        tvLimit = view.findViewById(R.id.tv_limit);
        tvOdstotek = view.findViewById(R.id.tv_odstotek);
        tvPorabljeno = view.findViewById(R.id.tv_porabljeno);
        tvPreostalo = view.findViewById(R.id.tv_preostalo);
        pbProracun = view.findViewById(R.id.pb_proracun);
        layoutVnosLimit = view.findViewById(R.id.layout_vnos_limit);
        llNasveti = view.findViewById(R.id.ll_nasveti_container);

        ImageButton btnUrediLimit = view.findViewById(R.id.btn_uredi_limit);
        Button btnPotrdiLimit = view.findViewById(R.id.btn_potrdi_limit);
        Button btnPrekliciLimit = view.findViewById(R.id.btn_preklici_limit);
        EditText etLimit = view.findViewById(R.id.et_limit);

        // Naložimo shranjene vrednosti
        posodobiPrikaz();

        // Klik na svinčnik — pokaži vnosno polje
        btnUrediLimit.setOnClickListener(v -> {
            layoutVnosLimit.setVisibility(View.VISIBLE);
            tvLimit.setVisibility(View.GONE);
            btnUrediLimit.setVisibility(View.GONE);
        });

        // Potrdi nov limit
        btnPotrdiLimit.setOnClickListener(v -> {
            String vnos = etLimit.getText().toString();
            if (!vnos.isEmpty()) {
                float limit = Float.parseFloat(vnos);
                sp.edit().putFloat("limit", limit).apply();
                skrijiVnos(btnUrediLimit);
                posodobiPrikaz();
            }
        });

        // Prekliči
        btnPrekliciLimit.setOnClickListener(v -> {
            skrijiVnos(btnUrediLimit);
        });

        return view;
    }

    private void skrijiVnos(ImageButton btnUrediLimit) {
        layoutVnosLimit.setVisibility(View.GONE);
        tvLimit.setVisibility(View.VISIBLE);
        btnUrediLimit.setVisibility(View.VISIBLE);
    }

    private void posodobiPrikaz() {
        float limit = sp.getFloat("limit", 0f);

        AppDatabase db = AppDatabase.getDatabase(getContext());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            double porabljeno = db.dao().getTotalConsumption();
            float preostalo = limit - (float) porabljeno;
            int odstotek = limit > 0 ? (int) ((porabljeno / limit) * 100) : 0;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String limitStr = String.format("€%.2f", limit);
                    tvLimit.setText(limitStr);
                    tvPorabljeno.setText(String.format("€%.2f", porabljeno));
                    tvPreostalo.setText(String.format("€%.2f", preostalo));
                    tvOdstotek.setText(odstotek + "%");
                    pbProracun.setProgress(odstotek);

                    generirajNasvete(db);
                });
            }
        });
    }

    private void generirajNasvete(AppDatabase db) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<KategorijaVsota> porabaPoKategorijah = db.dao().getPorabaPoKategorijah();
            
            // Sortiranje po vsoti (padajoče)
            Collections.sort(porabaPoKategorijah, (o1, o2) -> Double.compare(o2.vsota, o1.vsota));

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    llNasveti.removeAllViews();

                    int stNasvetov = 0;
                    for (KategorijaVsota kv : porabaPoKategorijah) {
                        if (stNasvetov >= 3) break;
                        
                        String[] nasvet = dobiNasvetZaKategorijo(kv.kategorija);
                        if (nasvet != null) {
                            dodajNasvetVUI(nasvet[0], nasvet[1]);
                            stNasvetov++;
                        }
                    }

                    // Če ni dovolj porabe, dodaj splošne nasvete
                    if (stNasvetov == 0) {
                        dodajNasvetVUI(getString(R.string.proracun_nasvet_prazen_naslov), getString(R.string.proracun_nasvet_prazen_opis));
                    }
                });
            }
        });
    }

    private String[] dobiNasvetZaKategorijo(String kategorija) {
        Map<String, String[]> nasvetiMap = new HashMap<>();

        nasvetiMap.put("Hrana", new String[]{getString(R.string.nasvet_hrana_naslov), getString(R.string.nasvet_hrana_opis)});
        nasvetiMap.put("Kava", new String[]{getString(R.string.nasvet_kava_naslov), getString(R.string.nasvet_kava_opis)});
        nasvetiMap.put("Prevoz", new String[]{getString(R.string.nasvet_prevoz_naslov), getString(R.string.nasvet_prevoz_opis)});
        nasvetiMap.put("Stanovanje", new String[]{getString(R.string.nasvet_stanovanje_naslov), getString(R.string.nasvet_stanovanje_opis)});
        nasvetiMap.put("Tehnologija", new String[]{getString(R.string.nasvet_tehnologija_naslov), getString(R.string.nasvet_tehnologija_opis)});
        nasvetiMap.put("Drugo", new String[]{getString(R.string.nasvet_drugo_naslov), getString(R.string.nasvet_drugo_opis)});

        return nasvetiMap.get(kategorija);
    }

    private void dodajNasvetVUI(String naslov, String opis) {
        LinearLayout tipLayout = new LinearLayout(getContext());
        tipLayout.setOrientation(LinearLayout.VERTICAL);
        tipLayout.setBackgroundResource(R.color.sivaKartica);
        tipLayout.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(8), 0, 0);
        tipLayout.setLayoutParams(params);

        TextView tvNaslov = new TextView(getContext());
        tvNaslov.setText(naslov);
        tvNaslov.setTextColor(getResources().getColor(R.color.besediloTemno));
        tvNaslov.setTextSize(14);
        tvNaslov.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvOpis = new TextView(getContext());
        tvOpis.setText(opis);
        tvOpis.setTextColor(getResources().getColor(R.color.besediloSivo));
        tvOpis.setTextSize(13);
        
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.setMargins(0, dpToPx(2), 0, 0);
        tvOpis.setLayoutParams(descParams);

        tipLayout.addView(tvNaslov);
        tipLayout.addView(tvOpis);
        llNasveti.addView(tipLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}