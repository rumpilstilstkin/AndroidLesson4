package com.example.rumpilstilstkin.lesson4;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class MainStorageActivity extends AppCompatActivity {

    private EditText editText;
    private final static String FILENAME = "savedText.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_storage);
        editText = (EditText) findViewById(R.id.editText);

        Button clearButton = (Button) findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(clearListener);

        Button savePrivateButton = (Button) findViewById(R.id.buttonSavePrivate);
        savePrivateButton.setOnClickListener(savePrivateListener);

        Button savePublicButton = (Button) findViewById(R.id.buttonSavePublic);
        savePublicButton.setOnClickListener(savePublicListener);

        Button loadPrivateButton = (Button) findViewById(R.id.buttonLoadPrivate);
        loadPrivateButton.setOnClickListener(loadPrivateListener);

        Button loadPublicButton = (Button) findViewById(R.id.buttonLoadPublic);
        loadPublicButton.setOnClickListener(loadPublicListener);

        Button deletePrivateFlieButton = (Button) findViewById(R.id.buttonPrivate);
        deletePrivateFlieButton.setOnClickListener(deletePrivateFileListener);

        Button deletePublicFileButton = (Button) findViewById(R.id.buttonPublic);
        deletePublicFileButton.setOnClickListener(deletePublicFileListener);
    }

    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editText.setText("");
        }
    };

    private View.OnClickListener savePrivateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File dir = new File(getApplicationContext().getFilesDir().getPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, FILENAME);
            onSaveText(file);
        }
    };

    private View.OnClickListener savePublicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILENAME);
            onSaveText(file);
        }
    };

    private View.OnClickListener loadPrivateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getFilesDir(), FILENAME);
            onLoadText(file);
        }
    };

    private View.OnClickListener loadPublicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILENAME);
            onLoadText(file);
        }
    };

    private View.OnClickListener deletePrivateFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getFilesDir(), FILENAME);
            onDeleteFile(file);
        }
    };

    private View.OnClickListener deletePublicFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILENAME);
            onDeleteFile(file);
        }
    };

    private void onSaveText(File file) {
        if (!isExternalStorageWritable()) {
            showToast(getText(R.string.toast_external_storage_not_found));
            return;
        }

        try {
            if (!editText.getText().toString().isEmpty()) {
                // Если по какой-то причине вы создадите папку с таким же именем как файл
                // как оказалось это возможно, то просто удалите эту папку
                // иначе будет ошибка java.io.FileNotFoundException: open failed: EISDIR (Is a directory)
                FileOutputStream outputStream = new FileOutputStream(file, false);
                outputStream.write(editText.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();

            }
        } catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    public void onLoadText(File file) {
        if (!isExternalStorageReadable()) {
            showToast(getText(R.string.toast_external_storage_not_found));
            return;
        }
        try {
            StringBuilder text = new StringBuilder();
            if(!file.exists()) {
                showToast(getText(R.string.toast_file_not_exist));
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            // есть текст в br считываем его построчно
            while ((line = br.readLine()) != null) {
                // добавляем считанное в StringBuilder
                text.append(line);
                // перенос на новую строку не считывается поэтому добавляем его
                text.append('\n');
            }
            // последний добавленный перенос лишний, убираем его
            text.deleteCharAt(text.length() - 1);
            br.close();
            editText.setText(text);
        }
        catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    public void onDeleteFile(File file) {
        if (file.exists()) {
            file.delete();
            showToast(getText(R.string.toast_file_deleted));
        }
        else {
            showToast(getText(R.string.toast_file_not_exist));
        }
        editText.setText("");
    }

    // проверим доступен ли external storage для записи/чтения
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // проверим доступен ли external storage для чтения
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void showToast(CharSequence toastMessage) {
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
