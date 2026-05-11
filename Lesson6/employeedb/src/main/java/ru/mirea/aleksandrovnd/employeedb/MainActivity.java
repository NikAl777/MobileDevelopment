package ru.mirea.aleksandrovnd.employeedb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewHeroes;
    private Button buttonAddHeroes;
    private Button buttonShowHeroes;
    private Button buttonClearHeroes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHeroes = findViewById(R.id.textViewHeroes);
        buttonAddHeroes = findViewById(R.id.buttonAddHeroes);
        buttonShowHeroes = findViewById(R.id.buttonShowHeroes);
        buttonClearHeroes = findViewById(R.id.buttonClearHeroes);

        AppDatabase db = App.getInstance().getDatabase();
        HeroDao heroDao = db.heroDao();

        // ➤ Добавить героев
        buttonAddHeroes.setOnClickListener(v -> {
            if (heroDao.getAll().isEmpty()) {

                Hero hero1 = new Hero();
                hero1.name = "Shadow Fox";
                hero1.power = "Invisible movement";
                hero1.dangerLevel = 7;
                heroDao.insert(hero1);

                Hero hero2 = new Hero();
                hero2.name = "Steel Ray";
                hero2.power = "Energy shield";
                hero2.dangerLevel = 9;
                heroDao.insert(hero2);

                Hero hero3 = new Hero();
                hero3.name = "Storm Pulse";
                hero3.power = "Electric storm";
                hero3.dangerLevel = 10;
                heroDao.insert(hero3);

                textViewHeroes.setText("Супер-герои добавлены");
            } else {
                textViewHeroes.setText("Герои уже есть в базе");
            }
        });

        // ➤ Показать всех
        buttonShowHeroes.setOnClickListener(v -> {
            List<Hero> heroes = heroDao.getAll();

            if (heroes.isEmpty()) {
                textViewHeroes.setText("База пустая");
                return;
            }

            StringBuilder builder = new StringBuilder();

            for (Hero hero : heroes) {
                builder.append("ID: ").append(hero.id).append("\n");
                builder.append("Имя: ").append(hero.name).append("\n");
                builder.append("Способность: ").append(hero.power).append("\n");
                builder.append("Уровень опасности: ").append(hero.dangerLevel).append("\n\n");
            }

            textViewHeroes.setText(builder.toString());
        });

        // ➤ Очистить базу
        buttonClearHeroes.setOnClickListener(v -> {
            heroDao.deleteAll();
            textViewHeroes.setText("База данных очищена");
        });
    }
}