package gr.academic.city.sdmd.finalapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import gr.academic.city.sdmd.finalapplication.domain.Student;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.lv_results)
    ListView lvResults;

    @Bind(R.id.txt_first_name)
    EditText txtFirstName;

    @Bind(R.id.txt_last_name)
    EditText txtLastName;

    @Bind(R.id.txt_age)
    EditText txtAge;

    private StudentsApi studentsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StudentsApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        studentsApi = retrofit.create(StudentsApi.class);
    }

    @OnClick(R.id.btn_insert)
    public void onCreateStudentClicked() {
        String firstName = txtFirstName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String age = txtLastName.getText().toString();

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

    @OnLongClick(R.id.btn_insert)
    public boolean onCreateStudentLocallyClicked() {
        String firstName = txtFirstName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String age = txtLastName.getText().toString();

        SQLiteOpenHelper helper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // THIS IS EXECUTED ON THE MAIN THREAD NOW, JUST FOR DEMO PURPOSES, DON'T DO THIS AT HOME
        long id = cupboard().withDatabase(db).put(new Student(firstName, lastName, age));

        showToast(getString(R.string.msg_student_created_with_id, id));

        db.close();
        helper.close();

        return true;
    }

    @OnClick(R.id.btn_query)
    public void onGetStudentsClicked() {
        studentsApi.getAllStudents()
                .enqueue(new Callback<List<Student>>() {
                    @Override
                    public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                        if (response.isSuccessful()) {
                            List<Student> students = response.body();
                            showStudents(students);
                        } else {
                            showToast(R.string.msg_students_not_fetched);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Student>> call, Throwable t) {
                        Log.e("APP", t.getMessage());
                        showToast(R.string.msg_server_error);
                    }
                });
    }

    @OnLongClick(R.id.btn_query)
    public boolean onGetStudentsLocallyClicked() {
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
        lvResults.setAdapter(new ArrayAdapter<Student>(MainActivity.this, android.R.layout.simple_list_item_1, students));
    }

    private void showToast(int msgString) {
        Toast.makeText(this, msgString, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
