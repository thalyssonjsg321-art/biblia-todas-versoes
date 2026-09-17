package br.com.thalyssonjsg.biblia;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout content;
    private LinearLayout bottomBar;
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

    // Conteúdo demonstrativo. Textos completos devem ser adicionados somente com licença/autorização.
    private final String demoText =
        "1 No princípio, Deus criou os céus e a terra.\n\n" +
        "2 A terra estava sem forma e vazia; havia trevas sobre a face do abismo, " +
        "e o Espírito de Deus pairava sobre as águas.\n\n" +
        "3 Disse Deus: Haja luz; e houve luz.\n\n" +
        "4 Deus viu que a luz era boa e separou a luz das trevas.\n\n" +
        "5 Deus chamou à luz Dia, e às trevas chamou Noite.\n\n" +
        "6 E assim terminou o primeiro capítulo da leitura demonstrativa.";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("biblia", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        content = findViewById(R.id.content);
        bottomBar = findViewById(R.id.bottomBar);
        buildBottomBar();
        showHome();
    }

    private void applyTheme(boolean recreate) {
        if (recreate && !isFinishing()) recreate();
    }

    private void buildBottomBar() {
        addNavButton("⌂\nInício", v -> showHome());
        addNavButton("⌕\nPesquisar", v -> showSearch());
        addNavButton("★\nFavoritos", v -> showFavorites());
        addNavButton("⚙\nConfig.", v -> showSettings());
    }

    private void addNavButton(String label, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(11f);
        b.setAllCaps(false);
        b.setOnClickListener(listener);
        b.setGravity(Gravity.CENTER);
        b.setPadding(2, 0, 2, 0);
        b.setBackground(new ColorDrawable(Color.TRANSPARENT));
        bottomBar.addView(b, new LinearLayout.LayoutParams(0, -1, 1f));
    }

    private View load(int layout) {
        content.removeAllViews();
        return getLayoutInflater().inflate(layout, content, false);
    }

    private void showHome() {
        View v = load(R.layout.screen_home);
        content.addView(v);
        v.findViewById(R.id.btnRead).setOnClickListener(x -> showReader());
        v.findViewById(R.id.btnSearch).setOnClickListener(x -> showSearch());
        v.findViewById(R.id.btnFavorites).setOnClickListener(x -> showFavorites());
        v.findViewById(R.id.btnSettings).setOnClickListener(x -> showSettings());
    }

    private void showReader() {
        View v = load(R.layout.screen_reader);
        content.addView(v);
        Spinner spinner = v.findViewById(R.id.versionSpinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, versions));
        spinner.setSelection(Math.min(prefs.getInt("version", 0), versions.length - 1));
        TextView text = v.findViewById(R.id.readerText);
        text.setText(demoText);
        text.setTextSize(fontSize);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View x, int p, long id) { prefs.edit().putInt("version", p).apply(); }
            public void onNothingSelected(AdapterView<?> a) { }
        });
        v.findViewById(R.id.zoomIn).setOnClickListener(x -> { fontSize = Math.min(42f, fontSize + 2f); text.setTextSize(fontSize); });
        v.findViewById(R.id.zoomOut).setOnClickListener(x -> { fontSize = Math.max(12f, fontSize - 2f); text.setTextSize(fontSize); });
        v.findViewById(R.id.highlight).setOnClickListener(x -> {
            SpannableString s = new SpannableString(text.getText());
            s.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, Math.min(80, s.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setText(s);
            Toast.makeText(this, "Texto marcado.", Toast.LENGTH_SHORT).show();
        });
        v.findViewById(R.id.favorite).setOnClickListener(x -> saveFavorite("Gênesis 1 — " + spinner.getSelectedItem()));
        v.findViewById(R.id.list).setOnClickListener(x -> saveFavorite("Lista: Pregação do dia 10/08/2026 — Gênesis 1"));
        v.findViewById(R.id.searchButton).setOnClickListener(x -> showSearch());
    }

    private void showSearch() {
        View v = load(R.layout.screen_search);
        content.addView(v);
        EditText query = v.findViewById(R.id.query);
        TextView results = v.findViewById(R.id.results);
        v.findViewById(R.id.doSearch).setOnClickListener(x -> {
            String q = query.getText().toString().trim();
            if (q.isEmpty()) { results.setText("Digite uma palavra ou referência."); return; }
            if (demoText.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT)))
                results.setText("Resultado encontrado:\n\nGênesis 1\n" + demoText);
            else
                results.setText("Nenhum resultado no conteúdo demonstrativo.\n\nPara pesquisa completa, adicione textos licenciados em assets/bibles.");
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
            applyTheme(true);
        });
        Spinner spinner = v.findViewById(R.id.defaultVersion);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, versions));
        spinner.setSelection(Math.min(prefs.getInt("version", 0), versions.length - 1));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View x, int p, long id) { prefs.edit().putInt("version", p).apply(); }
            public void onNothingSelected(AdapterView<?> a) { }
        });
    }

    private void saveFavorite(String item) {
        String old = prefs.getString("favorites", "");
        String value = old.isEmpty() ? "★ " + item : old + "\n\n★ " + item;
        prefs.edit().putString("favorites", value).apply();
        Toast.makeText(this, "Salvo nos favoritos.", Toast.LENGTH_SHORT).show();
    }
}
