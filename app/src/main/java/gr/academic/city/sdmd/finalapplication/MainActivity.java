package gr.academic.city.sdmd.finalapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import gr.academic.city.sdmd.finalapplication.databinding.ActivityMainBinding;
import gr.academic.city.sdmd.finalapplication.domain.Student;
import gr.academic.city.sdmd.finalapplication.domain.Students;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StudentsApi studentsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StudentsApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        studentsApi = retrofit.create(StudentsApi.class);

        binding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateStudentClicked();
            }
        });

        binding.btnInsert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onCreateStudentLocallyClicked();
            }
        });

        binding.btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetStudentsClicked();
            }
        });

        binding.btnQuery.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onGetStudentsLocallyClicked();
            }
        });
    }

    private void onCreateStudentClicked() {
        String firstName = binding.txtFirstName.getText().toString();
        String lastName = binding.txtLastName.getText().toString();
        String age = binding.txtLastName.getText().toString();

        studentsApi.createStudent(new Student(firstName, lastName, age))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showToast(R.string.msg_student_created);
                        } else {
                            showToast(R.string.msg_student_not_created);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast(R.string.msg_server_error);
                    }
                });
    }

    private boolean onCreateStudentLocallyClicked() {
        String firstName = binding.txtFirstName.getText().toString();
        String lastName = binding.txtLastName.getText().toString();
        String age = binding.txtLastName.getText().toString();

        SQLiteOpenHelper helper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // THIS IS EXECUTED ON THE MAIN THREAD NOW, JUST FOR DEMO PURPOSES, DON'T DO THIS AT HOME
        long id = cupboard().withDatabase(db).put(new Student(firstName, lastName, age));

        showToast(getString(R.string.msg_student_created_with_id, id));

        db.close();
        helper.close();

        return true;
    }

    private void onGetStudentsClicked() {
        studentsApi.getAllStudents()
                .enqueue(new Callback<Students>() {
                    @Override
                    public void onResponse(Call<Students> call, Response<Students> response) {
                        if (response.isSuccessful()) {
                            List<Student> students = response.body().items;
                            showStudents(students);
                        } else {
                            showToast(R.string.msg_students_not_fetched);
                        }
                    }

                    @Override
                    public void onFailure(Call<Students> call, Throwable t) {
                        Log.e("APP", t.getMessage());
                        showToast(R.string.msg_server_error);
                    }
                });
    }

    private boolean onGetStudentsLocallyClicked() {
        SQLiteOpenHelper helper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // THIS IS EXECUTED ON THE MAIN THREAD NOW, JUST FOR DEMO PURPOSES, DON'T DO THIS AT HOME
        List<Student> students = cupboard().withDatabase(db).query(Student.class).list();
        showStudents(students);

        db.close();
        helper.close();

        return true;
    }

    private void showStudents(List<Student> students) {
        binding.lvResults.setAdapter(new ArrayAdapter<Student>(MainActivity.this, android.R.layout.simple_list_item_1, students));
    }

    private void showToast(int msgString) {
        Toast.makeText(this, msgString, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
