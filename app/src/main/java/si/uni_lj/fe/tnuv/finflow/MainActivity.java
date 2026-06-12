package si.uni_lj.fe.tnuv.finflow;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ob zagonu prikaži PorabaFragment
        zamenjajFragment(new PorabaFragment());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_poraba);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_poraba) {
                zamenjajFragment(new PorabaFragment());
            } else if (id == R.id.nav_proracun) {
                zamenjajFragment(new ProracunFragment());
            } else if (id == R.id.nav_cilji) {
                zamenjajFragment(new CiljiFragment());
            }
            return true;
        });
    }

    private void zamenjajFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.vsebina, fragment);
        ft.commit();
    }
}