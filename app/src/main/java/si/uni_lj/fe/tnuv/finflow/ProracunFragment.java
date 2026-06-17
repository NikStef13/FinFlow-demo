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

public class ProracunFragment extends Fragment {

    SharedPreferences sp;
    TextView tvLimit, tvOdstotek, tvPorabljeno, tvPreostalo;
    ProgressBar pbProracun;
    LinearLayout layoutVnosLimit;

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
                    tvLimit.setText(String.format("€%.2f", limit));
                    tvPorabljeno.setText(String.format("€%.2f", porabljeno));
                    tvPreostalo.setText(String.format("€%.2f", preostalo));
                    tvOdstotek.setText(odstotek + "%");
                    pbProracun.setProgress(odstotek);
                });
            }
        });
    }
}