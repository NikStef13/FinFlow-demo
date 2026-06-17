package si.uni_lj.fe.tnuv.finflow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DodajCiljActivity extends AppCompatActivity {

    String izbranIkona = "";

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

        // Poslušalci za ikone
        View.OnClickListener ikona = v -> {
            // Resetiraj vse gumbe
            btnIkonaPotovanje.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnIkonaTelefon.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnIkonaAvto.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnIkonaStanovanje.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnIkonaIzobrazba.setBackgroundTintList(getColorStateList(R.color.sivaKartica));
            btnIkonaDrugo.setBackgroundTintList(getColorStateList(R.color.sivaKartica));

            btnIkonaPotovanje.setTextColor(getColor(R.color.besediloTemno));
            btnIkonaTelefon.setTextColor(getColor(R.color.besediloTemno));
            btnIkonaAvto.setTextColor(getColor(R.color.besediloTemno));
            btnIkonaStanovanje.setTextColor(getColor(R.color.besediloTemno));
            btnIkonaIzobrazba.setTextColor(getColor(R.color.besediloTemno));
            btnIkonaDrugo.setTextColor(getColor(R.color.besediloTemno));

            // Označi izbrani gumb
            ((Button) v).setBackgroundTintList(getColorStateList(R.color.zelena));
            ((Button) v).setTextColor(getColor(R.color.bela));
            izbranIkona = ((Button) v).getText().toString();
        };

        btnIkonaPotovanje.setOnClickListener(ikona);
        btnIkonaTelefon.setOnClickListener(ikona);
        btnIkonaAvto.setOnClickListener(ikona);
        btnIkonaStanovanje.setOnClickListener(ikona);
        btnIkonaIzobrazba.setOnClickListener(ikona);
        btnIkonaDrugo.setOnClickListener(ikona);


        // Dodaj cilj
        btnDodajCilj.setOnClickListener(v -> {
            String ime = etImeCilja.getText().toString();
            String znesekStr = etCiljniZnesek.getText().toString();
            String prihranjenoStr = etPrihranjeno.getText().toString();
            String rok = etRok.getText().toString();

            if (ime.isEmpty()) {
                Toast.makeText(this, "Vnesite ime cilja!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (znesekStr.isEmpty()) {
                Toast.makeText(this, "Vnesite ciljni znesek!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (izbranIkona.isEmpty()) {
                Toast.makeText(this, "Izberite ikono!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double znesek = Double.parseDouble(znesekStr);
                double prihranjeno = prihranjenoStr.isEmpty() ? 0 : Double.parseDouble(prihranjenoStr);

                Cilj novCilj = new Cilj(ime, znesek, prihranjeno, rok, izbranIkona);

                AppDatabase db = AppDatabase.getDatabase(this);
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    db.dao().insertCilj(novCilj);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cilj uspešno dodan!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vnesite veljavno številko!", Toast.LENGTH_SHORT).show();
            }
        });

        // Prekliči
        btnPreklici.setOnClickListener(v -> finish());
    }
}