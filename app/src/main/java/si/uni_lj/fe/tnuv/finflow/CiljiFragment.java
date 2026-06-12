package si.uni_lj.fe.tnuv.finflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class CiljiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cilji, container, false);

        Button btnDodajCilj = view.findViewById(R.id.btn_dodaj_cilj);
        btnDodajCilj.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DodajCiljActivity.class);
            startActivity(intent);
        });

        return view;
    }
}