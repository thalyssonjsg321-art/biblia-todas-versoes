package br.com.thalyssonjsg.biblia;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private LinearLayout content;
    private BottomNavigationView nav;
    private SharedPreferences prefs;
    private float fontSize = 19f;

    private final String[] versions = {
        "ARA — Almeida Revista e Atualizada",
        "ARC — Almeida Revista e Corrigida",
        "NVI — Nova Versão Internacional",
        "NTLH — Nova Tradução na Linguagem de Hoje",
        "ACF — Almeida Corrigida Fiel",
        "NAA — Nova Almeida Atualizada",
        "KJV — King James Version",
        "WEB — World English Bible"
    };

    // Texto demonstrativo. Para publicar uma versão completa, use arquivos/licenças
    // autorizados e coloque-os em assets/bibles/*.json.
    private final String demoText =
        "1 No princípio, Deus criou os céus e a terra.\\n\\n" +
        "2 A terra estava sem forma e vazia; havia trevas sobre a face do abismo, " +
        "e o Espírito de Deus pairava sobre as águas.\\n\\n" +
        "3 Disse Deus: Haja luz; e houve luz.\\n\\n" +
        "4 Deus viu que a luz era boa e separou a luz das trevas.\\n\\n" +
        "5 Deus chamou à luz Dia, e às trevas chamou Noite.\\n\\n" +
        "6 E assim terminou o primeiro capítulo da leitura demonstrativa.";

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("biblia", MODE_PRIVATE);
        content = findViewById(R.id.content);
        nav = findViewById(R.id.bottomNav);
        nav.inflateMenu(R.menu.bottom_menu);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) showHome();
            else if (id == R.id.nav_search) showSearch();
            else if (id == R.id.nav_favorites) showFavorites();
            else if (id == R.id.nav_settings) showSettings();
            return true;
        });
        showHome();
        nav.setSelectedItemId(R.id.nav_home);
    }

    private View load(int layout) {
        content.removeAllViews();
        return getLayoutInflater().inflate(layout, content, false);
    }

    private void showHome() {
        View v = load(R.layout.screen_home);
        content.addView(v);
        v.findViewById(R.id.btnRead).setOnClickListener(x -> { showReader(); });
        v.findViewById(R.id.btnSearch).setOnClickListener(x -> {
            nav.setSelectedItemId(R.id.nav_search);
        });
        v.findViewById(R.id.btnFavorites).setOnClickListener(x -> {
            nav.setSelectedItemId(R.id.nav_favorites);
        });
        v.findViewById(R.id.btnSettings).setOnClickListener(x -> {
            nav.setSelectedItemId(R.id.nav_settings);
        });
    }

    private void showReader() {
        View v = load(R.layout.screen_reader);
        content.addView(v);

        Spinner spinner = v.findViewById(R.id.versionSpinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, versions));
        spinner.setSelection(prefs.getInt("version", 0));
        TextView text = v.findViewById(R.id.readerText);
        text.setText(demoText);
        text.setTextSize(fontSize);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View x, int p, long id) {
                prefs.edit().putInt("version", p).apply();
            }
            public void onNothingSelected(AdapterView<?> a) {}
        });

        v.findViewById(R.id.zoomIn).setOnClickListener(x -> {
            fontSize = Math.min(42f, fontSize + 2f); text.setTextSize(fontSize);
        });
        v.findViewById(R.id.zoomOut).setOnClickListener(x -> {
            fontSize = Math.max(12f, fontSize - 2f); text.setTextSize(fontSize);
        });

        v.findViewById(R.id.highlight).setOnClickListener(x -> {
            SpannableString s = new SpannableString(text.getText());
            s.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, Math.min(80, s.length()),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setText(s);
            prefs.edit().putBoolean("highlighted", true).apply();
            Toast.makeText(this, "Texto marcado e salvo nesta sessão.", Toast.LENGTH_SHORT).show();
        });

        v.findViewById(R.id.favorite).setOnClickListener(x -> {
            saveFavorite("Gênesis 1 — " + spinner.getSelectedItem());
        });

        v.findViewById(R.id.list).setOnClickListener(x -> {
            saveFavorite("Lista: Pregação do dia 10/08/2026 — Gênesis 1");
        });

        v.findViewById(R.id.searchButton).setOnClickListener(x -> nav.setSelectedItemId(R.id.nav_search));
    }

    private void showSearch() {
        View v = load(R.layout.screen_search);
        content.addView(v);
        EditText query = v.findViewById(R.id.query);
        TextView results = v.findViewById(R.id.results);
        v.findViewById(R.id.doSearch).setOnClickListener(x -> {
            String q = query.getText().toString().trim();
            if (q.isEmpty()) {
                results.setText("Digite uma palavra ou referência.");
                return;
            }
            String lower = demoText.toLowerCase(Locale.ROOT);
            if (lower.contains(q.toLowerCase(Locale.ROOT))) {
                results.setText("Resultado encontrado:\\n\\nGênesis 1\\n" + demoText);
            } else {
                results.setText("Nenhum resultado no conteúdo demonstrativo.\\n\\n" +
                        "Para pesquisa completa, adicione os arquivos licenciados em assets/bibles.");
            }
        });
    }

    private void showFavorites() {
        View v = load(R.layout.screen_favorites);
        content.addView(v);
        TextView data = v.findViewById(R.id.favoriteData);
        data.setText(prefs.getString("favorites", "Nenhum favorito salvo ainda."));
        v.findViewById(R.id.newList).setOnClickListener(x -> {
            saveFavorite("📁 Pregação do dia 10/08/2026");
            data.setText(prefs.getString("favorites", ""));
        });
    }

    private void showSettings() {
        View v = load(R.layout.screen_settings);
        content.addView(v);
        Switch dark = v.findViewById(R.id.darkMode);
        dark.setChecked(prefs.getBoolean("dark", false));
        dark.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("dark", checked).apply();
            AppCompatDelegateWrapper.apply(this, checked);
        });

        Spinner spinner = v.findViewById(R.id.defaultVersion);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, versions));
        spinner.setSelection(prefs.getInt("version", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View x, int p, long id) {
                prefs.edit().putInt("version", p).apply();
            }
            public void onNothingSelected(AdapterView<?> a) {}
        });
    }

    private void saveFavorite(String item) {
        String old = prefs.getString("favorites", "");
        String value = old.isEmpty() ? "★ " + item : old + "\\n\\n★ " + item;
        prefs.edit().putString("favorites", value).apply();
        Toast.makeText(this, "Salvo nos favoritos.", Toast.LENGTH_SHORT).show();
    }

    public static class AppCompatDelegateWrapper {
        static void apply(Context c, boolean dark) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                dark ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                     : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
