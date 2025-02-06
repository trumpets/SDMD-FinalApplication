package gr.academic.city.sdmd.finalapplication;

import gr.academic.city.sdmd.finalapplication.domain.Student;
import gr.academic.city.sdmd.finalapplication.domain.Students;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by trumpets on 5/19/16.
 */
public interface StudentsApi {

    static final String BASE_URL = "https://city-201617.appspot.com/_ah/api/students/v1/";
    static final String STUDENTS_URL = "student";

    @GET(STUDENTS_URL)
    Call<Students> getAllStudents();

    @POST(STUDENTS_URL)
    Call<Void> createStudent(@Body Student student);
}