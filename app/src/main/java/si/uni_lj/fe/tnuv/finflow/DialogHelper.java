package si.uni_lj.fe.tnuv.finflow;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogHelper {

    public interface OnPotrdi {
        void run();
    }

    public static void prikaziPotrditev(Context context, String naslov, String sporocilo, int barvaGumba, OnPotrdi onPotrdi) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_potrditev, null);

        TextView tvNaslov = dialogView.findViewById(R.id.tv_potrditev_naslov);
        TextView tvSporocilo = dialogView.findViewById(R.id.tv_potrditev_sporocilo);
        Button btnPreklici = dialogView.findViewById(R.id.btn_potrditev_preklici);
        Button btnPotrdi = dialogView.findViewById(R.id.btn_potrditev_potrdi);

        tvNaslov.setText(naslov);
        tvSporocilo.setText(sporocilo);
        btnPotrdi.setBackgroundTintList(context.getColorStateList(barvaGumba));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnPreklici.setOnClickListener(v -> dialog.dismiss());
        btnPotrdi.setOnClickListener(v -> {
            onPotrdi.run();
            dialog.dismiss();
        });

        dialog.show();
    }
}