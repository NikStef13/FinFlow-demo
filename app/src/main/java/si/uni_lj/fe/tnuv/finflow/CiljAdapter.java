package si.uni_lj.fe.tnuv.finflow;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CiljAdapter extends ArrayAdapter<Cilj> {

    public interface Listener {
        void onUredi(Cilj cilj);
        void onIzbrisi(Cilj cilj);
        void onSpremenjenoPrihranjeno(Cilj cilj, double noviZnesek);
    }

    private final Listener listener;

    public CiljAdapter(Context context, List<Cilj> cilji, Listener listener) {
        super(context, 0, cilji);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cilj, parent, false);
        }

        Cilj cilj = getItem(position);

        TextView tvNaslov = convertView.findViewById(R.id.tv_cilj_naslov);
        TextView tvKategorija = convertView.findViewById(R.id.tv_cilj_kategorija);
        TextView tvZnesek = convertView.findViewById(R.id.tv_cilj_znesek);
        TextView tvPreostalo = convertView.findViewById(R.id.tv_cilj_preostalo);
        ProgressBar pbCilj = convertView.findViewById(R.id.pb_cilj);
        ImageButton btnMore = convertView.findViewById(R.id.btn_more);
        Button btnDodaj = convertView.findViewById(R.id.btn_cilj_dodaj);
        Button btnOdbij = convertView.findViewById(R.id.btn_cilj_odbij);

        TextView tvOdstotek = convertView.findViewById(R.id.tv_cilj_odstotek);

        tvNaslov.setText(cilj.ime);
        tvKategorija.setText(cilj.ikona);
        tvZnesek.setText(String.format("€%.2f / €%.2f", cilj.prihranjeno, cilj.ciljniZnesek));

        int odstotek = cilj.ciljniZnesek > 0
                ? (int) Math.min(100, (cilj.prihranjeno / cilj.ciljniZnesek) * 100)
                : 0;
        pbCilj.setProgress(odstotek);
        tvOdstotek.setText(odstotek + "%");

        double preostalo = cilj.ciljniZnesek - cilj.prihranjeno;
        if (preostalo <= 0) {
            tvPreostalo.setText(getContext().getString(R.string.cilji_dosezen));
        } else {
            tvPreostalo.setText(getContext().getString(R.string.cilji_manjka, String.format("€%.2f", preostalo)));
        }

        btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnMore);
            popup.getMenuInflater().inflate(R.menu.menu_item_actions, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    listener.onUredi(cilj);
                    return true;
                } else if (id == R.id.action_delete) {
                    listener.onIzbrisi(cilj);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        btnDodaj.setOnClickListener(v -> prikaziDialogZnesek(cilj, true));
        btnOdbij.setOnClickListener(v -> prikaziDialogZnesek(cilj, false));

        return convertView;
    }

    private void prikaziDialogZnesek(Cilj cilj, boolean dodajanje) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_znesek, null);

        TextView tvNaslov = dialogView.findViewById(R.id.tv_dialog_naslov);
        EditText etZnesek = dialogView.findViewById(R.id.et_dialog_znesek);
        Button btnPreklici = dialogView.findViewById(R.id.btn_dialog_preklici);
        Button btnPotrdi = dialogView.findViewById(R.id.btn_dialog_potrdi);

        tvNaslov.setText(dodajanje ? getContext().getString(R.string.cilj_dialog_dodaj_naslov) : getContext().getString(R.string.cilj_dialog_odbij_naslov));
        btnPotrdi.setText(dodajanje ? getContext().getString(R.string.cilj_dialog_gumb_dodaj) : getContext().getString(R.string.cilj_dialog_gumb_odbij));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnPreklici.setOnClickListener(v -> dialog.dismiss());
        btnPotrdi.setOnClickListener(v -> {
            String vnos = etZnesek.getText().toString();
            if (vnos.isEmpty()) {
                dialog.dismiss();
                return;
            }
            double znesek = Double.parseDouble(vnos);
            double noviZnesek = dodajanje
                    ? cilj.prihranjeno + znesek
                    : Math.max(0, cilj.prihranjeno - znesek);
            listener.onSpremenjenoPrihranjeno(cilj, noviZnesek);
            dialog.dismiss();
        });

        dialog.show();
    }
}