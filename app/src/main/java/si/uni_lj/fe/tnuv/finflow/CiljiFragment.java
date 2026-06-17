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

public class CiljiFragment extends Fragment {

    TextView tvSkupniCilji, tvSkupniCiljiOpis;
    ListView lvCilji;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cilji, container, false);

        tvSkupniCilji = view.findViewById(R.id.tv_skupni_cilji);
        tvSkupniCiljiOpis = view.findViewById(R.id.tv_skupni_cilji_opis);
        lvCilji = view.findViewById(R.id.lv_cilji);

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
                    tvSkupniCiljiOpis.setText(String.format("od €%.2f skupnega cilja", finalSkupniCilj));

                    List<String> ciljiString = new ArrayList<>();
                    for (Cilj c : cilji) {
                        ciljiString.add(c.ikona + " " + c.ime + ": €" + c.prihranjeno + " / €" + c.ciljniZnesek);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, ciljiString);
                    lvCilji.setAdapter(adapter);
                });
            }
        });
    }
}
