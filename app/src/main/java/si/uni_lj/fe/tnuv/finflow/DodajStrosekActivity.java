package si.uni_lj.fe.tnuv.finflow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DodajStrosekActivity extends AppCompatActivity {

    String izbranKategorija = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_strosek);

        EditText etZnesek = findViewById(R.id.et_znesek);
        EditText etOpis = findViewById(R.id.et_opis);

        Button btnHrana = findViewById(R.id.btn_hrana);
        Button btnKava = findViewById(R.id.btn_kava);
        Button btnPrevoz = findViewById(R.id.btn_prevoz);
        Button btnStanovanje = findViewById(R.id.btn_stanovanje);
        Button btnTehnologija = findViewById(R.id.btn_tehnologija);
        Button btnDrugo = findViewById(R.id.btn_drugo);

        Button btnDodaj = findViewById(R.id.btn_dodaj);
        Button btnPreklici = findViewById(R.id.btn_preklici);

        // Poslušalci za kategorije
        View.OnClickListener kategorija = v -> {
            // Resetiraj vse gumbe
            btnHrana.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnKava.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnPrevoz.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnStanovanje.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnTehnologija.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnDrugo.setBackgroundTintList(getColorStateList(R.color.sivaKartica));

            // Resetiraj barvo teksta vseh gumbov
            btnHrana.setTextColor(getColor(R.color.besediloTemno));
            btnKava.setTextColor(getColor(R.color.besediloTemno));
            btnPrevoz.setTextColor(getColor(R.color.besediloTemno));
            btnStanovanje.setTextColor(getColor(R.color.besediloTemno));
            btnTehnologija.setTextColor(getColor(R.color.besediloTemno));
            btnDrugo.setTextColor(getColor(R.color.besediloTemno));

            // Označi izbrani gumb
            ((Button) v).setBackgroundTintList(getColorStateList(R.color.zelena));
            ((Button) v).setTextColor(getColor(R.color.bela));
            izbranKategorija = ((Button) v).getText().toString();
        };

        btnHrana.setOnClickListener(kategorija);
        btnKava.setOnClickListener(kategorija);
        btnPrevoz.setOnClickListener(kategorija);
        btnStanovanje.setOnClickListener(kategorija);
        btnTehnologija.setOnClickListener(kategorija);
        btnDrugo.setOnClickListener(kategorija);

        // Dodaj strošek
        btnDodaj.setOnClickListener(v -> {
            String znesek = etZnesek.getText().toString();

            if (znesek.isEmpty()) {
                Toast.makeText(this, "Vnesite znesek!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (izbranKategorija.isEmpty()) {
                Toast.makeText(this, "Izberite kategorijo!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Zaenkrat samo Toast — kasneje povežemo z bazo
            Toast.makeText(this, "Strošek dodan: €" + znesek + " (" + izbranKategorija + ")", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Prekliči
        btnPreklici.setOnClickListener(v -> finish());
    }
}