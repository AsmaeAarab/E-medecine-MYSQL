package com.example.e_medecine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.e_medecine.ApiRest.Apis;
import com.example.e_medecine.ApiRest.PatientService;
import com.example.e_medecine.model.Patient;
import com.example.e_medecine.model.Users;
import com.example.e_medecine.model.Ville;
import com.example.e_medecine.sqliteBd.GlobalDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientSignupActivity extends AppCompatActivity {

    GlobalDbHelper db;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @BindView(R.id.imageProfil)
    ImageView imgpro;
    @BindView(R.id.genre)
    Spinner genreSpinner;
    @BindView(R.id.assurance)
    Spinner assuranceSpinner;
    @BindView(R.id.ville)
    Spinner villeSpinner;

    @BindView(R.id.prenom)
    EditText editTextPrenom;
    @BindView(R.id.nom)
    EditText editTextNom;
    @BindView(R.id.email)
    EditText editTextEmail;
    @BindView(R.id.mdp)
    EditText editTextMdp;
    @BindView(R.id.confirmMDP)
    EditText editTextConfirmMdp;
    @BindView(R.id.phone)
    EditText editTextPhone;
    @BindView(R.id.age)
    EditText editTextAge;
    @BindView(R.id.adresse)
    EditText editTextAdresse;

    @BindView(R.id.signUp)
    Button buttonSignup;
    @BindView(R.id.ChooseProfile)
    Button choose;
    private boolean isclicked = false;
    private final int REQUEST_CODE_GALLERY = 999;

    PatientService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genre_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.YesNo_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assuranceSpinner.setAdapter(adapter1);

        db=new GlobalDbHelper(this);
        ArrayList<String> listVilles=db.getAllVilles();
        ArrayAdapter<String> adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listVilles);
        villeSpinner.setAdapter(adapter2);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PatientSignupActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);
                isclicked = true;
            }
        });

    }


    @OnTextChanged({R.id.email,R.id.mdp})
    public void ValidationFields(){

        String emailInput=editTextEmail.getText().toString().trim();
        if(emailInput.isEmpty()){
            editTextEmail.setError("Field can't be empty");
           }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            editTextEmail.setError("Please enter a valid email address");
            buttonSignup.setEnabled(false);
        }
        else {
            editTextEmail.setError(null);
            buttonSignup.setEnabled(true);
        }

        String passwordInput = editTextMdp.getText().toString().trim();
        if (passwordInput.isEmpty()) {
            editTextMdp.setError("Field can't be empty");
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            editTextMdp.setError("Password too weak");
        } else {
            editTextMdp.setError(null);
        }

    }

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                   // "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
    Users u;
    Users u1;
    int  idPatient;
    Patient p;

    @OnClick(R.id.signUp)
    public void signUp(){
       String genre= genreSpinner.getSelectedItem().toString();
        String assurance= assuranceSpinner.getSelectedItem().toString();
        String ville= villeSpinner.getSelectedItem().toString();
        String prenom=editTextPrenom.getText().toString();
        String nom=editTextNom.getText().toString();
        String email=editTextEmail.getText().toString();
        String mdp=editTextMdp.getText().toString();
        String confirmMdp=editTextConfirmMdp.getText().toString();
        String phone=editTextPhone.getText().toString();
        String age=editTextAge.getText().toString();
        String adresse=editTextAdresse.getText().toString();

        ////////// START MYSQL
        int x=0;
        byte[] imgprofile = imageViewToByte(imgpro);

        u=new Users();
        u.setNomUser(nom);
        u.setPrenomUser(prenom);
        u.setGenreUser(genre);
        u.setTelephoneUser(phone);
        u.setImageUser(imgprofile);
        Integer IDville=db.getIdVille(ville);
        Ville v=new Ville(IDville,ville);
        u.setIdVille(v);
        u.setEmailUser(email);
        u.setPasswordUser(mdp);
        u.setRoleUser("patient");
        p=new Patient();
        p.setAdresse(adresse);
        p.setAgePatient(age);
        p.setCnssPatient(assurance);

        ///////////FIN MYSQL
        Intent intent = new Intent(this, PatientLoginActivity.class);
        db = new GlobalDbHelper(this);
        sqLiteDatabase=db.getWritableDatabase();
        if(genre.equals("")||assurance.equals("")||ville.equals("")||prenom.equals("")||nom.equals("")||email.equals("")
                ||mdp.equals("")||confirmMdp.equals("")||phone.equals("")||age.equals("")||adresse.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Fiels are empty or image not set",Toast.LENGTH_SHORT).show();
        }
        else {
            Integer Idville=db.getIdVille(ville);
            Boolean checkEmail = db.checkEmail(email);
            if (checkEmail == true) {
                if (mdp.equals(confirmMdp)) {
                    try {
                        byte[] imgprofileval = imageViewToByte(imgpro);
                        sqLiteDatabase.beginTransaction();
                        Boolean insert = db.insertUser(imgprofileval,nom, prenom, genre, phone, Idville, email, mdp, "patient");
                        int idUser = db.getIdUser(email);
                        Boolean insert2 = db.insertPatient(idUser, age, adresse, assurance);
                        addUser(u);
                        //|| insert2 ==true
                        if (insert == true && insert2 == true) {
                            sqLiteDatabase.setTransactionSuccessful();
                            Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    } finally {
                        sqLiteDatabase.endTransaction();
                        db.close();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"mdp and confimMDP not equal",Toast.LENGTH_SHORT).show();
                }
            }
            else { Toast.makeText(getApplicationContext(),"Email already exists",Toast.LENGTH_SHORT).show();}
        }
    }
    public boolean addUser(Users u){
        service= Apis.getPatientsService();
        Call<Users> call=service.addUser(u);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Toast.makeText(getApplicationContext(), "yesADD ", Toast.LENGTH_SHORT).show();
                idPatient=getIdPatient(u.getEmailUser());
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getApplicationContext(), " User Added Mysql ", Toast.LENGTH_SHORT).show();
                idPatient=getIdPatient(u.getEmailUser()); //FCT DE RECUPERATION DE ID USER
                Log.e("Error:",t.getMessage());

            }
        });
        return true;
    }
    public void addPatient(Patient p){
        service= Apis.getPatientsService();
        Call<Patient> call=service.ajoutPatient(p);
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                Toast.makeText(getApplicationContext(), "yesADDPatient ", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Patient Added Mysql ", Toast.LENGTH_SHORT).show();

                Log.e("Error:",t.getMessage());
            }
        });
    }
    int idX;
    public int getIdPatient(String emailUser){
        service= Apis.getPatientsService();
        Call<List<Users>>call = service.getIdPatient(emailUser);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>>call, Response<List<Users>>response) {
                List<Users>uList=response.body();
                for(Users u : uList ){
                    idX=u.getIdUser();
                }
                //Toast.makeText(getApplicationContext(), "getid Yes "+idX, Toast.LENGTH_SHORT).show();
                u1=new Users(idX);
                p.setIdUser(u1);
                addPatient(p);   ////FCT INSERTION PATIENT
            }
            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
               // Toast.makeText(getApplicationContext(), "getidNo "+idX, Toast.LENGTH_SHORT).show();
                Log.e("Error:",t.getMessage());
            }
        });
    return idX;
    }

    @OnClick(R.id.ChooseProfile)
    public void choose(){

    }


    private byte[] imageViewToByte(ImageView imgpro) {
        Bitmap bitmap = ((BitmapDrawable)imgpro.getDrawable()).getBitmap();
        Bitmap bitmapreduced = reduceBitmapSize(bitmap,240000);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapreduced.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public static Bitmap reduceBitmapSize(Bitmap bitmap,int MAX_SIZE) {
        double ratioSquare;
        int bitmapHeight, bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSquare = (bitmapHeight * bitmapWidth) / MAX_SIZE;
        if (ratioSquare <= 1)
            return bitmap;
        double ratio = Math.sqrt(ratioSquare);
        Log.d("mylog", "Ratio: " + ratio);
        int requiredHeight = (int) Math.round(bitmapHeight / ratio);
        int requiredWidth = (int) Math.round(bitmapWidth / ratio);
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }else {
                Toast.makeText(this, "Vous n'avez pas la permission d'acceder", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgpro.setImageBitmap(bitmap);
            }catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.age)
    public void onclickage(){
        Calendar c= Calendar.getInstance();
        int day=c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);

        DatePickerDialog dpd=new DatePickerDialog(PatientSignupActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                editTextAge.setText(mDay+"/"+(mMonth+1)+"/"+mYear) ;

            }
        },day,month,year);
        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dpd.show();
    }

}
