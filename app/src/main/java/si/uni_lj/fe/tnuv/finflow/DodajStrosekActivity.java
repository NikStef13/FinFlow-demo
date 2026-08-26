package si.uni_lj.fe.tnuv.finflow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DodajStrosekActivity extends AppCompatActivity {

    String izbranKategorija = "";
    int strosekId = -1;
    long obstojeciDatum = 0;

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

        Button[] gumbiKategorij = {btnHrana, btnKava, btnPrevoz, btnStanovanje, btnTehnologija, btnDrugo};

        View.OnClickListener kategorija = v -> {
            for (Button b : gumbiKategorij) {
                b.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
                b.setTextColor(getColor(R.color.besediloTemno));
            }
            ((Button) v).setBackgroundTintList(getColorStateList(R.color.temnoModra));
            ((Button) v).setTextColor(getColor(R.color.bela));
            izbranKategorija = ((Button) v).getText().toString();
        };

        for (Button b : gumbiKategorij) {
            b.setOnClickListener(kategorija);
        }

        strosekId = getIntent().getIntExtra("strosek_id", -1);
        if (strosekId != -1) {
            btnDodaj.setText("Shrani spremembe");
            AppDatabase db = AppDatabase.getDatabase(this);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Strosek obstojeci = db.dao().getStrosekById(strosekId);
                obstojeciDatum = obstojeci.datum;
                runOnUiThread(() -> {
                    etZnesek.setText(String.valueOf(obstojeci.znesek));
                    etOpis.setText(obstojeci.opis);
                    for (Button b : gumbiKategorij) {
                        if (b.getText().toString().equals(obstojeci.kategorija)) {
                            b.performClick();
                        }
                    }
                });
            });
        }

        btnDodaj.setOnClickListener(v -> {
            String znesekStr = etZnesek.getText().toString();
            String opis = etOpis.getText().toString();

            if (znesekStr.isEmpty()) {
                Toast.makeText(this, "Vnesite znesek!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (izbranKategorija.isEmpty()) {
                Toast.makeText(this, "Izberite kategorijo!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double znesek = Double.parseDouble(znesekStr);
                AppDatabase db = AppDatabase.getDatabase(this);

                if (strosekId != -1) {
                    Strosek posodobljen = new Strosek(znesek, opis, izbranKategorija, obstojeciDatum);
                    posodobljen.id = strosekId;
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().updateStrosek(posodobljen);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Strošek posodobljen!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                } else {
                    long datum = System.currentTimeMillis();
                    Strosek novStrosek = new Strosek(znesek, opis, izbranKategorija, datum);
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().insertStrosek(novStrosek);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Strošek uspešno dodan!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vnesite veljavno številko!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPreklici.setOnClickListener(v -> finish());
    }
}