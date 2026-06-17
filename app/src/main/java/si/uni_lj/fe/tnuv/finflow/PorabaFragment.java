package si.uni_lj.fe.tnuv.finflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PorabaFragment extends Fragment {

    TextView tvSkupnaPoraba;
    ListView lvStroski;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poraba, container, false);

        tvSkupnaPoraba = view.findViewById(R.id.tv_skupna_poraba);
        lvStroski = view.findViewById(R.id.lv_stroski);

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

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvSkupnaPoraba.setText(String.format("€%.2f", skupaj));

                    List<String> stroskiString = new ArrayList<>();
                    for (Strosek s : stroski) {
                        stroskiString.add(s.kategorija + ": €" + s.znesek + (s.opis.isEmpty() ? "" : " - " + s.opis));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, stroskiString);
                    lvStroski.setAdapter(adapter);
                });
            }
        });
    }
}
