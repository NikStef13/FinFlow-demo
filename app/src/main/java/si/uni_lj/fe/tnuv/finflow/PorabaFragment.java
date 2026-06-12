package si.uni_lj.fe.tnuv.finflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class PorabaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poraba, container, false);

        Button btnDodajStrosek = view.findViewById(R.id.btn_dodaj_strosek);
        btnDodajStrosek.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DodajStrosekActivity.class);
            startActivity(intent);
        });

        return view;
    }
}