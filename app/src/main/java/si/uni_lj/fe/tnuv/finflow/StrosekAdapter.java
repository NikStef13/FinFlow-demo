package si.uni_lj.fe.tnuv.finflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class StrosekAdapter extends ArrayAdapter<Strosek> {

    public interface Listener {
        void onUredi(Strosek strosek);
        void onIzbrisi(Strosek strosek);
    }

    private final Listener listener;

    public StrosekAdapter(Context context, List<Strosek> stroski, Listener listener) {
        super(context, 0, stroski);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_strosek, parent, false);
        }

        Strosek strosek = getItem(position);

        TextView tvStrosek = convertView.findViewById(R.id.tv_strosek);
        ImageButton btnMore = convertView.findViewById(R.id.btn_more);

        tvStrosek.setText(strosek.kategorija + ": €" + strosek.znesek + (strosek.opis.isEmpty() ? "" : " - " + strosek.opis));

        btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnMore);
            popup.getMenuInflater().inflate(R.menu.menu_item_actions, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    listener.onUredi(strosek);
                    return true;
                } else if (id == R.id.action_delete) {
                    listener.onIzbrisi(strosek);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        return convertView;
    }
}
