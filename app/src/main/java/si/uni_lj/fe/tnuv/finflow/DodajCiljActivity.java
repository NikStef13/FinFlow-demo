package si.uni_lj.fe.tnuv.finflow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DodajCiljActivity extends AppCompatActivity {

    String izbranIkona = "";
    int ciljId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_cilj);

        EditText etImeCilja = findViewById(R.id.et_ime_cilja);
        EditText etCiljniZnesek = findViewById(R.id.et_ciljni_znesek);
        EditText etPrihranjeno = findViewById(R.id.et_prihranjeno);
        EditText etRok = findViewById(R.id.et_rok);

        Button btnIkonaPotovanje = findViewById(R.id.btn_ikona_potovanje);
        Button btnIkonaTelefon = findViewById(R.id.btn_ikona_telefon);
        Button btnIkonaAvto = findViewById(R.id.btn_ikona_avto);
        Button btnIkonaStanovanje = findViewById(R.id.btn_ikona_stanovanje);
        Button btnIkonaIzobrazba = findViewById(R.id.btn_ikona_izobrazba);
        Button btnIkonaDrugo = findViewById(R.id.btn_ikona_drugo);

        Button btnDodajCilj = findViewById(R.id.btn_dodaj_cilj);
        Button btnPreklici = findViewById(R.id.btn_preklici);

        Button[] gumbiIkon = {btnIkonaPotovanje, btnIkonaTelefon, btnIkonaAvto,
                btnIkonaStanovanje, btnIkonaIzobrazba, btnIkonaDrugo};

        View.OnClickListener ikona = v -> {
            for (Button b : gumbiIkon) {
                b.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
                b.setTextColor(getColor(R.color.besediloTemno));
            }
            ((Button) v).setBackgroundTintList(getColorStateList(R.color.zelena));
            ((Button) v).setTextColor(getColor(R.color.bela));
            izbranIkona = ((Button) v).getText().toString();
        };

        for (Button b : gumbiIkon) {
            b.setOnClickListener(ikona);
        }

        ciljId = getIntent().getIntExtra("cilj_id", -1);
        if (ciljId != -1) {
            btnDodajCilj.setText(R.string.dodaj_strosek_gumb_shrani);
            AppDatabase db = AppDatabase.getDatabase(this);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Cilj obstojeci = db.dao().getCiljById(ciljId);
                runOnUiThread(() -> {
                    etImeCilja.setText(obstojeci.ime);
                    etCiljniZnesek.setText(String.valueOf(obstojeci.ciljniZnesek));
                    etPrihranjeno.setText(String.valueOf(obstojeci.prihranjeno));
                    etRok.setText(obstojeci.rok);
                    for (Button b : gumbiIkon) {
                        if (b.getText().toString().equals(obstojeci.ikona)) {
                            b.performClick();
                        }
                    }
                });
            });
        }

        btnDodajCilj.setOnClickListener(v -> {
            String ime = etImeCilja.getText().toString();
            String znesekStr = etCiljniZnesek.getText().toString();
            String prihranjenoStr = etPrihranjeno.getText().toString();
            String rok = etRok.getText().toString();

            if (ime.isEmpty()) {
                Toast.makeText(this, getString(R.string.dodaj_cilj_napaka_ime), Toast.LENGTH_SHORT).show();
                return;
            }
            if (znesekStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.dodaj_cilj_napaka_znesek), Toast.LENGTH_SHORT).show();
                return;
            }
            if (izbranIkona.isEmpty()) {
                Toast.makeText(this, getString(R.string.dodaj_cilj_napaka_ikona), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double znesek = Double.parseDouble(znesekStr);
                double prihranjeno = prihranjenoStr.isEmpty() ? 0 : Double.parseDouble(prihranjenoStr);
                AppDatabase db = AppDatabase.getDatabase(this);

                if (ciljId != -1) {
                    Cilj posodobljen = new Cilj(ime, znesek, prihranjeno, rok, izbranIkona);
                    posodobljen.id = ciljId;
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().updateCilj(posodobljen);
                        runOnUiThread(() -> {
                            Toast.makeText(this, getString(R.string.dodaj_cilj_uspeh_posodobljen), Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                } else {
                    Cilj novCilj = new Cilj(ime, znesek, prihranjeno, rok, izbranIkona);
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.dao().insertCilj(novCilj);
                        runOnUiThread(() -> {
                            Toast.makeText(this, getString(R.string.dodaj_cilj_uspeh_dodan), Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.dodaj_strosek_napaka_stevilka), Toast.LENGTH_SHORT).show();
            }
        });

        btnPreklici.setOnClickListener(v -> finish());
    }
}